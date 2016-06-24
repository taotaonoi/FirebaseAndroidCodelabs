package com.akexorcist.myapplication.manager;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Akexorcist on 6/20/2016 AD.
 */

public class EventTrackerManager {
    public static void onLogout(Context context, String userEmail) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "logout");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, userEmail);
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
