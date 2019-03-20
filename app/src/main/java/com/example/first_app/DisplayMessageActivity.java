package com.example.first_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextWatcher;
import android.widget.Toast;

import static com.example.first_app.R.id.editAge;
import static com.example.first_app.R.id.textAllSet;

/**
 * <h1> Setup Activity</h1>
 * This class contains the Setup screen activity.
 * This is where the user is asked to input their parameters
 *
 * @author Ioana Susnoschi
 * @version 1.0
 * @since 19/03/2019

 */

public class DisplayMessageActivity extends AppCompatActivity {

    int num1;
    int num2;
    int num3;
    EditText height;
    EditText weight;
    EditText age;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_message);

        height = (EditText) findViewById(R.id.editHeight);
        weight = (EditText) findViewById(R.id.editWeight);
        age = (EditText) findViewById(editAge);
        // text watchers
        height.addTextChangedListener(textWatcher);
        weight.addTextChangedListener(textWeightWatcher);
        age.addTextChangedListener(textAgeWatcher);

        // buttons
        Button done = (Button)findViewById(R.id.buttonDone);

        //Register onClick listener
        done.setOnClickListener(doneButtonListener);

        /** This gets the parameters values and checks they are the right format*/
        //height = findViewById(R.id.enterHeight);

        /**
         * Get the Intent that started this activity and extract the string
          */

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        /**
         * Capture the layout's TextView and set the string as its text
         */
        TextView textView = findViewById(R.id.textRequest);
        textView.setText(message);
    }

    TextWatcher textWatcher = new TextWatcher(){

    /**
     * TextWatcher method is used to check the parameters input by the users are within specified limits
     * @param s default method parameter
     * @param start default method parameter
     * @param count default method parameter
     * @param after default method parameter
     */
    @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        /**
         * Condition: person height between 140 and 220cm
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (height.getText().length() > 2) {
                int num = Integer.parseInt(height.getText().toString());
                if (num >= 130 && num <= 220) {
                    //save the number
                    num1 = num;
                } else {
                    Toast.makeText(DisplayMessageActivity.this, "Please enter height in the range of 140-220cm", Toast.LENGTH_SHORT).show();
                    height.setText("");
                    num1 = -1;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
//            if (height.getText().length() <= 2) {
//                Toast.makeText(DisplayMessageActivity.this, "Please enter height in the range of 140-220cm", Toast.LENGTH_SHORT).show();
//                height.setText("");
//                num1 = -1;
//            }
            if (num1==-1)
            {
                Toast.makeText(DisplayMessageActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
            }
        }
        };


    // SECOND TEXT WATCHER: weight

    TextWatcher textWeightWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s2, int start2, int count2, int after2) {

        }
        // Condition: person weight between 40 and 120kg
        @Override
        public void onTextChanged(CharSequence s2, int start2, int before2, int count2) {
            if (weight.getText().length() > 1) {
                int num_w = Integer.parseInt(weight.getText().toString());
                if (num_w >= 40 && num_w <= 120) {
                    //save the number
                    num2 = num_w;
                } else {
                    Toast.makeText(DisplayMessageActivity.this, "Please enter weight in the range 40-120kg", Toast.LENGTH_SHORT).show();
                    weight.setText("");
                    num2 = -1;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s2) {

            if (num3==-1)
            {
                Toast.makeText(DisplayMessageActivity.this,"Please fill the field",Toast.LENGTH_SHORT).show();
            }
        }
    };

    // TEXT watcher for Age

    TextWatcher textAgeWatcher;

    {
        textAgeWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s3, int start3, int count3, int after3) {

            }

            // Condition: person age between 16 and 90 y.o.
            @Override
            public void onTextChanged(CharSequence s3, int start3, int before3, int count3) {


                    int num_a;
                    num3 = 0;

                    /**
                    * Use try-catch to get value of input text
                     *
                     *
                 */
                try {
                        num_a = Integer.parseInt(age.getText().toString());
                    } catch (NumberFormatException e) {
                        num_a = 0;
                    }

                    if (num_a >= 16 && num_a <= 90) {
                        //save the number
                        num3 = num_a;
                    } else {
                        if(num_a != 0){
                        Toast.makeText(DisplayMessageActivity.this, "Please enter age in the range 16-90 y.o.", Toast.LENGTH_SHORT).show();
                        num3 = -1;}

                    }

                }

            @Override
            public void afterTextChanged(Editable s2) {

                if (num3 == 0) {
                    Toast.makeText(DisplayMessageActivity.this, "Please fill the field", Toast.LENGTH_SHORT).show();

                }
            }
        };
    }


    /**
     * Use a listener to make a line of text appear after button press
     */
    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView viewAllSet = findViewById(R.id.textAllSet);
            /**
             *  change visibility of the text (previously set as invisible)
             */
            viewAllSet.setVisibility(View.VISIBLE);

            DisplayMessageActivity.this.finish();
        }
    };

}