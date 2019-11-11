package com.acat.firebase.rtdbbe.data.localdatamanager;

public class DataManager {

    private SharedPrefHelper helper;

    public DataManager(SharedPrefHelper helper) {
        this.helper = helper;
    }

    public void setKey(String key) {
        helper.putKey(key);
    }

    public String getKey() {
        return helper.getKey();
    }

    public void setValue(String json) {
        helper.putValue(json);
    }

    public String getValue() {
        return helper.getValue();
    }

    public void setFirebaseChildrenPath(String childrenPath) {
        helper.putFirebaseChildrenPath(childrenPath);
    }

    public String getFirebaseChildrenPath() {
        return helper.getFirebaseChildrenPath();
    }

    public String getFirebaseChildrenUpdateOrDeletePath() {
        return getFirebaseChildrenPath()+"/"+getKey();
    }
}
