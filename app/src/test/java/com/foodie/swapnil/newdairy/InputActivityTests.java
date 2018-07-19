package com.foodie.swapnil.newdairy;

import android.content.Intent;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by sumit on 7/11/2018.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)

public class InputActivityTests {
    InputActivity activity;
    EditText etCowId;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(InputActivity.class)
                .create()
                .resume()
                .get();
        etCowId = activity.findViewById(R.id.etId);

    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void continueShouldLaunchDisplayActivity() {
        // define the expected results
        Intent expectedIntent = new Intent(activity, DisplayActivity.class);
        // click the continue button
        activity.findViewById(R.id.textView).callOnClick();
        // get the actual results
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();
        // check if the expected results match the actual results
        assertTrue(expectedIntent.filterEquals(actualIntent));
    }

    @Test
    public void checkCowId(){

        assertNotNull("Cow number is null",etCowId);
        assertTrue("It shold not be empty",!etCowId.getText().toString().isEmpty() );

        activity.findViewById(R.id.btnSubmit).callOnClick();
    }
}

