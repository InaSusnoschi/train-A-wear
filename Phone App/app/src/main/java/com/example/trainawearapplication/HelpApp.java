package com.example.trainawearapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * The HelpApp activity contains four expandable views with more information on the app and system
 *
 * @author  Ioana Susnoschi
 * @version 1.0
 * @since   2019-04-13
 */


public class HelpApp extends AppCompatActivity {

    final String TAG = "HelpApp";

    LinearLayout linearLayoutFault, linearLayoutMount, linearLayoutHealth, layoutHelpButtons;
    Button buttonFault, buttonMount, buttonHealth, buttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_app);

        Intent helpApp = getIntent();

        linearLayoutFault = findViewById(R.id.faultLayout);
        linearLayoutMount = findViewById(R.id.mountLayout);
        linearLayoutHealth = findViewById(R.id.healthLayout);
        layoutHelpButtons = findViewById(R.id.layoutHelpButtons);
        buttonFault = findViewById(R.id.buttonFault) ;
        buttonMount =  findViewById(R.id.buttonMount) ;
        buttonHealth =  findViewById(R.id.buttonHealth) ;
        buttonExit = findViewById(R.id.buttonExitHelp);

        buttonFault.setOnClickListener(buttonFaultyOnClickListener);
        buttonMount.setOnClickListener(buttonMountOnClickListener);
        buttonHealth.setOnClickListener(buttonHealthOnClickListener);
        buttonExit.setOnClickListener(buttonExitPress);
    }

    // when the user presses the Faulty button, the layout showing information appears
    View.OnClickListener buttonFaultyOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG,"set visibility of layout");
            linearLayoutFault.setVisibility(View.VISIBLE);
            layoutHelpButtons.setVisibility(View.GONE);
            buttonExit.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener buttonMountOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG,"set visibility of mount layout");
            linearLayoutMount.setVisibility(View.VISIBLE);
            layoutHelpButtons.setVisibility(View.GONE);
            buttonExit.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener buttonHealthOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG,"set visibility of health layout");
            linearLayoutHealth.setVisibility(View.VISIBLE);
            layoutHelpButtons.setVisibility(View.GONE);
            buttonExit.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener buttonExitPress= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG,"set visibility of layout");
            linearLayoutFault.setVisibility(View.GONE);
            linearLayoutMount.setVisibility(View.GONE);
            linearLayoutHealth.setVisibility(View.GONE);
            buttonExit.setVisibility(View.GONE);
            layoutHelpButtons.setVisibility(View.VISIBLE);
        }
    };
}

