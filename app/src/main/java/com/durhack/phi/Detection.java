package com.durhack.phi;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class Detection implements SensorEventListener{
    // TODO: 14/11/20 Seamus compass stuff
    private SensorManager sensorManager;
    private Sensor sensor;

    public Detection(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
    }

    // TODO: 14/11/2020 Write time loop for collecting data (APi for timing)
    //  Every x milliseconds record relative position and magnetic field
    //  Once collected n samples return data

//    public CollectData(){
//
//    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        float[] bField = event.values;
        // Do something with this sensor value.
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}

//public class SensorActivity extends Activity implements SensorEventListener {
//    private SensorManager sensorManager;
//    private Sensor mLight;
//
//    @Override
//    public final void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.main);
//
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//    }
//
//    @Override
//    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Do something here if sensor accuracy changes.
//    }
//
//    @Override
//    public final void onSensorChanged(SensorEvent event) {
//        // The light sensor returns a single value.
//        // Many sensors return 3 values, one for each axis.
//        float[] bField = event.values;
//        // Do something with this sensor value.
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        sensorManager.unregisterListener(this);
//    }
//}
