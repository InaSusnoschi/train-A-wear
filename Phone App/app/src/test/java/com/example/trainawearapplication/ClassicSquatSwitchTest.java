package com.example.trainawearapplication;


import android.widget.TextView;

import com.example.trainawearapplication.ClassicSquat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

//import static com.example.first_app.ClassicSquat.textFeedback;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import static org.junit.Assert.assertThat;


public class ClassicSquatSwitchTest {

    private static ClassicSquat mActivitySquat;


    private static final int TEST_CASE1 = 0;
    private static final int TEST_CASE2 = 2;
    private static TextView outputText;

    //private MainActivity instructionExample;


    /**
     * @brief Test method for {@link ClassicSquat}
     */

    @Test
    public void testSwitchCase0(){

        mActivitySquat = new ClassicSquat();

        String instructionDef = "0";

        assertEquals("Ready to start", mActivitySquat.showFeedback(instructionDef));

    }

    @Test
    public void testSwitchCase1(){

        mActivitySquat = new ClassicSquat();

        String instructionDef = "1";

        assertEquals("Let's go!", mActivitySquat.showFeedback(instructionDef));

    }

    @Test
    public void testSwitchCase2(){

        mActivitySquat = new ClassicSquat();

        String instructionDef = "2";

        assertEquals("Knees caving in: Keep your knees in line with your toes", mActivitySquat.showFeedback(instructionDef));

    }


    @Test
    public void testSwitchCase3(){

        mActivitySquat = new ClassicSquat();

        String instructionDef = "3";

        assertEquals("Your back is bending, keep it straight while tensing your abs", mActivitySquat.showFeedback(instructionDef));

    }

    @Test
    public void testSwitchCase6(){

        mActivitySquat = new ClassicSquat();

        String instructionDef = "6";

        assertEquals("If you keep your hips steady, the abs are working harder", mActivitySquat.showFeedback(instructionDef));

    }

    @Test
    public void testSwitchCaseDefault(){

        mActivitySquat = new ClassicSquat();

        String instructionDef = "anything else";

        assertEquals("You're doing great, keep going!", mActivitySquat.showFeedback(instructionDef));

    }

    @Test
    public void testSwitchCaseFail(){

        mActivitySquat = new ClassicSquat();

        String instructionDef = "2";

        assertEquals("You're doing great, keep going!", mActivitySquat.showFeedback(instructionDef));

    }

}
