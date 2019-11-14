package com.acat.firebase.rtdbbe.databases.localdatamanager;

public class DataManager {

    private SharedPrefHelper helper;

    public DataManager(SharedPrefHelper helper) {
        this.helper = helper;
    }

    public void setFirebaseKey(String firebaseKey) {
        helper.putKey(firebaseKey);
    }

    public String getFirebaseKey() {
        return helper.getKey();
    }

    public void setFirebaseValue(String json) {
        helper.putValue(json);
    }

    public String getFirebaseValue() {
        return helper.getValue();
    }

    public void setFirebaseChildrenPath(String childrenPath) {
        helper.putFirebaseChildrenPath(childrenPath);
    }

    public String getFirebaseChildrenPath() {
        return helper.getFirebaseChildrenPath();
    }

    public String getFirebaseChildrenUpdateOrDeletePath() {
        StringBuilder sb = helper.createStringBuilder();
        sb.append(getFirebaseChildrenPath());
        sb.append("/");
        sb.append(getFirebaseKey());
        return sb.toString();
    }
}
