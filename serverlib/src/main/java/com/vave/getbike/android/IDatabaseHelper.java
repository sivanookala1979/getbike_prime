/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.android;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author suzuki
 * @version 1.0, 16-Jul-2015
 */
public interface IDatabaseHelper {

    void close();

    SQLiteDatabase getWritableDatabase2();
}
