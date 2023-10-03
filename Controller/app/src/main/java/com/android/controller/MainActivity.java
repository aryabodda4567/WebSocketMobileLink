package com.android.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.controller.functionalities.ContactAndCallLogUtil;
import com.android.controller.functionalities.DeviceInformationFetcher;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;


public class MainActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 1;
    public String webSocketURI;
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result ->
    {
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Connecting...");
//            builder.setMessage(result.getContents());
            webSocketURI = result.getContents();
            callConnection(webSocketURI);

            builder.setMessage("Close the QR code to connect the computer");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button controlButton = findViewById(R.id.connectButton);
        //ask permissions
        String[] permissions = {
                android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.SMS_FINANCIAL_TRANSACTIONS,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                android.Manifest.permission.MODIFY_AUDIO_SETTINGS, // Permission to set volume range
                Manifest.permission.CALL_PHONE // Permission to ring the phone
        };

        // Check if permissions are granted
        if (checkPermissions(permissions)) {
            // All permissions are already granted
            // You can perform your actions that require these permissions here
            Log.d("Permission", "All permissions are granted");
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }


        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to permission class

                // Call the qr scanner class
                scanCode();


            }
        });
    }

    private boolean checkPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // All permissions have been granted
                // You can proceed with your actions
                Log.d("Permission", "All permissions are granted");


            } else {
                // Some permissions were denied
                // Handle the case where permissions were not granted
                Log.d("Permission", "Some permissions were denied");

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);


    }

    private void callConnection(String webSocketURI) {
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        serviceIntent.putExtra("webSocketURI", webSocketURI);
        startService(serviceIntent);

    }


}