package com.acat.firebase.rtdbbe.databases.realtimefirebase.observer;

import com.acat.firebase.rtdbbe.model.KeyAndValue;

import java.util.List;

public interface FirebaseResult {

    void toastFirebaseResult(String output);
    void retrieveFirebaseData(List<KeyAndValue> data);
}
