package com.vave.getbike.android;

/**
 * Created by sivanookala on 25/11/16.
 */

public interface ILogger {

    public int d(String tag, String msg);  //Debug

    public int v(String tag,String msg);   //Verbose

    public int i(String tag,String msg);   //info

    public int w(String tag,String msg);   //warning

    public int e(String tag,String msg);   //error

}
