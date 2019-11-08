package com.acat.firebase.rtdbbe.database;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.acat.firebase.rtdbbe.R;
import com.acat.firebase.rtdbbe.database.observer.FirebaseResult;
import com.acat.firebase.rtdbbe.database.utils.FirebaseUtils;
import com.acat.firebase.rtdbbe.model.KeyAndValue;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

//Facade by Aye Chan Aung Thwin

public class FirebaseRealtimeCRUDGenerator {

    private FirebaseDependency dependency = FirebaseDependency.getInstance();
    private RealtimeFirebasePath firebasePath;
    private String childrenPath;

    public void transferToFirebase(FirebaseOperation operation, final Object object, String childrenPath, final FirebaseResult result) {
        //DatabaseReference
        firebasePath = (RealtimeFirebasePath) dependency.getInstance(RealtimeFirebasePath.class);
        firebasePath.setDatabaseReference(FirebaseUtils.DEVELOPER_REFERENCE);

        //ChildrenPath
        this.childrenPath = childrenPath;

        //Object to Json Conversion
        Gson gson = (Gson) dependency.getInstance(Gson.class);
        String json = gson.toJson(object);

        //Children creation
        firebasePath.setChildrenPath(childrenPath);

        switch (operation) {
            case CREATE:
                create(operation, result, json);
                break;
            case UPDATE:
                update(operation, result, json);
                break;
            case RETRIEVE:
                retrieve(result);
                break;
            case DELETE:
                delete(result);
                break;
            default:

        }
    }

    public void create(final FirebaseOperation operation, final FirebaseResult result, String json) {
        //Generate KEY for current path
        DatabaseReference reference = firebasePath.getChildrenPath();
        final String fakeId = reference.push().getKey();

        //Upload KEY and VALUE to Firebase
        reference.child(fakeId).setValue(json);

        //Result
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isCreated = false;
                    for (DataSnapshot out: dataSnapshot.getChildren()) {
                        switch (operation) {
                            case CREATE:
                                if (out.getKey().equals(fakeId)) {
                                    isCreated = true;
                                }
                                break;
                        }
                    }
                    if (isCreated) {
                        result.toastFirebaseResult(FirebaseUtils.CREATION_SUCCESS);
                    }
                    else {
                        result.toastFirebaseResult(FirebaseUtils.CREATION_FAILED);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.toastFirebaseResult(FirebaseUtils.FIREBASE_ERROR);
            }
        });
    }

    public void update(final FirebaseOperation operation, final FirebaseResult result, String json) {
        //Generate KEY for current path
        DatabaseReference reference = firebasePath.getChildrenPath();
        final String fakeId = reference.push().getKey();

        //data creation to non-empty path to UPDATE data
        reference.setValue(json);

        //Split data and manage
        final String[] data = childrenPath.split("/");
        StringBuilder sb = null;
        for (int i=0; i<data.length; i++) {
            if (i<data.length-1) {
                if (sb==null) {
                    sb = (StringBuilder) dependency.getInstance(StringBuilder.class);
                    sb.append(data[i]);
                }
                else {
                    sb.append("/");
                    sb.append(data[i]);
                }
            }
        }
        firebasePath.setChildrenPath(sb.toString()); //Set children path
        reference = firebasePath.getChildrenPath(); //Set new children path to fetch result;

        //Result
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isUpdated = false;
                    for (DataSnapshot out: dataSnapshot.getChildren()) {
                        if (out.getKey().equals(data[data.length-1])) {
                            isUpdated = true;
                        }
                    }
                    if (isUpdated) {
                        result.toastFirebaseResult(FirebaseUtils.UPDATE_SUCCESS);
                    }
                    else {
                        result.toastFirebaseResult(FirebaseUtils.UPDATE_FAILED);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.toastFirebaseResult(FirebaseUtils.FIREBASE_ERROR);
            }
        });
    }

    public void retrieve(final FirebaseResult result) {
        //Generate KEY for current path
        DatabaseReference reference = firebasePath.getChildrenPath();

        //Result
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<KeyAndValue> data = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot out: dataSnapshot.getChildren()) {
                        if (data==null) {
                            data = new ArrayList<>();
                        }
                        data.add(new KeyAndValue(out.getKey(), out.getValue(String.class)));
                    }
                }
                if (data!=null) {
                    result.retrieveFirebaseData(data);
                    result.toastFirebaseResult(FirebaseUtils.RETRIEVE_EXISTS);
                }
                else {
                    result.retrieveFirebaseData(null);
                    result.toastFirebaseResult(FirebaseUtils.RETRIEVE_NO_DATA);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.toastFirebaseResult(FirebaseUtils.FIREBASE_ERROR);
            }
        });
    }

    public void delete(final FirebaseResult result) {
        DatabaseReference reference = firebasePath.getChildrenPath();

        //Delete
        reference.removeValue();

        //Split data and manage
        final String[] data = childrenPath.split("/");
        StringBuilder sb = null;
        for (int i=0; i<data.length; i++) {
            if (i<data.length-1) {
                if (sb==null) {
                    sb = (StringBuilder) dependency.getInstance(StringBuilder.class);
                    sb.append(data[i]);
                }
                else {
                    sb.append("/");
                    sb.append(data[i]);
                }
            }
        }
        firebasePath.setChildrenPath(sb.toString()); //Set children path
        reference = firebasePath.getChildrenPath(); //Set new children path to fetch result;

        //Result
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isDeleted = true;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot out: dataSnapshot.getChildren()) {
                        if (out.getKey().equals(data[data.length-1])) {
                            isDeleted = false;
                        }
                    }
                }
                if (isDeleted) {
                    result.toastFirebaseResult(FirebaseUtils.DELETE_SUCCESS);
                }
                else {
                    result.toastFirebaseResult(FirebaseUtils.DELETE_FAILED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.toastFirebaseResult(FirebaseUtils.FIREBASE_ERROR);
            }
        });
    }
}
