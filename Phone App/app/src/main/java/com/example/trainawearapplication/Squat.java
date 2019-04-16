package com.example.trainawearapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * @brief The Squat activity contains the three types of squats
 *
 * @author  Ioana Susnoschi
 * @version 1.0
 * @since   2019-01-31
 */
public class Squat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat);

        Intent squat = getIntent();
    }

    public void enterClassicSquat(View view) {
        Intent classicSquat = new Intent(this, ClassicSquat.class);
        startActivity(classicSquat);
    }

    public void enterPlieSquat(View view) {
        Intent plieSquat = new Intent(this, PlieSquat.class);
        startActivity(plieSquat);
    }
}

