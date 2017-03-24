package com.vave.getbike.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adarsht on 06/12/16.
 */

public class StringUtils {
    public static boolean isStringValid(String refString) {
        return (refString!=null) && !refString.isEmpty();
    }

    public static List<String> getStringsList(String ...data) {
        List<String> list = new ArrayList<>();
        for (String name:data) {
            list.add(name);
        }
        return  list;
    }

    public static double getDouble(String stringData) {
        double data = 0.0;
        if(stringData!= null && !stringData.isEmpty()){
            data = Double.parseDouble(stringData);
        }
        return data;
    }

    public static String getTwodigitString(int hourOfDay) {
        return (hourOfDay>9)? hourOfDay+"" : "0"+hourOfDay;
    }
}
