package com.example.first_app;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class SquatInstr extends AppCompatActivity {
    private static final String TAG = "SquatInstruction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat_instr);
        Log.d(TAG, "make instruction view");
    }

    Intent squatInstr = getIntent();
}




