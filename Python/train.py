import numpy as np
import tensorflow as tf
from tensorflow.keras.applications.vgg16 import VGG16
from tensorflow.keras import layers, models
# from tensorflow.keras.applications.vgg16 import preprocess_input
# from tensorflow.keras.applications.resnet50 import preprocess_input
# from tensorflow.keras.applications.resnet_v2 import preprocess_input
# from tensorflow.keras.applications.inception_resnet_v2 import preprocess_input
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input
import tensorflow.lite as lite
import pickle as pkl
import matplotlib.pyplot as plt
import main

c = tf.compat.v1.ConfigProto()
c.gpu_options.allow_growth = True
# print(device_lib.list_local_devices())



def load(file):
    with open(file, "rb") as f:
        data = pkl.load(f)

    x = data[0]
    y = data[1]
    return x, y


if __name__ == "__main__":
    x, y = load("./preProc3.pkl")
    xT, yT = load("./preProc1.pkl")

    new_input = tf.keras.Input(shape=(x.shape[1]))
    model = tf.keras.Sequential(
        [
            new_input,
            layers.Dense(50, activation="relu", name="dense", kernel_initializer="normal"),
            layers.Dense(50, activation="relu", name="dense1", kernel_initializer="normal"),
            layers.Dense(50, activation="relu", name="dense2", kernel_initializer="normal"),
            layers.Dense(y.shape[1], activation="linear", name="dense3", kernel_initializer="normal"),
        ]
    )

    model.summary()
    model.compile(optimizer="adam",
                  loss="msle",
                  metrics=[tf.keras.metrics.MeanSquaredLogarithmicError(), "accuracy", "mae"])

    hist = model.fit(x, y, epochs=300, batch_size=32, validation_data=(xT, yT))
    plt.plot(hist.history["mae"])
    plt.show()




# new_input = tf.keras.Input(shape=(224, 224, 3))
# model = tf.keras.applications.MobileNetV2(
#     include_top=False,
#     weights="imagenet",
#     input_tensor=new_input,
#     pooling="avg",
# )
#
# model.summary()
# model = tf.keras.Sequential(
#     [
#         model,
#         layers.Dense(512, activation='relu'),
#         layers.Dropout(0.6),
#         layers.Dense(512, activation='relu'),
#         layers.Dropout(0.5),
#         layers.Dense(36, activation='softmax')
#     ]
# )
# model.layers[0].trainable = False
#
# model.summary()
# model.compile(optimizer="adam",
#               loss="sparse_categorical_crossentropy",
#               metrics=[tf.keras.metrics.SparseCategoricalAccuracy()])
#
# datagen = tf.keras.preprocessing.image.ImageDataGenerator(preprocessing_function=preprocess_input,
#                                                           horizontal_flip=True)
# train_data = datagen.flow_from_directory('Data_full/', class_mode='binary', target_size=(224, 224))
# verify_data = datagen.flow_from_directory('Verify_full/', class_mode='binary', target_size=(224, 224))
# print(train_data.class_indices)
# pkl.dump(train_data.class_indices, open("./classes_lite_1", "wb"))
# model.fit(train_data, epochs=20, batch_size=100, validation_data=verify_data)
# model.save('./trainedModels/lite_v2')