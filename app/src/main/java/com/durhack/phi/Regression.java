package com.durhack.phi;

import android.app.Activity;
import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.TensorFlowLite;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.nnapi.NnApiDelegate;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;

public class Regression {

    protected Interpreter tflite;
    /** Model for categorising */
    private MappedByteBuffer tfliteModel;
    private List<String> labels;
    /** Optional GPU delegate for accleration. */
    private GpuDelegate gpuDelegate = null;
    /** Options for configuring the Interpreter. */
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    /** Input size along the x axis. */
    private final int inputSize;

    /** Input image TensorBuffer. */
    private TensorBuffer inputBuffer;

    /** Output probability TensorBuffer. */
    private final TensorBuffer outputBuffer;

    /** Processer to apply post processing of the output probability. */
//    private final TensorProcessor probabilityProcessor;

    public enum Device {
        CPU,
        NNAPI,
        GPU
    }

    protected Regression(Activity activity, Device device, int numThreads) throws IOException {
        tfliteModel = FileUtil.loadMappedFile(activity, getModelPath());
//        labels = FileUtil.loadLabels(activity, getLabelPath());

        switch (device) {
            case NNAPI:
                NnApiDelegate nnApiDelegate = new NnApiDelegate();
                tfliteOptions.addDelegate(nnApiDelegate);
                break;
            case GPU:
                gpuDelegate = new GpuDelegate();
                tfliteOptions.addDelegate(gpuDelegate);
                break;
            case CPU:
                break;
        }
        tfliteOptions.setNumThreads(numThreads);
        tflite = new Interpreter(tfliteModel, tfliteOptions);

        // Reads type and shape of input and output tensors, respectively.
        // The input tensor is in position 0 in tflite.getInputTensor.
        int inputTensorIndex = 0;
        int[] inputShape = tflite.getInputTensor(inputTensorIndex).shape(); // {1, height, width, 3}
        inputSize = inputShape[1];
        DataType inputDataType = tflite.getInputTensor(inputTensorIndex).dataType();
        int probabilityTensorIndex = 0;
        int[] outputShape = tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
        DataType outputDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        // Creates the input tensor.
//        inputBuffer = TensorBuffer.createFixedSize(inputShape, inputDataType);

        // Creates the output tensor and its processor.
        outputBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType);

        // Creates the post processor for the output probability.
//        probabilityProcessor = new TensorProcessor.Builder().build();
    }

    public void close() {
        tflite.close();
        tflite = null;
    }

    public float[] process(final float[] floats) {
//        float[] floats = new float[600];
//        inputBuffer = ;
        tflite.run(floats, outputBuffer.getBuffer().rewind());
        float[] outArr = outputBuffer.getFloatArray();
        return outArr;
    }

    protected String getModelPath() {
        return "liteModel.tflite";
    }
}
