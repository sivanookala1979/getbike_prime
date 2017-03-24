package com.vave.getbike.android;

/**
 * Created by sivanookala on 25/11/16.
 */

public class CeroneLogger implements ILogger {

    @Override
    public int d(String tag, String msg) {
        System.out.println(tag + " " + msg);
        return 0;
    }

    @Override
    public int v(String tag, String msg) {
        System.out.println(tag + " " + msg);
        return 0;
    }

    @Override
    public int i(String tag, String msg) {
        System.out.println(tag + " " + msg);
        return 0;
    }

    @Override
    public int w(String tag, String msg) {
        System.out.println(tag + " " + msg);
        return 0;
    }

    @Override
    public int e(String tag, String msg) {
        System.out.println(tag + " " + msg);
        return 0;
    }
}
