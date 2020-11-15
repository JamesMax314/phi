package com.durhack.phi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.media.Image;
import android.nfc.Tag;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "Main";

    FragmentManager fragmentManager;
//    private Session session;
//    private boolean installRequested;
//    private GLSurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        surfaceView = findViewById(R.id.surfaceview);
//        surfaceView.setOnTouchListener(this);

//        installRequested = false;

//        try {
//            switch (ArCoreApk.getInstance().requestInstall(this, true)) {
//                case INSTALLED:
//                    Log.i(TAG, "arCore Installed");
//                    break;
//                case INSTALL_REQUESTED:
//                    Log.i(TAG, "arCore Not Installed");
//                    break;
//            }
//        } catch (UnavailableUserDeclinedInstallationException e) {
//            Toast.makeText(this, "TODO: handle exception " + e, Toast.LENGTH_LONG)
//                    .show();
//            return;
//        } catch (UnavailableDeviceNotCompatibleException e) {
//            e.printStackTrace();
//        }
//
//        if (session == null) {
//            Exception exception = null;
//            String message = null;
//            try {
//                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
//                    case INSTALL_REQUESTED:
//                        installRequested = true;
//                        return;
//                    case INSTALLED:
//                        break;
//                }
//                // Create the session.
//                session = new Session(/* context= */ this);
//            } catch (UnavailableArcoreNotInstalledException
//                    | UnavailableUserDeclinedInstallationException e) {
//                message = "Please install ARCore";
//                exception = e;
//            } catch (UnavailableApkTooOldException e) {
//                message = "Please update ARCore";
//                exception = e;
//            } catch (UnavailableSdkTooOldException e) {
//                message = "Please update this app";
//                exception = e;
//            } catch (UnavailableDeviceNotCompatibleException e) {
//                message = "This device does not support AR";
//                exception = e;
//            } catch (Exception e) {
//                message = "Failed to create AR session";
//                exception = e;
//            }
//        }
//        boolean isDepthSupported = session.isDepthModeSupported(Config.DepthMode.AUTOMATIC);
//        Config config = new Config(session);
//        if (isDepthSupported) {
//            config.setDepthMode(Config.DepthMode.AUTOMATIC);
//        }
//        session.configure(config);
//        try {
//            session.resume();
//        } catch (CameraNotAvailableException e) {
//            e.printStackTrace();
//        }
////        surfaceView.onResume();
//
////        session.setCameraTextureName(0);
//
//        Frame frame;
//        try {
//            frame = session.update();
//        } catch (CameraNotAvailableException e) {
//            Log.e(TAG, "Camera not available during onDrawFrame", e);
//            return;
//        }
//
//        try {
//            Image depthImage = frame.acquireDepthImage();
//        } catch (NotYetAvailableException e) {
//            e.printStackTrace();
//        }
//
//
        fragmentManager = getSupportFragmentManager();
//        CameraFragment cameraFragment = new CameraFragment();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, new CameraFragment(), "camFrag")
                    .commit();
        }

//        assert savedInstanceState != null;
//        ((CameraFragment) Objects.requireNonNull(fragmentManager.getFragment(savedInstanceState, "camFrag"))).drawVectors();
//        cameraFragment.drawVectors();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}

