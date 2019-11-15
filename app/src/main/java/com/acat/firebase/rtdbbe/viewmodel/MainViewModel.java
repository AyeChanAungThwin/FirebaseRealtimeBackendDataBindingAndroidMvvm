package com.acat.firebase.rtdbbe.viewmodel;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.acat.firebase.rtdbbe.databases.realtimefirebase.FirebaseOperation;
import com.acat.firebase.rtdbbe.databases.realtimefirebase.FirebaseRealtimeCRUDGenerator;
import com.acat.firebase.rtdbbe.databases.realtimefirebase.observer.FirebaseResult;
import com.acat.firebase.rtdbbe.model.User;

public class MainViewModel extends BaseObservable {

    private User user;
    private String toastMessage = null;
    private String childrenPath;
    private FirebaseResult result;

    public MainViewModel(String childrenPath, FirebaseResult result) {
        this.childrenPath = childrenPath;
        this.result = result;
        user = new User("", "");
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

    public void onCreateNewData() {
        switch (isInputValid()) {
            case 0:
                setToastMessage("Please enter both fields");
                break;
            case 1:
                setToastMessage("Password length must greater than 5!");
                break;
            case 2:
                FirebaseRealtimeCRUDGenerator crud = new FirebaseRealtimeCRUDGenerator();
                crud.setChildrenPath(childrenPath);
                crud.execute(FirebaseOperation.CREATE, user, result);
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
