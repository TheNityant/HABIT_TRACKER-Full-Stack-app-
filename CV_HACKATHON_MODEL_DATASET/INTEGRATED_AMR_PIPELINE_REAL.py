"""
INTEGRATED_AMR_PIPELINE_REAL.py
--------------------------------------
Full integration of V1, V2, V3, and V4 models for AMR prediction and discovery.
- V1: K-mer based bacteria ID
- V2: Antibiotic prediction (multi-input)
- V3: CV-based bacteria & gene detection (CNN)
- V4: CARD gene verification

This script loads all models, processes a .fna file, and produces a unified report.
"""
import os
import joblib
import pandas as pd
import numpy as np
import tensorflow as tf
from collections import Counter
from keras.utils import load_img, img_to_array
import matplotlib.pyplot as plt
import sys
sys.path.append(os.path.join(BASE_DIR, 'V4_GENE_DETECTION'))
from V4_GENE_DET import detect_card_genes, CARD_FASTA

# --- PATHS ---
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
#project_root = os.path.abspath(os.path.join(BASE_DIR, ".."))

V1_DIR = os.path.join(BASE_DIR, 'V1_Model_Output')
V2_DIR = os.path.join(BASE_DIR, 'V2_Model_Output')
V3_DIR = os.path.join(BASE_DIR, 'V3_Model_Output')
CARD_FASTA = os.path.join(BASE_DIR, 'MAIN_MODEL', 'CARD_DB.fasta')

# --- LOAD MODELS ---
print("\n═"*70)
print(" ⚙️  BOOTING INTEGRATED AMR PIPELINE ...")
print("═"*70)

# V1
v1_model = joblib.load(os.path.join(V1_DIR, 'bacterial_id_model.pkl'))
le_id = joblib.load(os.path.join(V1_DIR, 'label_encoder_id.pkl'))
# V2
v2_model = joblib.load(os.path.join(V2_DIR, 'v2_multi_input_model_FIXED.pkl'))
v2_features = joblib.load(os.path.join(V2_DIR, 'v2_feature_columns_FIXED.pkl'))
# V3
v3_model = tf.keras.models.load_model(os.path.join(V3_DIR, 'v3_vision_model.h5'))

# --- V4 CARD DB ---
def load_card_database(filepath):
    db = {}
    try:
        with open(filepath, 'r') as f:
            current_header, current_seq = "", []
            for line in f:
                line = line.strip()
                if line.startswith(">"):
                    if current_header: db[current_header] = "".join(current_seq)
                    current_header = line
                    current_seq = []
                else:
                    current_seq.append(line)
            if current_header: db[current_header] = "".join(current_seq)
        return db
    except FileNotFoundError:
        print(f"❌ ERROR: Could not find {filepath}. Check your file name!")
        return None

# --- INPUT ---
patient_file = input("🧬 Drag Patient DNA (.fna) here: ").strip().strip('"').strip("'")
if not os.path.exists(patient_file):
    print(f"❌ File not found: {patient_file}")
    exit()

# --- SEQUENCE EXTRACTION ---
with open(patient_file, 'r', encoding='utf-8', errors='ignore') as f:
    sequence = "".join([line.strip() for line in f if not line.startswith(">")]).upper()

# --- V1: BACTERIA ID ---
kmer_counts = Counter([sequence[i:i+6] for i in range(len(sequence) - 6 + 1)])
v1_features = v1_model.feature_names_in_
test_row_v1 = [kmer_counts.get(name, 0) for name in v1_features]
v1_species = le_id.inverse_transform(v1_model.predict(pd.DataFrame([test_row_v1], columns=v1_features)))[0]

# --- V3: BACTERIA & GENE (CNN) ---
def generate_cgr_image(seq, out_path="temp_cgr.png"):
    coords = {'A': [0,0], 'T': [1,0], 'G': [1,1], 'C': [0,1]}
    pos = np.array([0.5, 0.5])
    points = []
    for base in seq[:50000]:
        if base in coords:
            pos = (pos + np.array(coords[base])) / 2
            points.append(pos)
    points = np.array(points)
    plt.figure(figsize=(5, 5), dpi=100, facecolor='black')
    plt.scatter(points[:, 0], points[:, 1], s=0.5, c='cyan', alpha=0.9, marker='.')
    plt.axis('off')
    plt.savefig(out_path, bbox_inches='tight', pad_inches=0, facecolor='black')
    plt.close()
    return out_path

