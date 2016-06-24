package com.akexorcist.myapplication.manager;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Akexorcist on 6/24/2016 AD.
 */

public class VibrationManager {
    public static void vibrate(Context context) {
        if (VibrationManager.hasVibrator(context)) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(300);
        }
    }

    public static boolean hasVibrator(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        return vibrator.hasVibrator();
    }
}
