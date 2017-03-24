package com.vave.getbike.utils;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Created by sivanookala on 26/10/16.
 */

public class GetBikeTestUtils {

    @NonNull
    public static BaseMatcher<View> isPositive() {
        return new BaseMatcher<View>() {
            @Override
            public boolean matches(Object item) {
                TextView textView = (TextView) item;
                boolean result = false;
                if (textView.getText() != null) {
                    if (Double.parseDouble(textView.getText() + "") > 0) {
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected the value to be greater than " + 0);
            }
        };
    }

    @NonNull
    public static BaseMatcher<View> greaterThan(final int compareWith) {
        return new BaseMatcher<View>() {
            @Override
            public boolean matches(Object item) {
                TextView textView = (TextView) item;
                boolean result = false;
                if (textView.getText() != null) {
                    if (Integer.parseInt(textView.getText() + "") > compareWith) {
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected the value to be greater than " + compareWith);
            }
        };
    }
}