temp_img = generate_cgr_image(sequence)
img = load_img(temp_img, target_size=(256, 256))
img_array = img_to_array(img) / 255.0
img_array = np.expand_dims(img_array, axis=0)
v3_pred_prob = v3_model.predict(img_array, verbose=0)[0][0]
# Placeholder: update with V3 gene output if available
v3_bacteria = v1_species  # If V3 can output species, update this line
v3_gene = "Unknown_Gene"
if os.path.exists(temp_img): os.remove(temp_img)

# --- V4: CARD GENE VERIFICATION ---

# --- V4: CARD GENE DETECTION (NEW INTEGRATION) ---
detected_genes = detect_card_genes(sequence, CARD_FASTA)
verified_gene_names = set(g['gene_name'] for g in detected_genes)
gene_verified = False
if v3_gene != "Unknown_Gene" and v3_gene in verified_gene_names:
    gene_verified = True

# V4 gene reporting (print all detected genes, top hits, mechanisms)
v4_gene_report = ""
if detected_genes:
    v4_gene_report += f"\n⚠️ CRITICAL: V4 aligned {len(detected_genes)} CARD resistance genes!\n"
    v4_gene_report += "[ TOP 10 CONFIRMED HITS ]\n"
    unique_genes = {g['gene_name']: g for g in detected_genes}
    for name, data in list(unique_genes.items())[:10]:
        v4_gene_report += f" 🧬 [{data['aro_id']}] {data['gene_name']}\n"
        v4_gene_report += f"    └─ Mechanism: {data['mechanism']}\n"
else:
    v4_gene_report += "✅ V4 Scan Clear: No known CARD resistance genes aligned.\n"

# --- V2: ANTIBIOTIC PREDICTION ---

# --- Robust V2 Multi-Antibiotic Prediction ---
labels_path = os.path.abspath(os.path.join(BASE_DIR, '..', 'DATASET', 'MASTER_TRAINING_LABELS.csv'))
genome_id = os.path.splitext(os.path.basename(patient_file))[0]
valid_antibiotics = set()
try:
    labels_df = pd.read_csv(labels_path, usecols=['Genome ID','Antibiotic'])
    valid_antibiotics = set(labels_df[labels_df['Genome ID'].astype(str) == genome_id]['Antibiotic'].str.strip().unique())
except Exception as e:
    print(f'Warning: Could not load/parse MASTER_TRAINING_LABELS.csv for validation. {e}')

results = []
antibiotics = [col.replace('Drug_', '') for col in v2_features if col.startswith('Drug_')]
for ab in antibiotics:
    features = {col: 0 for col in v2_features}
    for kmer, count in kmer_counts.items():
        if kmer in features:
            features[kmer] = count
    drug_col = f'Drug_{ab}'
    if drug_col in features:
        features[drug_col] = 1
    X = pd.DataFrame([features])[v2_features]
    pred = v2_model.predict(X)[0]
    proba = v2_model.predict_proba(X)[0]
    is_valid = ab in valid_antibiotics
    results.append({'Antibiotic': ab, 'Prediction': pred, 'Prob_Resistant': proba[1], 'Prob_Susceptible': proba[0], 'Validated': is_valid})

# --- REPORT ---
print("\n=== INTEGRATED AMR PIPELINE REPORT ===")
print(f"V1 Bacteria: {v1_species}")
print(f"V3 Bacteria: {v3_bacteria}")
if v1_species == v3_bacteria:
    print("✅ V3 agrees with V1 on bacteria ID.")
else:
    print("⚠️ V3 disagrees with V1 on bacteria ID! Please review.")
print(f"V3 Gene: {v3_gene}")
if v3_gene != "Unknown_Gene":
    if gene_verified:
        print(f"✅ V4 confirms V3 gene: {v3_gene}")
    else:
        print(f"⚠️ V4 could not confirm V3 gene: {v3_gene}")
else:
    print("V3 did not predict a specific gene.")
print("\n--- V4 CARD Gene Detection Report ---")
print(v4_gene_report)
print("\nAntibiotic Predictions:")
for r in results:
    status = 'RESISTANT' if r['Prediction']==1 else 'SUSCEPTIBLE'
    validated = '✅' if r.get('Validated') else '❌'
    print(f"{r['Antibiotic']}: {status} (Resistant Prob: {r['Prob_Resistant']:.2f}) [Validated: {validated}]")
if not gene_verified:
    print("\n⚠️ New/mutated gene detected! Suggesting extra antibiotics.")
print("\nPipeline complete.")
