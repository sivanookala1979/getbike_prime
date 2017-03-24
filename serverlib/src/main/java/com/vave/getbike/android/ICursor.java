/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.android;

import com.vave.getbike.datasource.BaseDataSource;

import java.util.HashMap;

/**
 * @author suzuki
 * @version 1.0, 16-Jul-2015
 */
public interface ICursor {

    public HashMap<String, Object> getItemAt(int index, BaseDataSource baseDataSource);

    public int getCount();
}
