package com.acat.firebase.rtdbbe.viewmodel;

import com.acat.firebase.rtdbbe.model.User;

import java.util.Observable;

public class MainActivityViewModel extends Observable {

    private User user;

    MainActivityViewModel() {
        user = new User("", 0);
    }
}
