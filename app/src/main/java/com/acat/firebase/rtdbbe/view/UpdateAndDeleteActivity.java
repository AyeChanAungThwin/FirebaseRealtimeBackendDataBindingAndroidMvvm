package com.acat.firebase.rtdbbe.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.acat.firebase.rtdbbe.R;
import com.acat.firebase.rtdbbe.application.AppLayer;
import com.acat.firebase.rtdbbe.databases.localdatamanager.DataManager;
import com.acat.firebase.rtdbbe.databinding.UpdateAndDeleteActivityBinding;
import com.acat.firebase.rtdbbe.viewmodel.UpdateOrDeleteViewModel;

public class UpdateAndDeleteActivity extends BaseActivity {

    //Local Database
    private DataManager dataManager;

    //Data Binding
    private UpdateAndDeleteActivityBinding mBinding;

    public static Intent getIntent(Context context) {
        return new Intent(context, UpdateAndDeleteActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Local Database
        dataManager = ((AppLayer)getApplication()).getDataManager();

        //Data Binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.update_and_delete_activity);
        mBinding.setViewModel(new UpdateOrDeleteViewModel(dataManager, this));
    }

    public void toastFirebaseResult(String output) {
        if (output!=null) {
            Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        }
    }

    @BindingAdapter("android:toast")
    public static void showToast(View v, String message) {
        if (message!=null)
            Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
