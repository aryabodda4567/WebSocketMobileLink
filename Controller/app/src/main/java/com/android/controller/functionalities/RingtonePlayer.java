package com.android.controller.functionalities;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class RingtonePlayer {

    public static Ringtone ringtone;

    public RingtonePlayer(Context context) {
        playSystemRingtone(context);
    }

    public static void playSystemRingtone(Context context) {
        // Get the default system ringtone URI
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        if (defaultRingtoneUri != null) {
            // Initialize a Ringtone object with the default ringtone URI
            if (ringtone == null) {
                ringtone = RingtoneManager.getRingtone(context, defaultRingtoneUri);
            }

            // Play the ringtone
            if (!ringtone.isPlaying()) {
                ringtone.play();
            }
        }
    }

    public static void stopSystemRingtone() {
        // Stop the ringtone if it's playing
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }
}

