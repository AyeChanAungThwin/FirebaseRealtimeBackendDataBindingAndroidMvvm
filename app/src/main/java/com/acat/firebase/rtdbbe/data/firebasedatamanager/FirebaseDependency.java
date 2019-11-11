package com.acat.firebase.rtdbbe.data.firebasedatamanager;

import java.lang.reflect.InvocationTargetException;

class FirebaseDependency {

    private static FirebaseDependency dependency;

    public static FirebaseDependency getInstance() {
        if (dependency==null) {
            synchronized (FirebaseDependency.class) {
                if (dependency==null) {
                    dependency = new FirebaseDependency();
                }
            }
        }
        return dependency;
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
