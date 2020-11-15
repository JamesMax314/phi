import sys

import tensorflow as tf
# from tensorflow.keras.applications.resnet50 import preprocess_input
# from tensorflow.keras.applications.inception_resnet_v2 import preprocess_input
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input
from tensorflow.keras import layers
import pickle as pkl

def convert(dir):
    print(dir)
    model = tf.keras.models.load_model(dir)
    model.build(input_shape=600)
    model.summary()
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tfLiteModel = converter.convert()
    return tfLiteModel

def save_lite(model, dir):
    with tf.io.gfile.GFile(dir, "wb") as f:
        f.write(model)

if __name__ == "__main__":
    modelDir = "./model"
    liteDir = "./liteModel"
    modelFile = modelDir
    liteFile = liteDir + ".tflite"
    mTfLiteModel = convert(modelFile)
    save_lite(mTfLiteModel, liteFile)
