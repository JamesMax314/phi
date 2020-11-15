package com.durhack.phi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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