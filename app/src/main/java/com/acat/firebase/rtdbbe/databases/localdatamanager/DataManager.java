package com.acat.firebase.rtdbbe.databases.localdatamanager;

import com.acat.firebase.rtdbbe.model.KeyAndValue;

import java.util.List;

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

    public void saveClickItemFromList(List<KeyAndValue> data, int position, String childrenPath) {
        this.setFirebaseKey(data.get(position).getFirebaseKey());
        this.setFirebaseValue(data.get(position).getFirebaseValue());
        this.setFirebaseChildrenPath(childrenPath);
    }
}
