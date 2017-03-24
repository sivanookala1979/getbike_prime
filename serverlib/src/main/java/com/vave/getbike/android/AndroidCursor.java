/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.android;

import android.database.Cursor;

import com.vave.getbike.datasource.BaseDataSource;

import java.util.HashMap;

/**
 * @author suzuki
 * @version 1.0, 16-Jul-2015
 */
public class AndroidCursor implements ICursor {

    Cursor cursor;

    public AndroidCursor(Cursor cursor) {
        super();
        this.cursor = cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public HashMap<String, Object> getItemAt(int index, BaseDataSource baseDataSource) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        cursor.moveToPosition(index);
        for (String column : baseDataSource.getAllColumns()) {
            result.put(column, cursor.getString(cursor.getColumnIndex(column)));
        }
        return result;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }
}
