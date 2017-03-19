package com.blogspot.blogdejhg.lantern;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class Lantern extends AppCompatActivity {
    private Camera camera;
    private int requestPermissionId = 29;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lantern);
        if(!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            alertFinish("Warning", "Sorry, your device hasn't flash!");
        }else {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    requestPermissionId);
            }
            ToggleButton lantern = (ToggleButton) findViewById(R.id.lanternButton);
            lantern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked && camera == null) {
                        try {
                            camera = Camera.open();
                        } catch (RuntimeException ex) {
                            alertFinish("Camera Error", "Can't access camera device");
                        }
                        if(camera != null) {
                            turnFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            camera.startPreview();
                        }
                    } else if (camera != null) {
                        turnFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                    }
                }
            });
        }
    }

    private void alertFinish(String title, String message){
        new AlertDialog.Builder(Lantern.this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            })
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    private void turnFlashMode(String mode) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(mode);
        camera.setParameters(parameters);
    }
}
