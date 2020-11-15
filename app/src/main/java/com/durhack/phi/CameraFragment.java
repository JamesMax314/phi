package com.durhack.phi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CamFrag";

    PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private Button button;
    public Detection detection;
    public float[] inputBatch;

    private MainActivity activity;

    private final Handler mainHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);

        MainActivity context = (MainActivity) requireActivity();
        activity = context;

        previewView = view.findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(context));

        button = view.findViewById(R.id.button);
        button.setOnClickListener(this);

        detection = new Detection(context);

        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                Log.i(TAG, "onClick: cam_button");
                detectRunnable runnable = new detectRunnable();
                new Thread(runnable).start();
//                drawVectors();
//                float[] floatBatch = detection.collectBatch();
                drawVectors();
                break;
        }
    }

    class detectRunnable implements Runnable{
        detectRunnable(){
        }

        @Override
        public void run() {
            float[] input = detection.collectBatch();
            int a = 1;
            // Posts message to main thread
            mainHandler.post(() -> saveBatch(input));
        }
    }

    void saveBatch(float[] input){
        inputBatch = input;
        try {
            Regression regression = new Regression(activity, Regression.Device.CPU, 4);
            float[] position = regression.process(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        float[]
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) Objects.requireNonNull(getContext()), cameraSelector, preview);
    }

    void drawVectors(){
        MainActivity context = (MainActivity) requireActivity();
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        ImageView imageView = (ImageView) context.findViewById(R.id.imageView1);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(50, 50, 10, paint);
        imageView.setImageBitmap(bitmap);
    }
}
