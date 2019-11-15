package com.acat.firebase.rtdbbe.viewmodel;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.acat.firebase.rtdbbe.databases.localdatamanager.DataManager;
import com.acat.firebase.rtdbbe.databases.realtimefirebase.FirebaseOperation;
import com.acat.firebase.rtdbbe.databases.realtimefirebase.FirebaseRealtimeCRUDGenerator;
import com.acat.firebase.rtdbbe.databases.realtimefirebase.observer.FirebaseResult;
import com.acat.firebase.rtdbbe.model.User;
import com.google.gson.Gson;

public class UpdateOrDeleteViewModel extends BaseObservable {

    private User user;
    private String toastMessage = null;

    private DataManager dataManager;
    private FirebaseResult result;

    public UpdateOrDeleteViewModel(DataManager dataManager, FirebaseResult result) {
        this.dataManager = dataManager;
        this.result = result;
        user = new User(getUserFromDB().getEmail(), getUserFromDB().getPassword());
    }

    @Bindable
    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        user.setEmail(email);
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPassword() {
        return user.getPassword();
    }

    public void setPassword(String password) {
        user.setPassword(password);
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getToastMessage() {
        return toastMessage;
    }

    public void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
        notifyPropertyChanged(BR.toastMessage);
    }

    public void onUpdate() {
        switch (isInputValid()) {
            case 0:
                setToastMessage("Please enter valid email address!");
                break;
            case 1:
                setToastMessage("Password length must greater than 5!");
                break;
            case 2:
                FirebaseRealtimeCRUDGenerator crud = new FirebaseRealtimeCRUDGenerator();
                crud.setChildrenPath(dataManager.getFirebaseChildrenUpdateOrDeletePath());
                if (isSameUser()) {
                    setToastMessage("No updates for same data!");
                    ((Activity)result).finish(); //Destroy the activity
                }
                else {
                    //Update New Data from fields
                    crud.execute(FirebaseOperation.UPDATE, this.user, result);
                    ((Activity)result).finish(); //Destroy the activity
                }
                break;
            case 3:
                setToastMessage("Please enter both fields");
                break;
        }
    }

    private boolean isSameUser() {
        if (getUserFromDB().getEmail().equals(getEmail())
                && getUserFromDB().getPassword().equals(getPassword())) {
            return true;
        }
        return false;
    }

    private User getUserFromDB() {
        Gson gson = new Gson();
        User user = gson.fromJson(dataManager.getFirebaseValue(), User.class);
        return user;
    }

    public void onDelete() {
        switch (isInputValid()) {
            case 0:
                setToastMessage("Please enter both fields");
                break;
            case 1:
                setToastMessage("Password length must greater than 5!");
                break;
            case 2:
                FirebaseRealtimeCRUDGenerator crud = new FirebaseRealtimeCRUDGenerator();
                crud.setChildrenPath(dataManager.getFirebaseChildrenUpdateOrDeletePath());
                crud.execute(FirebaseOperation.DELETE, null, result);
                ((Activity)result).finish();
                break;
            default:
                setToastMessage("Please enter valid email address!");
        }
    }

    public int isInputValid() {
        if (TextUtils.isEmpty(getEmail())&&TextUtils.isEmpty(getPassword())) {
            return 0;
        }
        else if (TextUtils.isEmpty(getPassword())||getPassword().length()<=5) {
            return 1;
        }
        else if (!TextUtils.isEmpty(getEmail())
                && Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches()
                && getPassword().length()>5) {
            return 2;
        }
        else {
            return 3;
        }
    }
}
