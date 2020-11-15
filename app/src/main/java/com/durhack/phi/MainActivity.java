package com.durhack.phi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Regression regression = new Regression(this, Regression.Device.CPU, 4);
//            regression.process();
        } catch (IOException e) {
            e.printStackTrace();
        }



        fragmentManager = getSupportFragmentManager();

        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, new CameraFragment())
                    .commit();
        }
    }
}