package com.acat.firebase.rtdbbe.view;

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
import com.acat.firebase.rtdbbe.data.firebasedatamanager.FirebaseOperation;
import com.acat.firebase.rtdbbe.data.firebasedatamanager.FirebaseRealtimeCRUDGenerator;
import com.acat.firebase.rtdbbe.data.localdatamanager.DataManager;
import com.acat.firebase.rtdbbe.model.CustomListView;
import com.acat.firebase.rtdbbe.model.KeyAndValue;
import com.acat.firebase.rtdbbe.model.User;

import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button btn1;
    private EditText name, age;
    private ListView listView;

    private FirebaseRealtimeCRUDGenerator firebaseCRUD;
    private User user;
    private List<KeyAndValue> data;

    private DataManager dataManager;
    private String childrenPath = "entries/registration/users";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        mapping();

        //Local Database
        dataManager = ((AppLayer)getApplication()).getDataManager();

        //Fascade
        firebaseCRUD = (FirebaseRealtimeCRUDGenerator)
                ((AppLayer)getApplication()).getInstance(FirebaseRealtimeCRUDGenerator.class);

        //Set Children Path
        firebaseCRUD.setChildrenPath(childrenPath);
    }

    private void mapping() {
        //Mapping
        btn1 = findViewById(R.id.btn1);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        listView = findViewById(R.id.simpleListView);
        btn1.setOnClickListener(this);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseCRUD.execute(FirebaseOperation.RETRIEVE, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseCRUD.execute(FirebaseOperation.RETRIEVE,this);
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
                    firebaseCRUD.execute(FirebaseOperation.CREATE, user, this);
                }
                else {
                    toastFirebaseResult("Field length < 5!");
                    Toast.makeText(getApplicationContext(), "Field length must greater than 5", Toast.LENGTH_SHORT).show();
                }
                break;
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

    public void toastFirebaseResult(String output) {
        Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
    }

    public void retrieveFirebaseData(final List<KeyAndValue> data) {
        this.data = data;
        if (data==null) {
            listView.setAdapter(null); //Clear ListView Here
            return;
        }
        listView.setAdapter(new CustomListView(this, data));
    }
}
