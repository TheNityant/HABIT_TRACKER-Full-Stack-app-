import argparse
import os
import tensorflow as tf
from tensorflow.keras import layers, models
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import numpy as np

# Best practices: callbacks for early stopping and checkpointing
from tensorflow.keras.callbacks import EarlyStopping, ModelCheckpoint, ReduceLROnPlateau

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(BASE_DIR, ".."))
DATASET_DIR = os.path.join(BASE_DIR, "CNN_CGR_DATASET_FIXED")  # Use properly-visible dataset
OUTPUT_DIR = os.path.join(PROJECT_ROOT, "V3_Model_Output")
os.makedirs(OUTPUT_DIR, exist_ok=True)

# --- Device Info ---
physical_devices = tf.config.list_physical_devices('GPU')
if physical_devices:
    print(f"\U0001F680 GPU(s) detected: {[d.name for d in physical_devices]}")
else:
    print("\U0001F4BB No GPU detected. Training will use CPU.\nIf you want to enable GPU acceleration in the future, use Python 3.10 and install tensorflow-directml or set up WSL2 with CUDA.")
    print("Training will proceed on CPU. This may take longer, but results will be identical.")

# --- CLI Args for batch size/epochs ---
parser = argparse.ArgumentParser(description='Train V3 CNN Model')
parser.add_argument('--epochs', type=int, default=35, help='Number of training epochs (default: 35)')
parser.add_argument('--batch_size', type=int, default=16, help='Batch size (default: 16)')
args, _ = parser.parse_known_args()

print("⚙️ Initializing HD Vision Brain Training...")


# Light augmentation helps the model focus on robust structural signal.
train_datagen = ImageDataGenerator(
    rescale=1. / 255,
    validation_split=0.2,
    horizontal_flip=True,
    vertical_flip=True,
    rotation_range=10,
)

val_datagen = ImageDataGenerator(rescale=1. / 255, validation_split=0.2)

train_gen = train_datagen.flow_from_directory(
    DATASET_DIR,
    target_size=(256, 256),
    batch_size=args.batch_size,
    class_mode='binary',
    subset='training',
    shuffle=True
)

val_gen = val_datagen.flow_from_directory(
    DATASET_DIR,
    target_size=(256, 256),
    batch_size=args.batch_size,
    class_mode='binary',
    subset='validation',
    shuffle=False
)

print(f"🧠 Class Mapping Locked: {train_gen.class_indices}")

# --- DEBUG: Print filenames and labels from generator ---
print("\nSample batch from training generator:")
batch_x, batch_y = next(train_gen)
for i in range(len(batch_x)):
    if hasattr(train_gen, 'filepaths'):
        fname = os.path.basename(train_gen.filepaths[i])
    else:
        fname = f"img_{i}"
    print(f"  {fname} | Label: {batch_y[i]}")


resistant_count = sum(1 for cls in train_gen.classes if cls == 0)
susceptible_count = sum(1 for cls in train_gen.classes if cls == 1)
total_train = max(1, resistant_count + susceptible_count)

class_weight = {
    0: total_train / max(1, 2 * resistant_count),
    1: total_train / max(1, 2 * susceptible_count),
}

print(f"\n📊 Training class counts: RESISTANT={resistant_count}, SUSCEPTIBLE={susceptible_count}")
print(f"⚖️ Dynamic class weights: {class_weight}")

# --- CGR-specific CNN (better fit for sparse/fractal synthetic images) ---
model = models.Sequential([
    layers.Input(shape=(256, 256, 3)),
    layers.Conv2D(32, (3, 3), activation='relu', padding='same'),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(64, (3, 3), activation='relu', padding='same'),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(128, (3, 3), activation='relu', padding='same'),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(128, (3, 3), activation='relu', padding='same'),
    layers.MaxPooling2D((2, 2)),
    layers.GlobalAveragePooling2D(),
    layers.Dense(128, activation='relu'),
    layers.Dropout(0.35),
    layers.Dense(1, activation='sigmoid')
])
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=1e-3),
    loss='binary_crossentropy',
    metrics=['accuracy', tf.keras.metrics.AUC(name='auc')]
)

# Add callbacks for best results
early_stop = EarlyStopping(monitor='val_loss', patience=8, restore_best_weights=True)
reduce_lr = ReduceLROnPlateau(monitor='val_loss', factor=0.4, patience=3, min_lr=1e-5, verbose=1)
checkpoint_path = os.path.join(OUTPUT_DIR, "best_v3_vision_model.h5")
checkpoint = ModelCheckpoint(checkpoint_path, monitor='val_loss', save_best_only=True)

print("🚀 Training HD Model with best practices...")
history = model.fit(
    train_gen,
    validation_data=val_gen,
    epochs=args.epochs,
    callbacks=[early_stop, reduce_lr, checkpoint],
    class_weight=class_weight,
    verbose=1
)

best_val_acc = float(np.max(history.history.get('val_accuracy', [0.0])))
best_val_auc = float(np.max(history.history.get('val_auc', [0.0])))
print(f"📈 Best validation accuracy: {best_val_acc:.4f}")
print(f"📈 Best validation AUC: {best_val_auc:.4f}")

# Save final model as well
model_path = os.path.join(OUTPUT_DIR, "v3_vision_model.h5")
model.save(model_path)
print(f"✅ CORE FIXED: V3 HD Vision Brain saved to {model_path}")
print(f"🏆 Best model (lowest val_loss) saved to {checkpoint_path}")

# NOTE: For GPU acceleration on Windows, use TensorFlow-DirectML or WSL2 with CUDA. Native CUDA is not supported after TF 2.10.
# See: https://learn.microsoft.com/en-us/windows/ai/directml/gpu-tensorflow-windows