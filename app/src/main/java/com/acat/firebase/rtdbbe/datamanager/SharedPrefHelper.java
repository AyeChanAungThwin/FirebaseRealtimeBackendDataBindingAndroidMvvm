package com.acat.firebase.rtdbbe.datamanager;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {

    private String data = "SAVE_DATA";
    private String key = "KEY";
    private String value = "VALUE";
    private String firebaseChildrenPath = "FIREBASE_CHILDREN_PATH";

    private SharedPreferences mSharedPreferences;

    public SharedPrefHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(data, Context.MODE_PRIVATE);
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public void putKey(String key) {
        mSharedPreferences.edit().putString(this.key, key).apply();
    }

    public String getKey() {
        return mSharedPreferences.getString(this.key, null);
    }

    public void putValue(String value) {
        mSharedPreferences.edit().putString(this.value, value).apply();
    }

    public String getValue() {
        return mSharedPreferences.getString(this.value, null);
    }

    public void putFirebaseChildrenPath(String firebaseChildrenPath) {
        mSharedPreferences.edit().putString(this.firebaseChildrenPath, firebaseChildrenPath).apply();
    }

    public String getFirebaseChildrenPath() {
        return mSharedPreferences.getString(this.firebaseChildrenPath, null);
    }
}
