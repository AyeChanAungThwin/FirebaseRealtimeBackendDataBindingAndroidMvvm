package com.acat.firebase.rtdbbe.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealtimeFirebasePath {

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
        databaseReference.child(childrenPath);
    }

    public DatabaseReference getChildrenPath() {
        return databaseReference.child(this.childrenPath);
    }
}
