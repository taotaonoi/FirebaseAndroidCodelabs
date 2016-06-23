package com.akexorcist.myapplication.manager;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Akexorcist on 6/24/2016 AD.
 */

public class VibrationManager {
    public static void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);
    }
}
