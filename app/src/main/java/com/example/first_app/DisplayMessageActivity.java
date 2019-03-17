package com.example.first_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    EditText height;
    EditText weight;
    EditText age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        /** This gets the parameters values and checks they are the right format*/
        //height = findViewById(R.id.enterHeight);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textRequest);
        textView.setText(message);
    }


}
