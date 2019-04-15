package com.example.trainawearapplication;


import android.support.test.rule.ActivityTestRule;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SetupInstrumentationTest {

    //declare an activity to test. replace Setup with name of activity class you want to test. (i.e MainActivity)
    public final ActivityTestRule<Setup> activityActivityTestRule =
            new ActivityTestRule<>(Setup.class);


    private String height_tobe_typed= "160";
    private String weight = "60";
    private String age = "25";


    //first test example - needs to be of type void or to return something
    @Test
    public void login_success(){
        //always launch your activity. No need to declare intent so it can be null
        activityActivityTestRule.launchActivity(null);
        Log.e("@Test","Performing login success test");

        //iterate through various buttons, text fields etc,
        // //but always remember that activities need to be tested in sequence
        // and always from the first step until the last one you want to test
        Espresso.onView((withId(R.id.editHeight)))
                .perform(ViewActions.typeText(height_tobe_typed));

        Espresso.onView(withId(R.id.editWeight))
                .perform(ViewActions.typeText(weight));

        Espresso.onView(withId(R.id.editAge))
                .perform(ViewActions.typeText(age));

        Espresso.onView(withId(R.id.buttonDone))
                .perform(ViewActions.click());

        //checking for a text being displayed is needs to have a matcher for the text and a matcher for the view
        //in this case check(matches(withText will check the text
        //and check(matches(isDisplayed() will validate the view which contains the text is displayed

       /* Espresso.onView(withId(R.id.textAllSet)).check(matches(withText(R.string.ready)))
                .check(matches(isDisplayed()));*/
    }

}
