package com.acat.firebase.rtdbbe.databases.realtimefirebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class RealtimeFirebasePath {

    private final FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = null;

    private String childrenPath;

    public void setDatabaseReference(String referenceName) {
        databaseReference = firebase.getReference(referenceName);
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setChildrenPath(String childrenPath) {
        this.childrenPath = childrenPath;
    }

    public DatabaseReference getChildrenPath() {
        return getDatabaseReference().child(childrenPath);
    }
}
