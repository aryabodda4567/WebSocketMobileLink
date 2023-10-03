package com.android.controller.functionalities;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class VolumeController {
    Context context;


    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;

    public VolumeController(Context context) {
        this.context = context;
    }

    // Method to mute the device volume
    public void mute() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_RING, true);
    }

    // Method to unmute the device volume
    public void unmute() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_RING, false);
    }

    // Method to set the device to ring mode
    public void setToRingMode() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    // Method to decrease the volume by 1
    public void lowerVolume() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, 1);
    }

    // Method to increase the volume by 1
    public void raiseVolume() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, 1);
    }


    public String audioManager(String command) throws JSONException {
        String[] words = command.split(" ");
        String option = words[1];
        JSONObject jsonObject = new JSONObject();

        if (option.equals("INCREASE_VOLUME")) {
            raiseVolume();
            jsonObject.put("ACK", "Volume raised");

        } else if (option.equals("DECREASE_VOLUME")) {
            lowerVolume();
            jsonObject.put("ACK", "Volume lowered");
        } else if (option.equals("RING")) {
            setToRingMode();
            RingtonePlayer ringtonePlayer = new RingtonePlayer(context);
            jsonObject.put("ACK", "Ring");
        } else if (option.equals("UNMUTE")) {
            unmute();
            jsonObject.put("ACK", "Un muted");
        } else if (option.equals("MUTE")) {
            mute();
            jsonObject.put("ACK", "Muted");
        } else {
            jsonObject.put("Error", "Wrong option");
        }
        return jsonObject.toString();
    }


}
