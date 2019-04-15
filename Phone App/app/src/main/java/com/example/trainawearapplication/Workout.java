package com.example.trainawearapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @file Workout activity
 * @author Ioana Susnoschi
 * @brief Parents the three workout types and displays a short description of each. Implements
 * ModifiedScrollView
 */
public class Workout extends AppCompatActivity {
    final String TAG = "Workout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        Intent workout = getIntent();

    }

    /** Called when the user presses Squat button     */
    public void enterSquat(View view) {
        Intent squat = new Intent(this, Squat.class);
        startActivity(squat);
    }

    public void enterBiceps(View view) {
        Intent biceps = new Intent(this, Biceps.class);
        startActivity(biceps);
    }


    public void enterPlank(View view) {
        Intent plank = new Intent(this, Plank.class);
        startActivity(plank);
    }

}
