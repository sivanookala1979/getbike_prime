package com.vave.getbike.syncher;

import com.vave.getbike.helper.DefaultExceptionHandler;
import com.vave.getbike.helper.ExceptionHandler;

/**
 * Created by sivanookala on 25/10/16.
 */

public class BaseSyncher {

    public static ExceptionHandler exceptionHandler = new DefaultExceptionHandler();

    //public static String BASE_URL = "http://localhost:9000";
    //public static String BASE_URL = "http://192.168.10.18:9000";
    //public static String BASE_URL = "http://videos.meritcampus.com:9000";
    public static String BASE_URL = "http://server.getbike.co.in";
    //public static String BASE_URL = "http://test.getbike.co.in";

    public static String accessToken = null;

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        BaseSyncher.accessToken = accessToken;
    }

    protected static void handleException(Exception e) {
        exceptionHandler.handleException(e);
    }

    public static void testSetup() {
        BaseSyncher.setAccessToken("1dd8344f-69d9-4e93-b5fe-3222edad6d85");
        //  BaseSyncher.setAccessToken("3584e6e4-a461-4660-a3d0-719a9f121b0c");
    }
}
