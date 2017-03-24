/*
 * (c) Copyright 2001-2015 COMIT AG
 * All Rights Reserved.
 */
package com.vave.getbike.android;




import com.vave.getbike.android.ICursor;
import com.vave.getbike.datasource.BaseDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author suzuki
 * @version 1.0, 16-Jul-2015
 */
public class CeroneCursor implements ICursor {

    List<HashMap<String, Object>> values = new ArrayList<HashMap<String, Object>>();

    public void addRow(HashMap<String, Object> row) {
        values.add(row);
    }

    public int getCount() {
        return values.size();
    }

    public HashMap<String, Object> getItemAt(int index, BaseDataSource baseDataSource) {
        return values.get(index);
    }
}
