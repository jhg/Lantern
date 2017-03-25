package com.blogspot.blogdejhg.lantern;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Lantern extends AppCompatActivity {
    View background;
    Drawable originalBackground;
    private Camera camera;
    private int requestPermissionId = 29;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lantern);
        // Screen toggle button
        ToggleButton screen = (ToggleButton) findViewById(R.id.screenButton);
        screen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(background == null) {
                    background = findViewById(R.id.lanternLayout);
                    originalBackground = background.getBackground();
                }
                if (isChecked) {
                    background.setBackgroundColor(0xffffffff);
                } else {
                    background.setBackgroundDrawable(originalBackground);
                }
            }
        });
        // Flash toggle button
        ToggleButton lantern = (ToggleButton) findViewById(R.id.lanternButton);
        // Check flash feature
        if(!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            lantern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(), "Sorry, your device hasn't flash!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            // Check permissions
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    requestPermissionId);
            }
            lantern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked && camera == null) {
                        // Turn on
                        try {
                            camera = Camera.open();
                        } catch (RuntimeException ex) {
                            Toast.makeText(getApplicationContext(), "Can't access camera device", Toast.LENGTH_LONG).show();
                        }
                        if(camera != null) {
                            turnFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            camera.startPreview();
                        }
                    } else if (camera != null) {
                        // Turn off
                        turnFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                    }
                }
            });
        }
    }

    private void turnFlashMode(String mode) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(mode);
        camera.setParameters(parameters);
    }
}
