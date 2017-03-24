package com.vave.getbike.datasource;

import com.vave.getbike.android.AndroidStubsFactory;

import org.junit.Before;

/**
 * Created by sivanookala on 26/10/16.
 */
public class BaseDataSourceTest {

    @Before
    public void setUp()
    {
        AndroidStubsFactory.IS_TEST = true;
    }

}
