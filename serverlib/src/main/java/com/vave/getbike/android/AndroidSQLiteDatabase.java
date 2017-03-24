/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.android;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author suzuki
 * @version 1.0, 16-Jul-2015
 */
public class AndroidSQLiteDatabase implements ISQLiteDatabase {

	SQLiteDatabase sqliteDatabase;

	public AndroidSQLiteDatabase(SQLiteDatabase sqliteDatabase) {
		super();
		this.sqliteDatabase = sqliteDatabase;
	}

	@Override
	public long insert(String tableName, String object, IContentValues newValues) {
		return sqliteDatabase.insert(tableName, object,
				getContentValues(newValues));
	}

	@Override
	public int delete(String tableName, String whereClause, String[] whereArgs) {
		return sqliteDatabase.delete(tableName, whereClause, whereArgs);
	}

	@Override
	public ICursor query(String tableName, String[] allColumns,
						 String selection, String[] selectionArgs, String groupBy,
						 String having, String orderBy) {
		return new AndroidCursor(sqliteDatabase.query(tableName, allColumns,
				selection, selectionArgs, groupBy, having, orderBy));
	}

	@Override
	public int update(String tableName, IContentValues newValues,
			String whereClause, String[] whereArgs) {
		return sqliteDatabase.update(tableName, getContentValues(newValues),
				whereClause, whereArgs);
	}

	private ContentValues getContentValues(IContentValues newValues) {
		return ((AndroidContentValues) newValues).getContentValues();
	}

	@Override
	public long create(String tableName, String[] allColumns) {
		return 0;
	}
}
