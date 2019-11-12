package com.acat.firebase.rtdbbe.data.firebasedatamanager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.acat.firebase.rtdbbe.data.firebasedatamanager.observer.FirebaseResult;
import com.acat.firebase.rtdbbe.model.KeyAndValue;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

//Facade by Aye Chan Aung Thwin

public class FirebaseRealtimeCRUDGenerator<T extends FirebaseModel> {

    private final FirebaseDependency dependency = FirebaseDependency.getInstance();
    private RealtimeFirebasePath firebasePath;
    private String childrenPath;
    private FirebaseResult result;
    private String[] splitData;
    private FirebaseOperation operation;
    private List<KeyAndValue> allDataListFromFirebase;
    private Context context;
    private boolean needUpdate = true;

    private String getChildrenPath() {
        return childrenPath;
    }

    public void setChildrenPath(String childrenPath) {
        this.childrenPath = childrenPath;
    }

    public <T extends FirebaseModel> void execute(FirebaseOperation operation, T object, final FirebaseResult result) {
        //DatabaseReference
        firebasePath = (RealtimeFirebasePath) dependency.getInstance(RealtimeFirebasePath.class);
        firebasePath.setDatabaseReference(FirebaseUtils.DEVELOPER_REFERENCE);

        //Object to Json Conversion
        Gson gson = (Gson) dependency.getInstance(Gson.class);
        String json = gson.toJson(object);

        //Children creation
        firebasePath.setChildrenPath(getChildrenPath());

        //FirebaseResult
        this.result = result;

        //operation
        this.operation = operation;

        switch (operation) {
            case CREATE:
                //Operation
                create(json);
                break;
            case UPDATE:
                //Operation
                if (!needUpdate) {
                    result.toastFirebaseResult("No need update for same data!");
                }
                update(json);
                break;
            case RETRIEVE:
                //Operation
                retrieve();
                break;
            case DELETE:
                //Operation
                delete();
                break;
        }
    }

    public void create(String json){
        //Generate KEY for current path
        DatabaseReference reference = firebasePath.getChildrenPath();
        final String fakeId = reference.push().getKey();

        //Upload KEY and VALUE to Firebase i.e., reference.child(KEY).setValue(VALUE);
        reference.child(fakeId).setValue(json);

        //Result
        listenResult(reference, fakeId); //Check KEY
    }

    public void update(String json){
        //Generate KEY for current path
        DatabaseReference reference = firebasePath.getChildrenPath();

        //data creation to non-empty path to UPDATE data
        reference.setValue(json);

        //Split data and manage
        splitData = getChildrenPath().split("/");
        StringBuilder sb = null;
        for (int i=0; i<splitData.length; i++) {
            if (i< splitData.length-1) {
                if (sb==null) {
                    sb = (StringBuilder) dependency.getInstance(StringBuilder.class);
                    sb.append(splitData[i]);
                }
                else {
                    sb.append("/");
                    sb.append(splitData[i]);
                }
            }
        }
        firebasePath.setChildrenPath(sb.toString()); //Set children path
        reference = firebasePath.getChildrenPath(); //Set new children path to fetch result;

        //Result
        listenResult(reference, splitData[splitData.length-1]); //Check KEY
    }

    public void retrieve() {
        //Generate KEY for current path
        DatabaseReference reference = firebasePath.getChildrenPath();

        //Result
        listenResult(reference, null);

    }

    public void delete() {
        DatabaseReference reference = firebasePath.getChildrenPath();

        //Delete
        reference.removeValue();

        //Split data and manage
        splitData = getChildrenPath().split("/");
        StringBuilder sb = null;
        for (int i=0; i<splitData.length; i++) {
            if (i< splitData.length-1) {
                if (sb==null) {
                    sb = (StringBuilder) dependency.getInstance(StringBuilder.class);
                    sb.append(splitData[i]);
                }
                else {
                    sb.append("/");
                    sb.append(splitData[i]);
                }
            }
        }
        firebasePath.setChildrenPath(sb.toString()); //Set children path
        reference = firebasePath.getChildrenPath(); //Set new children path to fetch result;

        //Result
        listenResult(reference, splitData[splitData.length-1]);
    }

    private void listenResult(DatabaseReference reference, final String id) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allDataListFromFirebase = null;
                boolean isCreatedOrUpdated = false;
                boolean isDeleted = true;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot out: dataSnapshot.getChildren()) {
                        switch (operation) {
                            case CREATE:
                            case UPDATE:
                                if (out.getKey().equals(id)) {
                                    isCreatedOrUpdated = !isCreatedOrUpdated;
                                    break;
                                }
                                break;
                            case DELETE:
                                if (out.getKey().equals(id)) {
                                    isDeleted = !isDeleted;
                                    break;
                                }
                                break;
                            case RETRIEVE:
                                if (allDataListFromFirebase == null) {
                                    allDataListFromFirebase = (List<KeyAndValue>) dependency.getInstance(ArrayList.class);
                                }
                                allDataListFromFirebase.add(new KeyAndValue(out.getKey(), out.getValue(String.class)));
                                break;
                        }
                    }
                }
                switch (operation) {
                    case CREATE:
                        if (isCreatedOrUpdated) {
                            result.toastFirebaseResult(FirebaseUtils.CREATION_SUCCESS);

                            //After Creation, refresh listView i.e., RETRIEVE
                            operation = FirebaseOperation.RETRIEVE;
                            retrieve();
                        }
                        else {
                            result.toastFirebaseResult(FirebaseUtils.CREATION_FAILED);
                        }
                        break;
                    case UPDATE:
                        if (isCreatedOrUpdated) {
                            result.toastFirebaseResult(FirebaseUtils.UPDATE_SUCCESS);
                        }
                        else {
                            result.toastFirebaseResult(FirebaseUtils.UPDATE_FAILED);
                        }
                        break;
                    case RETRIEVE:
                        if (allDataListFromFirebase!=null) {
                            result.retrieveFirebaseData(allDataListFromFirebase);
                            //result.toastFirebaseResult(FirebaseUtils.RETRIEVE_EXISTS);
                        }
                        else {
                            result.retrieveFirebaseData(null);
                            result.toastFirebaseResult(FirebaseUtils.RETRIEVE_NO_DATA);
                        }
                        break;
                    case DELETE:
                        if (isDeleted) {
                            result.toastFirebaseResult(FirebaseUtils.DELETE_SUCCESS);
                        }
                        else {
                            result.toastFirebaseResult(FirebaseUtils.DELETE_FAILED);
                        }
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.toastFirebaseResult(FirebaseUtils.FIREBASE_ERROR);
            }
        });
    }
}
