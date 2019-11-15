package com.acat.firebase.rtdbbe.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.acat.firebase.rtdbbe.R;
import com.acat.firebase.rtdbbe.application.AppLayer;
import com.acat.firebase.rtdbbe.databases.realtimefirebase.FirebaseOperation;
import com.acat.firebase.rtdbbe.databases.realtimefirebase.FirebaseRealtimeCRUDGenerator;
import com.acat.firebase.rtdbbe.databases.localdatamanager.DataManager;
import com.acat.firebase.rtdbbe.databinding.MainActivityBinding;
import com.acat.firebase.rtdbbe.model.CustomListView;
import com.acat.firebase.rtdbbe.model.KeyAndValue;
import com.acat.firebase.rtdbbe.viewmodel.MainViewModel;

import java.util.List;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    //local database
    private DataManager dataManager;

    //Data Binding
    MainActivityBinding mBinding;

    //realtime firebase database
    private FirebaseRealtimeCRUDGenerator firebaseCRUD;
    private List<KeyAndValue> data;

    //childrenPath
    private final String childrenPath = "entries/registration/users";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        //Local Database
        dataManager = ((AppLayer)getApplication()).getDataManager();

        //Data Binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mBinding.setViewModel(new MainViewModel(childrenPath, this));
        mBinding.simpleListView.setOnItemClickListener(this);

        //Firebase CRUD
        firebaseCRUD = (FirebaseRealtimeCRUDGenerator)
                ((AppLayer)getApplication()).getInstance(FirebaseRealtimeCRUDGenerator.class);
        firebaseCRUD.setChildrenPath(childrenPath);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseCRUD.execute(FirebaseOperation.RETRIEVE, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseCRUD.execute(FirebaseOperation.RETRIEVE, null,this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        toastFirebaseResult(data.get(position).getFirebaseKey());

        //Save ITEM in Local Database and fetch them for UPDATE and DELETE
        dataManager.saveClickItemFromList(data, position, childrenPath);

        //Go to another Activity
        Intent intent = UpdateAndDeleteActivity.getIntent(this);
        startActivity(intent);
    }

    public void toastFirebaseResult(String output) {
        if (output!=null) {
            Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
        }
    }

    public void retrieveFirebaseData(final List<KeyAndValue> data) {
        this.data = data;
        if (data==null) {
            mBinding.simpleListView.setAdapter(null); //Clear ListView Here
            return;
        }
        mBinding.simpleListView.setAdapter(new CustomListView(this, data));
    }

    @BindingAdapter("android:toast")
    public static void toastFieldErrorResult(View v, String message) {
        if (message!=null) {
            Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
