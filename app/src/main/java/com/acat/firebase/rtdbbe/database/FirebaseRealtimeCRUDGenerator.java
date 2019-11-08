package com.acat.firebase.rtdbbe.database;

import androidx.annotation.NonNull;

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

public class FirebaseRealtimeCRUDGenerator {

    private FirebaseDependency dependency = FirebaseDependency.getInstance();
    private RealtimeFirebasePath firebasePath;

    public void transferToFirebase(FirebaseOperation operation, final Object object, String childrenPath, final FirebaseResult result) {
        //DatabaseReference
        firebasePath = (RealtimeFirebasePath) dependency.getInstance(RealtimeFirebasePath.class);
        firebasePath.setDatabaseReference(FirebaseUtils.REFERENCE);

        //Object to Json Conversion
        Gson gson = (Gson) dependency.getInstance(Gson.class);
        String json = gson.toJson(object);

        //Children creation
        firebasePath.setChildrenPath(childrenPath);

        switch (operation) {
            case CREATE:
            case UPDATE:
                createOrUpdate(operation, result, json);
                break;
            case RETRIEVE:
                retrieve(result);
                break;
            case DELETE:
                delete(childrenPath, result);
                break;
            default:

        }
    }

    public void createOrUpdate(final FirebaseOperation operation, final FirebaseResult result, String json) {
        //Generate KEY for current path
        DatabaseReference reference = firebasePath.getChildrenPath();
        final String fakeId = reference.push().getKey();

        //Upload KEY and VALUE to Firebase
        if (operation==FirebaseOperation.CREATE) {
            reference.child(fakeId).setValue(json);
        }
        else if (operation==FirebaseOperation.UPDATE) {
            reference.setValue(json);
        }

        //Result
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean enableWrite = false;
                for (DataSnapshot out: dataSnapshot.getChildren()) {
                    if (fakeId.equals(out.getKey())) {
                        enableWrite = !enableWrite;
                        result.toastFirebaseResult(out.getKey()+"-->"+out.getValue(String.class));
                    }

                }
                if (!enableWrite) {
                    if (operation==FirebaseOperation.UPDATE) {
                        result.toastFirebaseResult("Updated Data!");
                    }
                    else {
                        result.toastFirebaseResult("You have to permission WRITE!");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.toastFirebaseResult("Failed to read data from Firebase!");
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
                    result.retrieveFirebaseResult(data);
                    result.toastFirebaseResult("All data is fetched from server!");
                }
                else {
                    result.toastFirebaseResult("No data on Firebase!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.toastFirebaseResult("Failed to read data from Firebase!");
            }
        });
    }

    public void delete(String childrenPath, final FirebaseResult result) {
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
                    result.toastFirebaseResult("Deleted from Firebase Server!");
                }
                else {
                    result.toastFirebaseResult("Failed to delete from Server!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.toastFirebaseResult("Failed to read data from Firebase!");
            }
        });
    }
}
