package com.acat.firebase.rtdbbe.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.acat.firebase.rtdbbe.R;
import com.acat.firebase.rtdbbe.applicationlayer.AppLayer;
import com.acat.firebase.rtdbbe.data.firebasedatamanager.FirebaseOperation;
import com.acat.firebase.rtdbbe.data.firebasedatamanager.FirebaseRealtimeCRUDGenerator;
import com.acat.firebase.rtdbbe.data.localdatamanager.DataManager;
import com.acat.firebase.rtdbbe.model.User;
import com.google.gson.Gson;

public class UpdateAndDeleteActivity extends BaseActivity implements View.OnClickListener {

    private EditText updateNameEditText, updateAgeEditText;
    private Button updateBtn, deleteBtn;

    private DataManager dataManager;
    private String childrenPath;
    private FirebaseRealtimeCRUDGenerator firebaseCRUD;
    private User userFromDB;

    public static Intent getIntent(Context context) {
        return new Intent(context, UpdateAndDeleteActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_and_delete_activity);

        //mapping
        mapping();

        //Local Database
        dataManager = ((AppLayer)getApplication()).getDataManager();
        childrenPath = dataManager.getFirebaseChildrenUpdateOrDeletePath();

        //Firebase CRUD
        firebaseCRUD = (FirebaseRealtimeCRUDGenerator)
                ((AppLayer)getApplication()).getInstance(FirebaseRealtimeCRUDGenerator.class);
        firebaseCRUD.setChildrenPath(childrenPath);

        //Generate Java Object User from JSON
        Gson gson = (Gson) ((AppLayer)getApplication()).getInstance(Gson.class);
        userFromDB = gson.fromJson(dataManager.getFirebaseValue(), User.class);

        //set EditText
        updateNameEditText.setText(userFromDB.getName());
        updateAgeEditText.setText(String.valueOf(userFromDB.getAge()));
    }

    public void mapping() {
        updateNameEditText = findViewById(R.id.updateNameEditText);
        updateAgeEditText = findViewById(R.id.updateAgeEditText);
        updateBtn = findViewById(R.id.updateBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateBtn:
                String name = updateNameEditText.getText().toString();
                int age = Integer.parseInt(updateAgeEditText.getText().toString());

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
                firebaseCRUD.execute(FirebaseOperation.DELETE, null, this);
                finish();
                break;
        }
    }

    private boolean isSameData(User userFromDB, User updateUser) {
        if (userFromDB.getName().equals(updateUser.getName())
                &&userFromDB.getAge()==(updateUser.getAge())) {
            return true;
        }
        return false;
    }

    public void toastFirebaseResult(String output) {
        if (output!=null) {
            Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        }
    }
}
