package com.inu.killergy.MyInfo;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManger {
    public static final String MY_INFO_PREFERENCE = "my_info";
    private Context context;
    public SharedPreferenceManger(Context context){
        this.context = context;
    }

    public void saveString(String preference,String key,String value){
        getSharedPreference(preference).edit().putString(key,value).apply();
    }

    public String getString(String preference,String key){
        return getSharedPreference(preference).getString(key,"");
    }

    private SharedPreferences getSharedPreference(String preference){
        return context.getSharedPreferences(preference, Context.MODE_PRIVATE);
    }

}
