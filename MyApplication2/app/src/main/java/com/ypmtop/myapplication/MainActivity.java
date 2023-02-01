package com.ypmtop.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.ypmtop.myapplication.utils.usbMonitor;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private usbMonitor mUsbMonitor;
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.CAMERA,
    };
    private List<String> mMissPermissions = new ArrayList<>();
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isVersionM()) {
            checkAndRequestPermissions();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsbMonitor = new usbMonitor(getApplicationContext());
        mUsbMonitor.registerUsbReceiver(getApplicationContext());
    }

    private void checkAndRequestPermissions() {
        mMissPermissions.clear();
        for (String permission : REQUIRED_PERMISSION_LIST) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                mMissPermissions.add(permission);
            }
        }
        // check permissions has granted
        if (mMissPermissions.isEmpty()) {
        } else {
            ActivityCompat.requestPermissions(this,
                    mMissPermissions.toArray(new String[mMissPermissions.size()]),
                    REQUEST_CODE);
        }
    }
    private boolean isVersionM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mMissPermissions.remove(permissions[i]);
                }
            }
        }
        // Get permissions success or not
        if (mMissPermissions.isEmpty()) {
            //startMainActivity();
        } else {
            Toast.makeText(MainActivity.this, "get permissions failed,exiting...",Toast.LENGTH_SHORT).show();
            MainActivity.this.finish();
        }
    }
}