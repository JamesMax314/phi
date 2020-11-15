package com.durhack.phi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Detection implements SensorEventListener {
    private static final String TAG = "Detect";

    private final int batchSize = 100;

    private final SensorManager mSensorManager;

    private final int proximityType = Sensor.TYPE_PROXIMITY;
    private final int magnetometerType = Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;

    private final Sensor mSensorProximity;
    private final Sensor mSensorMagnetometer;

    private final List<Float> positionBatch;
    private final List<Float> magnetometerBatch;

    private final Context context;

    private Boolean posFin;
    private Boolean magFin;

    public Detection(Context mContext) throws NotYetAvailableException, CameraNotAvailableException {
        context = mContext;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorProximity = mSensorManager.getDefaultSensor(proximityType);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(magnetometerType);
//        mSensorManager.registerListener(this, mSensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        positionBatch = new ArrayList<>();
        magnetometerBatch = new ArrayList<>();

        // Check whether the user's device supports the Depth API.



//        Session session = null;
//        try {
//            session = new Session(context);
////            Frame frame = new Frame(session);
//
//        } catch (UnavailableArcoreNotInstalledException e) {
//            e.printStackTrace();
//        } catch (UnavailableApkTooOldException e) {
//            e.printStackTrace();
//        } catch (UnavailableSdkTooOldException e) {
//            e.printStackTrace();
//        } catch (UnavailableDeviceNotCompatibleException e) {
//            e.printStackTrace();
//        }
//        boolean isDepthSupported = session.isDepthModeSupported(Config.DepthMode.AUTOMATIC);
//        Config config = new Config(session);
//        if (isDepthSupported) {
//            config.setDepthMode(Config.DepthMode.AUTOMATIC);
//        }
//        session.configure(config);
//
//        Frame frame = session.update();
//
//        Image depthImage = frame.acquireDepthImage();
    }

    public float[] collectBatch() {
        int samplingTimeMilliseconds = 3000;

        posFin = false;
        magFin = false;

        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer, samplingTimeMilliseconds*1000/batchSize);
        }
        if (mSensorProximity != null) {
            mSensorManager.registerListener(this, mSensorProximity, samplingTimeMilliseconds*1000/batchSize);
        }

        final SensorEventListener listener = this;
        AtomicBoolean collectionStopped = new AtomicBoolean(false);
//        new Handler().postDelayed(() -> {
//            mSensorManager.unregisterListener(listener);
//            collectionStopped.set(true);
//        }, samplingTimeMilliseconds);

        while (!posFin || !magFin) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                // do nothing
            }
        }

        unregisterAllListeners();

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
                    Log.i(TAG, String.valueOf(event.values[0]));
                    positionBatch.add(event.values[0]);
                } else {
                    posFin = true;
                }
                break;
            case magnetometerType:
                if (magnetometerBatch.size() < 3*batchSize) {
                    magnetometerBatch.add(event.values[0]);
                    magnetometerBatch.add(event.values[1]);
                    magnetometerBatch.add(event.values[2]);
                    if (positionBatch.size() < 3*batchSize){
                        positionBatch.add((float) 0);
                        positionBatch.add((float) 0);
//                        Log.i(TAG, String.valueOf(event.values[0]));
                        positionBatch.add(1f);
                    } else {
                        posFin = true;
                    }
                } else {
                    magFin = true;
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


