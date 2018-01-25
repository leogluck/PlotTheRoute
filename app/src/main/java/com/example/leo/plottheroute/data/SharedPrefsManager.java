package com.example.leo.plottheroute.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

/**
 * Created by Leo on 25.01.2018
 */

public class SharedPrefsManager {
    private static final SharedPrefsManager sharedPrefsInstance = new SharedPrefsManager();
    private final String FILE_KEY = "FILE_KEY";
    private final String SAVED_TEXT = "SAVED_TEXT";

    public static SharedPrefsManager getInstance() {
        return sharedPrefsInstance;
    }

    public void savePrefs(Context context, String points){

        SharedPreferences sharedPref = context.getSharedPreferences(
                FILE_KEY, Context.MODE_PRIVATE);
        Editor ed = sharedPref.edit();
        ed.putString(SAVED_TEXT, points);
        ed.commit();
        Toast.makeText(context, "Points Saved", Toast.LENGTH_SHORT).show();
    }

    public String getPrefs(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                FILE_KEY, Context.MODE_PRIVATE);
        String points = sharedPref.getString(SAVED_TEXT, "");
        Toast.makeText(context, "Points Recalled", Toast.LENGTH_SHORT).show();
        return points;
    }

    private SharedPrefsManager() {
    }
}
