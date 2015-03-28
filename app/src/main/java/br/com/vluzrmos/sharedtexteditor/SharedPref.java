package br.com.vluzrmos.sharedtexteditor;

import android.content.SharedPreferences;

public class SharedPref {
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;


    public static void setShared(SharedPreferences _prefs){
        prefs  = _prefs;
        editor = _prefs.edit();
    }

    public static SharedPreferences getPrefs(){
        return prefs;
    }

    public static SharedPreferences.Editor getEditor(){
        return editor;
    }

}
