package com.bet.gaopan.betpro.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gaopan on 2017/5/31.
 */

public class PreferenceUtil {
    private static SharedPreferences getDefault(final Context context) {
        return context.getSharedPreferences("app_setting", Context.MODE_PRIVATE);
    }
    private static void save(final SharedPreferences.Editor editor) {
        if (android.os.Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
    public static int getInt(final String key, final int defaultValue,
                             Context context) {
        final SharedPreferences prefs = getDefault(context);
        return prefs.getInt(key, defaultValue);
    }

    public static void putInt(final String key, final int value, Context context) {
        final SharedPreferences prefs = getDefault(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        save(editor);
    }

    public static long getLong(final String key, final long defaultValue,
                               Context context) {
        try {
            final SharedPreferences prefs = getDefault(context);
            return prefs.getLong(key, defaultValue);
        }catch (Exception e){
            return 0;
        }
    }

    public static void putLong(final String key, final long value,
                               Context context) {
        try {
            final SharedPreferences prefs = getDefault(context);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(key, value);
            save(editor);
        }catch (Exception e){}
    }

    public static String getString(final String key, final String defaultValue,
                                   Context context) {
        final SharedPreferences prefs = getDefault(context);
        return prefs.getString(key, defaultValue);
    }

    public static void putString(final String key, final String value,
                                 Context context) {
        final SharedPreferences prefs = getDefault(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        save(editor);
    }

    public static boolean getBoolean(final String key,
                                     final boolean defaultValue, Context context) {
        final SharedPreferences prefs = getDefault(context);
        return prefs.getBoolean(key, defaultValue);
    }

    public static void putBoolean(final String key, final boolean value,
                                  Context context) {
        final SharedPreferences prefs = getDefault(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        save(editor);
    }

    public static float getFloat(final String key, final float defaultValue,
                                 Context context) {
        final SharedPreferences prefs = getDefault(context);
        return prefs.getFloat(key, defaultValue);
    }

    public static void putFloat(final String key, final float value,
                                Context context) {
        final SharedPreferences prefs = getDefault(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        save(editor);
    }

}

