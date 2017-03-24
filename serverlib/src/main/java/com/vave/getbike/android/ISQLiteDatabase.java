/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.android;


/**
 * @author suzuki
 * @version 1.0, 16-Jul-2015
 */
public interface ISQLiteDatabase {

	long create(String tableName, String[] allColumns);

	long insert(String tableName, String object, IContentValues newValues);

	int delete(String tableName, String whereClause, String[] whereArgs);

	ICursor query(String tableName, String[] allColumns, String selection,
				  String[] selectionArgs, String groupBy, String having,
				  String orderBy);

	int update(String tableName, IContentValues newValues, String string,
			   String[] strings);

}
