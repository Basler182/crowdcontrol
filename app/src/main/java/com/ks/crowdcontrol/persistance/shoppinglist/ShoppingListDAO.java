package com.ks.crowdcontrol.persistance.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Class to save and load the Shopping List
 */
public class ShoppingListDAO {

    /*
     * Since you can only safe primitive key value pairs in the shared preferences, the function will convert the ArrayList
     * into a JSON String and safe it this way.
     */
    public static boolean save(Context context, ArrayList<String> stringArrayList){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shopping_list", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(stringArrayList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ArrayList", json);
        return editor.commit();
    }

    /*
     * Loads the Shopping List and returns a ArrayList<String>. This function will access the Shared Preferences and than
     * load the string, get its data type and convert it back to its original form.
     */
    public static ArrayList<String> load(Context context){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("shopping_list", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("ArrayList", "");
        ArrayList<String> stringArrayList = new ArrayList<>();
        if(!json.isEmpty()){
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
           stringArrayList = gson.fromJson(json, type);
        }
        return  stringArrayList;
    }
}
