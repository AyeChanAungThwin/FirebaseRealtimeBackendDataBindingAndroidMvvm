package com.acat.firebase.rtdbbe.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.acat.firebase.rtdbbe.R;
import com.acat.firebase.rtdbbe.applicationlayer.AppLayer;
import com.acat.firebase.rtdbbe.database.FirebaseOperation;
import com.acat.firebase.rtdbbe.database.FirebaseRealtimeCRUDGenerator;
import com.acat.firebase.rtdbbe.database.observer.FirebaseResult;
import com.acat.firebase.rtdbbe.datamanager.DataManager;
import com.acat.firebase.rtdbbe.model.CustomListView;
import com.acat.firebase.rtdbbe.model.KeyAndValue;
import com.acat.firebase.rtdbbe.model.User;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FirebaseResult, View.OnClickListener, AdapterView.OnItemClickListener {

    private Button btn1;
    private EditText name, age;
    private ListView listView;

    private FirebaseRealtimeCRUDGenerator firebaseCRUD;
    private User user;
    private String childrenPath;
    private List<KeyAndValue> data;

    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        dataManager = ((AppLayer)getApplication()).getDataManager();

        //Mapping
        btn1 = findViewById(R.id.btn1);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        listView = findViewById(R.id.simpleListView);
        btn1.setOnClickListener(this);
        listView.setOnItemClickListener(this);

        //Fascade
        firebaseCRUD = (FirebaseRealtimeCRUDGenerator)
                ((AppLayer)getApplication()).getInstance(FirebaseRealtimeCRUDGenerator.class);
        //Set Children Path
        childrenPath = "entries/registration/users";
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseCRUD.transferToFirebase(FirebaseOperation.RETRIEVE, null, childrenPath, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseCRUD.transferToFirebase(FirebaseOperation.RETRIEVE, null, childrenPath, this);
    }

    @Override
    public void toastFirebaseResult(String output) {
        if (output!=null) {
            Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void retrieveFirebaseData(final List<KeyAndValue> data) {
        this.data = data;
        if (data==null) {
            listView.setAdapter(null);
        }
        else {
            listView.setAdapter(new CustomListView(this, data));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                String username = name.getText().toString();
                String userAge = age.getText().toString();

                if (username.length()>5 && !userAge.isEmpty()) {
                    //User Object
                    user = (User) ((AppLayer)getApplication()).getInstance(User.class);
                    user.setName(name.getText().toString());
                    user.setAge(age.getText().toString());
                    firebaseCRUD.transferToFirebase(FirebaseOperation.CREATE, user, childrenPath, this);
                }
                else {
                    toastFirebaseResult("Field length < 5!");
                    Toast.makeText(getApplicationContext(), "Field length must greater than 5", Toast.LENGTH_SHORT).show();
                }
                break;
                //generator.transferToFirebase(FirebaseOperation.RETRIEVE, null, childrenPath, this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), data.get(position).getKey(), Toast.LENGTH_SHORT).show();
        dataManager.setKey(data.get(position).getKey());
        dataManager.setValue(data.get(position).getValue());
        dataManager.setFirebaseChildrenPath(childrenPath);
        Intent intent = new Intent(this, UpdateAndDeleteActivity.class);
        startActivity(intent);
    }
}
