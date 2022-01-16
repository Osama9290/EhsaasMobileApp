package com.example.ehsaas.UI.Preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.ehsaas.UI.SignUpLogin.SelectUserType;


public class UserPreferences {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String PREFER_NAME = "USER_PREF";

    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    public static final String KEY_EMAIL = "email";


    public UserPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }


    public String getUseremail() {
        return sharedPreferences.getString("User_email", "");
    }

    public void setUseremail(String useremaial) {
        editor.putString("User_email", useremaial).commit();
    }


    public String getUserId() {
        return sharedPreferences.getString("User_id", "");
    }

    public void setUserId(String userId) {
        editor.putString("User_id", userId).commit();
    }


    public String getUserType() {
        return sharedPreferences.getString("type", "");
    }

    public void setUserType(String userId) {
        editor.putString("type", userId).commit();
    }


    public void setPhone(String phone) {
        editor.putString("phone", phone).commit();
    }

    public String getPhone() {
        return sharedPreferences.getString("phone", "");
    }


    public String getPassword() {
        return sharedPreferences.getString("password", "");
    }

    public void setPassword(String password) {
        editor.putString("password", password).commit();
    }


    public String getPrivacy() {
        return sharedPreferences.getString("privacy", "");
    }

    public void setPrivacy(String password) {
        editor.putString("privacy", password).commit();
    }

    public String getDp() {
        return sharedPreferences.getString("dp", "");
    }

    public void setDp(String dp) {
        editor.putString("dp", dp).commit();
    }

    public String getLname() {
        return sharedPreferences.getString("lname", "");
    }

    public void setLname(String dp) {
        editor.putString("lname", dp).commit();
    }

    public String getFname() {
        return sharedPreferences.getString("fname", "");
    }

    public void setFname(String dp) {
        editor.putString("fname", dp).commit();
    }

    public void setDevice_token(String device_token) {
        editor.putString("Device_token", device_token).commit();
    }

    public String getDevice_token() {
        return sharedPreferences.getString("Device_token", "");
    }




    //Create login session
    public void createUserLoginSession(String email) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_EMAIL, email);

        editor.commit();
    }

    public boolean checkLogin() {
        if (this.isUserLoggedIn()) {


            return true;
        }
        return false;
    }

    public void logoutUser() {

        // Clearing all user data from Shared Preferences
        editor.remove("User_email");
        editor.remove("dp");
        editor.remove("User_id");
        editor.remove("lname");
        editor.remove("fname");
        editor.remove("password");
        editor.remove("phone");
        editor.remove("token");
        editor.remove(IS_USER_LOGIN);
        editor.commit();

        Intent i = new Intent(context, SelectUserType.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);

    }


    // Check for login
    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(IS_USER_LOGIN, false);

    }

}
