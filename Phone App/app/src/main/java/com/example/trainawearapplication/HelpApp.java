package com.example.trainawearapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

/**
 * The HelpApp activity contains four expandable views with more information on the app and system
 *
 * @author  Ioana Susnoschi
 * @version 1.0
 * @since   2019-04-13
 */


public class HelpApp extends AppCompatActivity {

    final String TAG = "HelpApp";

    LinearLayout linearLayoutFault, linearLayoutMount, linearLayoutHealth, linearLayoutStretch, layoutHelpButtons;
    Button buttonFault, buttonMount, buttonHealth, buttonStretch, buttonExit;

    private int stretchImages[] = new int[]{
            R.drawable.tricepstretch, R.drawable.lunge, R.drawable.hamstring
    };

    private String[] stretchImagesTitle = new String[]{
            "Str1", "Str2", "Str3"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_app);

        Intent helpApp = getIntent();

        linearLayoutFault = findViewById(R.id.faultLayout);
        linearLayoutMount = findViewById(R.id.mountLayout);
        linearLayoutHealth = findViewById(R.id.healthLayout);
        linearLayoutStretch = findViewById(R.id.stretchLayout);
        layoutHelpButtons = findViewById(R.id.layoutHelpButtons);

        buttonFault = findViewById(R.id.buttonFault) ;
        buttonMount =  findViewById(R.id.buttonMount) ;
        buttonHealth =  findViewById(R.id.buttonHealth) ;
        buttonStretch = findViewById(R.id.buttonStretch);
        buttonExit = findViewById(R.id.buttonExitHelp);

        buttonFault.setOnClickListener(buttonFaultyOnClickListener);
        buttonMount.setOnClickListener(buttonMountOnClickListener);
        buttonHealth.setOnClickListener(buttonHealthOnClickListener);
        buttonStretch.setOnClickListener(buttonStretchOnClickListener);
        buttonExit.setOnClickListener(buttonExitPress);

        // set carousel view
        CarouselView carouselView = findViewById(R.id.carouselStr);
        carouselView.setPageCount(stretchImages.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(stretchImages[position]);
            }
        });

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(HelpApp.this, stretchImagesTitle[position], Toast.LENGTH_SHORT).show();
            }
        });
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

    View.OnClickListener buttonStretchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG,"set visibility of health layout");
            linearLayoutStretch.setVisibility(View.VISIBLE);
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
            linearLayoutStretch.setVisibility(View.GONE);
            buttonExit.setVisibility(View.GONE);
            layoutHelpButtons.setVisibility(View.VISIBLE);
        }
    };
}

