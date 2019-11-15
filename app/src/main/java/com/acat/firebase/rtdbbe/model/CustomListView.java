package com.acat.firebase.rtdbbe.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.acat.firebase.rtdbbe.R;
import com.acat.firebase.rtdbbe.databinding.CustomListViewBinding;
import com.google.gson.Gson;

import java.util.List;

public class CustomListView extends BaseAdapter {

    private List<KeyAndValue> allDataListFromFirebase;
    private Context context;

    public CustomListView(Context context, List<KeyAndValue> allDataListFromFirebase) {
        this.context = context;
        this.allDataListFromFirebase = allDataListFromFirebase;
    }

    @Override
    public int getCount() {
        return allDataListFromFirebase.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Check Log if key is available!
        Log.d("FIREBASE KEY ---> ", allDataListFromFirebase.get(position).getFirebaseKey());

        //Data binding
        CustomListViewBinding clvBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.custom_list_view, parent, false);
        Gson gson = new Gson();
        User user = gson.fromJson(allDataListFromFirebase.get(position).getFirebaseValue(), User.class);
        clvBinding.setUserModel(user);
        return clvBinding.getRoot();
    }
}
