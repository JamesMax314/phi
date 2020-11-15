package com.durhack.phi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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

import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.NotYetAvailableException;
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

        try {
            detection = new Detection(context);
        } catch (NotYetAvailableException | CameraNotAvailableException e) {
            e.printStackTrace();
        }

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
//                float[] B = new float[2];
//                B[0] = 8;
//                B[1] = -14;
//                drawVectors(10,-8, B);
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

    class regressionRunnable implements Runnable{
        float[] input;
        Regression regression;
        regressionRunnable(float[] in, Regression reg){
            this.input = in;
            this.regression = reg;
        }

        @Override
        public void run() {
            float[] output = this.regression.process(input);
//            int a = 1;
            // Posts message to main thread
            mainHandler.post(() -> sampleField(output));
        }
    }

    void sampleField(float[] output){
        MainActivity context = (MainActivity) requireActivity();
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        ImageView imageView = (ImageView) context.findViewById(R.id.imageView1);
        Canvas canvas = new Canvas(bitmap);

        float scale = output[2];
        float mu = 1;

        float[] xs = new float[20];
        float[] ys = new float[20];
//        float[] Bs = new float[20*20][];

        for (int i=0; i<20; i++){
            xs[i] = (i-10) / scale;
            for (int j=0; j<20; j++) {
                ys[j] = (float) ((j - 10) / scale * 1.5);
                float[] Bs = calcB(mu, xs[i], ys[j], 0);
                drawVectors(canvas, xs[i], ys[j], Bs);
            }
        }

        imageView.setImageBitmap(bitmap);
    }

    void saveBatch(float[] input){
        inputBatch = input;
        try {
            Regression regression = new Regression(activity, Regression.Device.CPU, 4);
            regressionRunnable runnable = new regressionRunnable(inputBatch, regression);
            new Thread(runnable).start();
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

    float[] calcB(float mu, float x,float y, float z){
       float r = (float) Math.sqrt(x*x + y*y + z*z);
        float   phi = (float) Math.atan(y/x);
        float   theta =(float) Math.acos(z/r);
        float[] B = new float[2];
//        B[2] = 3*mu/(r**3) * -Math.sin(theta) - 1/3;
        B[0] = (float) (3*mu/(r*r*r)*Math.cos(phi)*Math.sin(theta) * Math.cos(theta));
        B[1] = (float) (3*mu/(r*r*r)*Math.sin(phi)*Math.sin(theta) * Math.cos(theta));
        return B;
    }


    void drawVectors(Canvas canvas, float x,float y,float[] B){
//        float s = (float) Math.sin(t);
//        float c = (float) Math.cos(t);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x+B[0], y+B[1]);
        path.lineTo((x+B[0])/3, (y+B[1])/3);
        path.lineTo(x+B[0]+4, y+B[1]+4);
//        path.lineTo(x+5*s, y+10*s);
//        path.lineTo(x-10*s, y+2*s);
//        path.close();
        path.offset(20, 40);
        canvas.drawPath(path, paint);
        path.offset(60, 100);
        canvas.drawPath(path, paint);
// offset is cumlative
// next draw displaces 50,100 from previous
        path.offset(60, 100);
        canvas.drawPath(path, paint);
    }
}
