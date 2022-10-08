package com.example.comp9900_commercialize.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private  final SharedPreferences sharedPreferences;

    // Constructor
    public Preferences(Context context){
        sharedPreferences = context.getSharedPreferences("SP", Context.MODE_PRIVATE);
    }

    // Implement functions of setting and getting values.
    public void putBoolean(String key, boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key){
        return sharedPreferences.getString(key, null);
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
