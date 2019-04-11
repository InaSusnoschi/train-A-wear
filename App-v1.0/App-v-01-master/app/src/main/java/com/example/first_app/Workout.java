package com.example.first_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.first_app.vs.Plank;

public class Workout extends AppCompatActivity {

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

    public void enterLunge(View view) {
        Intent lunge = new Intent(this, Lunge.class);
        startActivity(lunge);
    }

    public void enterPlank(View view) {
        Intent plank = new Intent(this, Plank.class);
        startActivity(plank);
    }

}
