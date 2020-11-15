package com.durhack.phi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Detection implements SensorEventListener {
    private final int batchSize = 100;
    private final int samplingTimeMilliseconds = 3000;

    private SensorManager mSensorManager;

    private final int proximityType = Sensor.TYPE_PROXIMITY;
    private final int magnetometerType = Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;

    private Sensor mSensorProximity;
    private Sensor mSensorMagnetometer;

    private List<Float> positionBatch;
    private List<Float> magnetometerBatch;

    public Detection(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorProximity = mSensorManager.getDefaultSensor(proximityType);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(magnetometerType);
        positionBatch = new ArrayList<>();
        magnetometerBatch = new ArrayList<>();
    }

    public float[] collectBatch() {
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer, samplingTimeMilliseconds*1000/batchSize);
        }
        if (mSensorProximity != null) {
            mSensorManager.registerListener(this, mSensorProximity, samplingTimeMilliseconds*1000/batchSize);
        }

        final SensorEventListener listener = this;
        AtomicBoolean collectionStopped = new AtomicBoolean(false);
        new Handler().postDelayed(() -> {
            mSensorManager.unregisterListener(listener);
            collectionStopped.set(true);
        }, samplingTimeMilliseconds);

        while (!collectionStopped.get()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                // do nothing
            }
        }

        float[] batch = new float[6*batchSize];
        for (int i=0; i<3*batchSize; i++) {
            batch[i] = positionBatch.get(i);
            batch[i+3*batchSize] = magnetometerBatch.get(i);
        }

        return batch;
    }

    public void unregisterAllListeners() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case proximityType:
                if (positionBatch.size() < 3*batchSize) {
                    positionBatch.add((float) 0);
                    positionBatch.add((float) 0);
                    positionBatch.add(event.values[0]);
                }
                break;
            case magnetometerType:
                if (magnetometerBatch.size() < 3*batchSize) {
                    magnetometerBatch.add(event.values[0]);
                    magnetometerBatch.add(event.values[1]);
                    magnetometerBatch.add(event.values[2]);
                }
                break;
            default:
                // do nothing
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}
