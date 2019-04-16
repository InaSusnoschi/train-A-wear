package com.example.trainawearapplication;


import android.content.Context;
import android.content.res.Configuration;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The MainActivity is the first activity on entering the app
 *
 * @author  Ioana Susnoschi
 * @version 2.0
 * @since   2019-01-31
 */
public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv = (TextView) findViewById(R.id.tv);
        Button wordpress = (Button) findViewById(R.id.buttonWordpress);
        Button insta = (Button) findViewById(R.id.buttonInsta);
        Button tweet = (Button) findViewById(R.id.buttonTwitter);

        Context context = getApplicationContext();

        tv.setText(tv.getText() + "\nScreen height : "
                + getScreenHeightInDPs(context) + "dp");

        //Register onClick listener
        wordpress.setOnClickListener(wordpressButtonListener);
        insta.setOnClickListener(instaButtonListener);
        tweet.setOnClickListener(twitterButtonListener);

    }

    /**
     * @brief Linked to media
     * This OnClickListener is linked to the train-A-wear wordpress
     */
    private View.OnClickListener wordpressButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://trainawear.wordpress.com/"));
            startActivity(i);
        }
    };

    /**
     * @brief Linked to media
     * This OnClickListener is linked to the train-A-wear instagram
     */
    private View.OnClickListener instaButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/trainawear/"));
            startActivity(i);
        }
    };

    /**
     * @brief Linked to media
     * This OnClickListener is linked to the train-A-wear twitter page
     */
    private View.OnClickListener twitterButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/TrainAWear"));
            startActivity(i);
        }
    };


    /** Called when the user taps the Setup button */
    public void enterSetup (View view) {
        Intent setup = new Intent(this, Setup.class);
        startActivity(setup);
    }

    /** Called when user taps the "workout" button*/
    public void enterWorkout (View view) {
        // enter new screen in response to button
        Intent workout = new Intent(this, Workout.class);

        startActivity(workout);
    }

    /** Called when user taps the "Progress" button*/
    public void enterProgress (View view){
        Intent progress = new Intent(this, Progress.class);
        startActivity(progress);
    }

    /** Called when user taps the "Help" button*/
    public void enterHelpApp (View view){
        Intent helpApp = new Intent(this, HelpApp.class);
        startActivity(helpApp);
    }




//    private class Background_get extends AsyncTask<String, Void, String>{
//        @Override
//        protected String doInBackground(String... params){
//            try{
//                /* Change IP to IP set in arduino sketch
//
//                 */
//                URL url = new URL("http://192.168.1.148/?" + params[0]);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                StringBuilder result = new StringBuilder();
//                String inputLine;
//                while ((inputLine = in.readLine()) != null)
//                    result.append(inputLine).append("\n");
//
//                in.close();
//                connection.disconnect();
//                return result.toString();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }

    /**
     * Method used to obtain the dimensions of the screen
     * @param context
     * @return Height of the screen
     */
    public static int getScreenHeightInDPs(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        /*
            public int heightPixels
                The absolute height of the display in pixels.

            public float density
             The logical density of the display.
        */
        int heightInDP = Math.round(dm.heightPixels / dm.density);
        Log.d(TAG, "height is:" + heightInDP);
        System.out.println("height is: "+ heightInDP);
        return heightInDP;

    }

}
