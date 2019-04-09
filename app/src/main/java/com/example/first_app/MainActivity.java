package com.example.first_app;

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

public class MainActivity extends AppCompatActivity {
//    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
//    public static final int SERVER_PORT = 65000;
    static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv = (TextView) findViewById(R.id.tv);
        Button wordpress = (Button) findViewById(R.id.buttonWordpress);
        Button insta = (Button) findViewById(R.id.buttonInsta);

        Context context = getApplicationContext();

        tv.setText(tv.getText() + "\nScreen height : "
                + getScreenHeightInDPs(context) + "dp");

        //Register onClick listener
        wordpress.setOnClickListener(wordpressButtonListener);
        insta.setOnClickListener(instaButtonListener);

    }

    private View.OnClickListener wordpressButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://trainawear.wordpress.com/"));
            startActivity(i);
        }
    };

    private View.OnClickListener instaButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/trainawear/"));
            startActivity(i);
        }
    };

    /* FROM HERE OLD CODE
    /** Called when the user taps the Setup button to transfer the message*/

    public void enterSetup (View view) {
        Intent setup = new Intent(this, Setup.class);
        startActivity(setup);
    }

    /**
     * background process for connecting to RPI and sending get request
     */


    private class Background_get extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params){
            try{
                /* Change IP to IP set in arduino sketch

                 */
                URL url = new URL("http://192.168.1.148/?" + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    result.append(inputLine).append("\n");

                in.close();
                connection.disconnect();
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    /** Called when user taps the "workout" button*/
    public void enterWorkout (View view) {
        // enter new screen in response to button
        Intent workout = new Intent(this, Workout.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(workout);
    }

    public void enterHistory (View view) {
        Intent history = new Intent(this, History.class);
        startActivity(history);
        Log.d(TAG, "enter history");
    }

    public static int getScreenHeightInDPs(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        /*
            In this example code we converted the float value
            to nearest whole integer number. But, you can get the actual height in dp
            by removing the Math.round method. Then, it will return a float value, you should
            also make the necessary changes.
        */

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
