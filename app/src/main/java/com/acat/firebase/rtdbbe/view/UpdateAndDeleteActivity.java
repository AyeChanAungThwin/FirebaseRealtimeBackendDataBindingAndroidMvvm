package com.acat.firebase.rtdbbe.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.acat.firebase.rtdbbe.R;
import com.acat.firebase.rtdbbe.applicationlayer.AppLayer;
import com.acat.firebase.rtdbbe.database.FirebaseOperation;
import com.acat.firebase.rtdbbe.database.FirebaseRealtimeCRUDGenerator;
import com.acat.firebase.rtdbbe.database.observer.FirebaseResult;
import com.acat.firebase.rtdbbe.datamanager.DataManager;
import com.acat.firebase.rtdbbe.model.KeyAndValue;
import com.acat.firebase.rtdbbe.model.User;
import com.google.gson.Gson;

import java.util.List;

public class UpdateAndDeleteActivity extends AppCompatActivity implements View.OnClickListener, FirebaseResult {

    private EditText updateName, updateAge;
    private Button updateBtn, deleteBtn;

    private DataManager dataManager;

    private FirebaseRealtimeCRUDGenerator generator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_and_delete_activity);
        mapping();
        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        dataManager = ((AppLayer)getApplication()).getDataManager();
        generator = (FirebaseRealtimeCRUDGenerator) ((AppLayer)getApplication()).getInstance(FirebaseRealtimeCRUDGenerator.class);
        Gson gson = (Gson) ((AppLayer)getApplication()).getInstance(Gson.class);
        User user = gson.fromJson(dataManager.getValue(), User.class);

        updateName.setText(user.getName());
        updateAge.setText(user.getAge());
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
                User user = (User) ((AppLayer)getApplication()).getInstance(User.class);
                user.setName(name);
                user.setAge(age);

                //ChildrenPath From Database;
                String childrenPath = dataManager.getFirebaseChildrenUpdateOrDeletePath();
                generator.transferToFirebase(FirebaseOperation.UPDATE, user, childrenPath,this);
                finish();
                break;
            case R.id.deleteBtn:
                //ChildrenPath From Database;
                String childrenPath2 = dataManager.getFirebaseChildrenUpdateOrDeletePath();
                generator.transferToFirebase(FirebaseOperation.DELETE, null, childrenPath2,this);
                finish();
                break;
        }
    }

    @Override
    public void toastFirebaseResult(String output) {
        if (output!=null) {
            Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void retrieveFirebaseResult(List<KeyAndValue> data) {

    }
}
