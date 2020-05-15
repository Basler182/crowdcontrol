package com.ks.crowdcontrol.persistance.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ShoppingListDAO {

    public static boolean save(Context context, ArrayList<String> stringArrayList){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shopping_list", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(stringArrayList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ArrayList", json);
        return editor.commit();
    }

    public static ArrayList<String> load(Context context){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("shopping_list", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("ArrayList", "");
        ArrayList<String> stringArrayList = new ArrayList<>();
        if(json.isEmpty()){
            //do nothing
        }else{
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
           stringArrayList = gson.fromJson(json, type);
        }
        return  stringArrayList;
    }
}
