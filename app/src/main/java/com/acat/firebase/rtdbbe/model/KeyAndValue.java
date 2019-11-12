package com.acat.firebase.rtdbbe.model;

public class KeyAndValue {

    private String firebaseKey;
    private String firebaseValue;

    public KeyAndValue() {

    }

    public KeyAndValue(String firebaseKey, String firebaseValue) {
        this.firebaseKey = firebaseKey;
        this.firebaseValue = firebaseValue;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getFirebaseValue() {
        return firebaseValue;
    }

    public void setFirebaseValue(String firebaseValue) {
        this.firebaseValue = firebaseValue;
    }
}
