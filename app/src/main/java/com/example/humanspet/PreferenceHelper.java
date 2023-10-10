package com.example.humanspet;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {
    private final String INTRO = "intro";
    private final String NAME = "name";
    private final String PASSWORD = "password";
    private final String EMAIL = "email";
    private final String ADDRESS = "address";
    private final String PHONE = "phone";
    private SharedPreferences app_prefs;
    private Context context;
    public void putPassword(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PASSWORD, loginOrOut);
        edit.apply();
    }
    public String getPassword() {
        return app_prefs.getString(PASSWORD, "");
    }
    public void putEmail(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(EMAIL, loginOrOut);
        edit.apply();
    }
    public String getEmail() {
        return app_prefs.getString(EMAIL, "");
    }

    public void putAddress(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(ADDRESS, loginOrOut);
        edit.apply();
    }
    public String getAddress() {
        return app_prefs.getString(ADDRESS, "");
    }

    public void putPhone(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PHONE, loginOrOut);
        edit.apply();
    }
    public String getPhone() {
        return app_prefs.getString(PHONE, "");
    }

    public PreferenceHelper(Context context)
    {
        app_prefs = context.getSharedPreferences("shared", 0);
        this.context = context;
    }

    public void putIsLogin(boolean loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putBoolean(INTRO, loginOrOut);
        edit.apply();
    }

    public void putName(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(NAME, loginOrOut);
        edit.apply();
    }

    public String getName()
    {
        return app_prefs.getString(NAME, "");
    }


}
