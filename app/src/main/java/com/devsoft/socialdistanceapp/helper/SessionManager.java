package com.devsoft.socialdistanceapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    public static  String SHARED_PREF="my_preference";
    public static  String PREF_LOGIN="PREF_LOGIN";
    public static  String PREF_LAT="PREF_LAT";
    public static  String PREF_LONG="PREF_LONG";


    SharedPreferences.Editor editor;
    SharedPreferences preference;
    public SessionManager(Context context){
        preference = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        editor = preference.edit();
    }
    public boolean isLogIn(){
        return preference.getBoolean(PREF_LOGIN,false);
    }
    public void setLogIn(boolean flag){
        editor.putBoolean(PREF_LOGIN,flag);
        editor.commit();
    }

    public double getLat(){
        return preference.getLong(PREF_LAT,0);
    }
    public void setLat(double flag){
        editor.putLong(PREF_LAT, (long) flag);
        editor.commit();
    }

    public double getLong(){
        return preference.getLong(PREF_LONG,0);
    }
    public void setLong(double flag){
        editor.putLong(PREF_LONG,(long)flag);
        editor.commit();
    }

}
