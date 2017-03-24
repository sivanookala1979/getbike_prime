package com.vave.getbike.utils;

import org.junit.Test;

import java.util.List;

import static com.vave.getbike.utils.GetBikeUtils.createList;
import static com.vave.getbike.utils.GetBikeUtils.trimList;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by sivanookala on 19/01/17.
 */

public class GetBikeUtilsTest {

    @Test
    public void trimListTESTHappyFlow() {
        List<String> input = createList("Siva", "Rama", "Krishna", "Nookala");
        assertEquals(createList("Siva", "Rama", "Krishna", "Nookala"), trimList(input, 4));
        assertEquals(createList("Siva", "Rama", "Krishna", "Nookala"), trimList(input, 10));
        assertEquals(createList("Siva", "Krishna"), trimList(input, 2));
        assertEquals(createList("Siva", "Rama", "Krishna", "Nookala"), trimList(input, 3));
    }

}
