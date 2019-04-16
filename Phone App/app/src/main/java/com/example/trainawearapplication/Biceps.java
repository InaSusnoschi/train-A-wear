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


public class Biceps extends AppCompatActivity  implements UpdateFeedback{
    /**
     * @param mImages Array containing the drawable resources for the Carousel view showing bicep curls
     * @param mImagesTitle Holds the titles of the images
     */
    final String TAG = "Biceps";

    UdpClientHandlerBicep udpClientHandler;
    private final int PORT = 31415;
    private Thread udpListener;
    private ClientSend udpSender;


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

        buttonConnect = findViewById(R.id.buttonStartBiceps);
        buttonDisconnect = findViewById(R.id.buttonStopBiceps);
        textViewState = findViewById(R.id.stateBicep); //
        textViewRx = findViewById(R.id.receivedBicep); //
        textFeedback = findViewById(R.id.feedbackBiceps); //

//        udpClientHandler = new ClassicSquat.UdpClientHandler(ClassicSquat parent);
        udpClientHandler = new com.example.trainawearapplication.UdpClientHandlerBicep();

        Log.d(TAG, "Start listening for messages");
        udpListener = new Thread(new com.example.trainawearapplication.ClientListenBicep(PORT, textViewState, udpClientHandler));
        udpListener.start();

        udpSender = new ClientSend("255.255.255.255", PORT);
        Log.d(TAG, "sending to " + textViewState.getText().toString());
        udpSender.execute("train-a-wear ready");
    }

    // when user presses START
    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Log.d(TAG,"Connecting");
                    Log.d(TAG,"connect button state");
                    buttonConnect.setEnabled(false);
                    buttonConnect.setVisibility(View.GONE);
                    buttonDisconnect.setEnabled(true);
                    buttonDisconnect.setVisibility(View.VISIBLE);

                    // start Send thread

                    udpSender = new ClientSend(textViewState.getText().toString(), PORT);
                    Log.d(TAG, textViewState.getText().toString());
                    Log.d(TAG, "sending to " + textViewState.getText().toString());
                    udpSender.execute("STARTbiceps");
                }
            };
    // disconnect on button press
    View.OnClickListener buttonDisonnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Log.d(TAG,"Disconnecting");
//                    udpClientThread.stopClient();
//                    textViewState.setText("clientEnd()");
                    Log.d(TAG,"disconnect button state");
                    buttonConnect.setEnabled(true);
                    buttonConnect.setVisibility(View.VISIBLE);
                    buttonDisconnect.setEnabled(false);
                    buttonDisconnect.setVisibility(View.GONE);

                    udpSender = new ClientSend(textViewState.getText().toString(), PORT);
                    Log.d(TAG, "sending stop msg " + textViewState.getText().toString());
                    udpSender.execute("STOPbiceps");
                }
            };


    public static String showFeedback (String instructionDef){
        switch (instructionDef){
            case "0":
                return("Ready to start");

            case "1":
                return ("Let's go!");

            case "5":
                return ("Try to keep you back in a neutral position, don't lift your hips");

            case "6":
                return ("If you keep your hips steady, the abs are working harder");

            case "7":
                return ("For an effective push up, bring your chest close to the ground");

            default:
                return("You're doing great, keep going!");
        }
    }
    /**
     * Method required by the UpdateFeedback interface
     * @param view
     * @param text
     */

    @Override
    public void setViewText(TextView view, String text) {


    }
}
