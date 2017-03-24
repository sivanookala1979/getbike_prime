package com.vave.getbike.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vave.getbike.model.Profile;
import com.vave.getbike.model.UserProfile;

/**
 * Created by sivanookala on 25/11/16.
 */

public class GetBikePreferences {

    private static final String LOGGED_IN = "loggedIn";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String PUBLICPROFILE = "publicProfile";
    private static final String USER_PROFILE = "userProfile";
    private static final String isTutorial_Completed = "completed";
    private static final String PROMOTIONS_BANNER_COMPLETED_ON = "01/01/2017";

    //UserProfile

    public static SharedPreferences preferences;

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static void setPreferences(Context context) {
        if (preferences == null) {
            GetBikePreferences.preferences = context.getSharedPreferences(
                    "com.vave.getbike", Context.MODE_PRIVATE);
        }
    }

    public static void reset() {
        preferences.edit().clear().commit();
    }

    public static String getAccessToken() {
        return preferences.getString(ACCESS_TOKEN, "");
    }

    public static void setAccessToken(String accessToken) {
        preferences.edit().putString(ACCESS_TOKEN, accessToken).commit();
    }

    public static void setIsTutorialCompleted(boolean isTutorialCompleted){
        preferences.edit().putBoolean(isTutorial_Completed,isTutorialCompleted).commit();
    }

    public static boolean isTutorialCompleted(){
        return preferences.getBoolean(isTutorial_Completed, false);
    }

    public static boolean isLoggedIn() {
        return preferences.getBoolean(LOGGED_IN, false);
    }

    public static void setLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(LOGGED_IN, loggedIn).commit();
    }
    public static Profile getPublicProfile() {
        java.lang.reflect.Type type = new TypeToken<Profile>() {
        }.getType();
        return new Gson().fromJson(preferences.getString(PUBLICPROFILE, ""), type);
    }

    public static void setPublicProfile(Profile profile) {
        String addressDetails = new Gson().toJson(profile);
        preferences.edit().putString(PUBLICPROFILE, addressDetails).commit();
    }

    public static UserProfile getUserProfile() {
        java.lang.reflect.Type type = new TypeToken<UserProfile>() {
        }.getType();
        return new Gson().fromJson(preferences.getString(USER_PROFILE, ""), type);
    }

    public static void setUserProfile(UserProfile profile) {
        String addressDetails = new Gson().toJson(profile);
        preferences.edit().putString(USER_PROFILE, addressDetails).commit();
    }

    public static String getPromotionsBannerCompletedOn() {
        return preferences.getString(PROMOTIONS_BANNER_COMPLETED_ON,"01/01/2017");
    }

    public static void setPromotionsBannerCompletedOn(String promotionsBannerCompletedOn) {
        preferences.edit().putString(PROMOTIONS_BANNER_COMPLETED_ON, promotionsBannerCompletedOn).commit();
    }

}
