"""
V3 Automated Batch Testing Script
--------------------------------------
Runs the trained V3 CNN model on all .fna files in a specified folder,
logs predictions, and summarizes results for quality assurance.

Usage:
    python V3_Automated_Batch_Test.py <fna_folder>

Requirements:
    - V3_Model_Output/v3_vision_model.h5 (256x256 model)
    - keras, tensorflow, numpy, pandas, matplotlib
"""
import os
import numpy as np
import pandas as pd
import tensorflow as tf
import tempfile
from keras.utils import load_img, img_to_array

# --- CONFIG ---
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(BASE_DIR, '..'))
MODEL_PATH = os.path.abspath(os.path.join(PROJECT_ROOT, 'V3_Model_Output', 'v3_vision_model.h5'))
FNA_DIR = os.path.abspath(os.path.join(BASE_DIR, '..', 'bacterial_dna'))  # Default folder

# --- LOAD MODEL ---
model = tf.keras.models.load_model(MODEL_PATH)

# --- BATCH TEST FUNCTION ---
def extract_sequence(fna_path):
    max_bases = 50000
    chunks = []
    current_len = 0
    with open(fna_path, 'r', encoding='utf-8', errors='ignore') as f:
        for line in f:
            if line.startswith(">"):
                continue
            seq_line = line.strip().upper()
            if not seq_line:
                continue
            needed = max_bases - current_len
            if needed <= 0:
                break
            if len(seq_line) > needed:
                seq_line = seq_line[:needed]
            chunks.append(seq_line)
            current_len += len(seq_line)
    return "".join(chunks)

from _UTILS.cgr_utils import generate_cgr_image

def predict_fna(fna_path):
    seq = extract_sequence(fna_path)
    temp_img = None
    with tempfile.NamedTemporaryFile(delete=False, suffix=".png", dir=BASE_DIR) as tmp:
        temp_img = tmp.name

    try:
        generate_cgr_image(seq, out_path=temp_img, size=256, dpi=100, color='cyan', bg='black')
        img = load_img(temp_img, target_size=(256, 256))
        img_array = img_to_array(img) / 255.0
        img_array = np.expand_dims(img_array, axis=0)
        prob = model.predict(img_array, verbose=0)[0][0]
    finally:
        if temp_img and os.path.exists(temp_img):
            os.remove(temp_img)

    label = 'RESISTANT' if prob < 0.5 else 'SUSCEPTIBLE'
    confidence = (1 - prob) * 100 if prob < 0.5 else prob * 100
    return label, confidence, prob

def main():
    import sys
    folder = FNA_DIR
    if len(sys.argv) == 2:
        folder = sys.argv[1]
    fna_files = [os.path.join(folder, f) for f in os.listdir(folder) if f.endswith('.fna')]
    results = []
    for fna in fna_files:
        label, confidence, prob = predict_fna(fna)
        results.append({'File': os.path.basename(fna), 'Prediction': label, 'Confidence': confidence, 'RawProb': prob})
        print(f"{os.path.basename(fna)}: {label} ({confidence:.2f}%)")
    df = pd.DataFrame(results)
    out_csv = os.path.join(BASE_DIR, 'V3_Batch_Test_Results.csv')
    df.to_csv(out_csv, index=False)
    print(f"\nBatch test complete. Results saved to {out_csv}")
    print(df['Prediction'].value_counts())

if __name__ == "__main__":
    main()
