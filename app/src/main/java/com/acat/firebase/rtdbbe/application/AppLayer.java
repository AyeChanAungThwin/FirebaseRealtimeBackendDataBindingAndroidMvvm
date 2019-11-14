package com.acat.firebase.rtdbbe.application;

import android.app.Application;

import com.acat.firebase.rtdbbe.databases.localdatamanager.DataManager;
import com.acat.firebase.rtdbbe.databases.localdatamanager.SharedPrefHelper;

import java.lang.reflect.InvocationTargetException;

public class AppLayer extends Application {

    private DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(this);
        dataManager = new DataManager(sharedPrefHelper);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public Object getInstance(Class className) {
        try {
            return className.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
