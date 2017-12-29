package com.fastbase.support;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fastbase.Splash_Activity;

/**
 * Created by Swapnil.Patel on 23-12-2017.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    public static final String PREF_NAME = "fastbase";

    public static final String Is_Login = "IsLoggedIn";

    public static final String Pm_token = "token";

    public static final String Pm_UserName = "UserName";
    public static final String Pm_UserType = "UserType";
    public static final String Pm_UserEmail = "UserEmail";
    public static final String Pm_UserImage = "UserImage";


    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveLogin(String token, String name, String type, String email, String image) {
        editor.putBoolean(Is_Login, true);
        editor.putString(Pm_token, token);
        editor.putString(Pm_UserName, name);
        editor.putString(Pm_UserType, type);
        editor.putString(Pm_UserEmail, email);
        editor.putString(Pm_UserImage, image);

        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Is_Login, false);
    }

    public void logOut() {
        editor.clear();
        editor.commit();

        Intent in = new Intent(context, Splash_Activity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(in);
    }
}
