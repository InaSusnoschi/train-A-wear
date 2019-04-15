package com.example.trainawearapplication;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import static com.example.trainawearapplication.ClassicSquat.showFeedback;


public class Biceps extends AppCompatActivity {
    /**
     * @param mImages Array containing the drawable resources for the Carousel view showing bicep curls
     * @param mImagesTitle Holds the titles of the images
     */
    final String TAG = "Biceps";

    // declare carousel parameters with 3 images
    private int mImages[] = new int[]{
            R.drawable.pullup1, R.drawable.pullup2, R.drawable.pullup3
    };

    private String[] mImagesTitle = new String[]{
            "Pullup1", "Pullup2", "Pullup3"
    };


    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonDisconnect;
    static TextView textViewState, textViewRx, textFeedback;

//    ClassicSquat.UdpClientHandler udpClientHandler;
//    UdpClientThread udpClientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biceps);

        Intent biceps = getIntent();

        // implement Carousel - can maybe make it separate class?

        CarouselView carouselView = findViewById(R.id.carousel);
        carouselView.setPageCount(mImages.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(mImages[position]);
            }
        });

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(Biceps.this, mImagesTitle[position], Toast.LENGTH_SHORT).show();
            }
        });

        buttonConnect =  findViewById(R.id.buttonStartBiceps);
        buttonDisconnect =  findViewById(R.id.buttonStopBiceps);
        textViewState = findViewById(R.id.state); //
        textViewRx = findViewById(R.id.received); //
        textFeedback = findViewById(R.id.feedbackBiceps); //

//        udpClientHandler = new ClassicSquat.UdpClientHandler(ClassicSquat parent);
    }



}
