package com.acat.firebase.rtdbbe.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.acat.firebase.rtdbbe.R;
import com.acat.firebase.rtdbbe.applicationlayer.AppLayer;
import com.acat.firebase.rtdbbe.data.firebasedatamanager.FirebaseOperation;
import com.acat.firebase.rtdbbe.data.firebasedatamanager.FirebaseRealtimeCRUDGenerator;
import com.acat.firebase.rtdbbe.data.firebasedatamanager.observer.FirebaseResult;
import com.acat.firebase.rtdbbe.data.localdatamanager.DataManager;
import com.acat.firebase.rtdbbe.model.KeyAndValue;
import com.acat.firebase.rtdbbe.model.User;
import com.google.gson.Gson;

import java.util.List;

public class UpdateAndDeleteActivity extends AppCompatActivity implements View.OnClickListener, FirebaseResult {

    private EditText updateName, updateAge;
    private Button updateBtn, deleteBtn;

    private DataManager dataManager;

    private FirebaseRealtimeCRUDGenerator firebaseCRUD;

    private User userFromDB;

    private String childrenPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_and_delete_activity);
        mapping();
        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        //Local Database
        dataManager = ((AppLayer)getApplication()).getDataManager();
        //Retrieve ChildrenPath From Database;
        childrenPath = dataManager.getFirebaseChildrenUpdateOrDeletePath();


        firebaseCRUD = (FirebaseRealtimeCRUDGenerator) ((AppLayer)getApplication()).getInstance(FirebaseRealtimeCRUDGenerator.class);
        firebaseCRUD.setChildrenPath(childrenPath);

        Gson gson = (Gson) ((AppLayer)getApplication()).getInstance(Gson.class);
        userFromDB = gson.fromJson(dataManager.getValue(), User.class);

        updateName.setText(userFromDB.getName());
        updateAge.setText(userFromDB.getAge());
    }

    public void mapping() {
        updateName = findViewById(R.id.updateName);
        updateAge = findViewById(R.id.updateAge);
        updateBtn = findViewById(R.id.updateBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateBtn:
                String name = updateName.getText().toString();
                String age = updateAge.getText().toString();

                //User Object
                User updateUser = (User) ((AppLayer)getApplication()).getInstance(User.class);
                updateUser.setName(name);
                updateUser.setAge(age);

                if (isSameData(userFromDB, updateUser)) {
                    toastFirebaseResult("No updates for same data!");
                }
                else {
                    firebaseCRUD.execute(FirebaseOperation.UPDATE, updateUser, this);
                }
                finish();
                break;
            case R.id.deleteBtn:
                firebaseCRUD.execute(FirebaseOperation.DELETE, this);
                finish();
                break;
        }
    }

    private boolean isSameData(User userFromDB, User updateUser) {
        if (userFromDB.getName().equals(updateUser.getName())
                &&userFromDB.getAge().equals(updateUser.getAge())) {
            return true;
        }
        return false;
    }

    @Override
    public void toastFirebaseResult(String output) {
        if (output!=null) {
            Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void retrieveFirebaseData(List<KeyAndValue> data) {

    }
}
