package com.blkxltng.caip;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceSettings {

    public final static String PREFS_NAME = "CAIP_prefs";
    public final static String PREF_VERSION_CODE_KEY = "version_code";
    public final static String PREF_SECURITY_PIN_KEY = "security_pin";

    static void saveToPref(Context context, String str) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("code", str);
        editor.commit();
    }

    static String getCode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String defaultValue = "";
        return sharedPref.getString("code", defaultValue);
    }
}
