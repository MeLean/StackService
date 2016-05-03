package com.meline.stackservice;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    public SharedPreferencesUtils(Context context, String name) {
        this.sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        this.spEditor = this.sharedPreferences.edit();
    }

    public void putStringInSharedPreferences(String key, String value) {
        spEditor.putString(key, value);
        spEditor.commit();
    }

    public void putIntInSharedPreferences(String key, int value) {
        spEditor.putInt(key, value);
        spEditor.commit();
    }

    public void putBooleanInSharedPreferences(String key, boolean value) {
        spEditor.putBoolean(key, value);
        spEditor.commit();
    }

    public String getStringFromSharedPreferences(String key) {
        return sharedPreferences.getString(key, null);
    }

    public int getIntFromSharedPreferences(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public boolean getBooleanFromSharedPreferences(String key) {
        boolean result = sharedPreferences.getBoolean(key, false);
        return result;
    }
}

