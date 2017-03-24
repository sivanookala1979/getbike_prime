/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.android;

import android.database.sqlite.SQLiteDatabase;

import com.vave.getbike.android.IDatabaseHelper;

/**
 * @author suzuki
 * @version 1.0, 16-Jul-2015
 */
public class CeroneDatabaseHelper implements IDatabaseHelper {

    @Override
    public void close() {
    }

    @Override
    public SQLiteDatabase getWritableDatabase2() {
        throw new RuntimeException();
    }
}
