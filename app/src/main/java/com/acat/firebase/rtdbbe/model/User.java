package com.acat.firebase.rtdbbe.model;

import com.acat.firebase.rtdbbe.data.firebasedatamanager.FirebaseModel;

public class User implements FirebaseModel {

    private String name;
    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
