package com.acat.firebase.rtdbbe.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.acat.firebase.rtdbbe.viewmodel.UploadOrUpdateViewModel;

import java.util.List;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    //data Binding
    MainActivityBinding mBinding;

    //realtime firebase database
    private FirebaseRealtimeCRUDGenerator firebaseCRUD;
    private List<KeyAndValue> data;

    //local database
    private DataManager dataManager;
    //childrenPath
    private final String childrenPath = "entries/registration/users";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        mBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mBinding.setViewModel(new UploadOrUpdateViewModel(this));
        mBinding.executePendingBindings();
        mBinding.simpleListView.setOnItemClickListener(this);

        //Local Database
        dataManager = ((AppLayer)getApplication()).getDataManager();

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

    @BindingAdapter("android:toast")
    public static void onBtnClick(View v, String message) {
        if (message!=null)
        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        toastFirebaseResult(data.get(position).getFirebaseKey());

        //Save ITEM in Local Database and fetch to UPDATE and DELETE
        dataManager.setFirebaseKey(data.get(position).getFirebaseKey());
        dataManager.setFirebaseValue(data.get(position).getFirebaseValue());
        dataManager.setFirebaseChildrenPath(childrenPath);
        Intent intent = UpdateAndDeleteActivity.getIntent(this);
        startActivity(intent);
    }

    public void toastFirebaseResult(String output) {
        if (output!=null)
        Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
    }

    public void retrieveFirebaseData(final List<KeyAndValue> data) {
        this.data = data;
        if (data==null) {
            mBinding.simpleListView.setAdapter(null); //Clear ListView Here
            return;
        }
        mBinding.simpleListView.setAdapter(new CustomListView(this, data));
    }
}
