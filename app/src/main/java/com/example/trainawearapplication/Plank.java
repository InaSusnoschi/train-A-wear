package com.example.trainawearapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

public class Plank extends AppCompatActivity implements UpdateFeedback{

    final String TAG = "PlankActivity";
    public static int instruction = 1;

    /**
     * @brief Create Carousel view with three images
     */
    private int plankImages[] = new int[]{
            R.drawable.planklow2, R.drawable.plankhigh1
    };

    private String[] plankImagesTitle = new String[]{
            "PlankL", "PlankH"
    };

    public static int getInstruction() {
        return instruction;
    }

    public static void setInstruction(int instruction) {
        ClassicSquat.instruction = instruction;
    }

    Button buttonConnect, buttonDisconnect;
    static TextView textViewStatePlank, textViewRxPlank, textFeedbackPlank, messageView;

    UdpClientHandlerPlank udpClientHandler;

    private final int PORT = 31415;
    private Thread udpListener;
    private ClientSend udpSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plank);

        Intent plank = getIntent();

        // set carousel view
        CarouselView carouselView = findViewById(R.id.carouselPlank);
        carouselView.setPageCount(plankImages.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(plankImages[position]);
            }
        });

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(Plank.this, plankImagesTitle[position], Toast.LENGTH_SHORT).show();
            }
        });

        buttonConnect =  findViewById(R.id.buttonStartPlank); // in History
        buttonDisconnect =  findViewById(R.id.buttonStopPlank); // in History
        textViewStatePlank = findViewById(R.id.statePlank); // store IP address after handshake
        textViewRxPlank = findViewById(R.id.receivedPlank); // store numbers for feedback
        textFeedbackPlank = findViewById(R.id.feedbackPlank); // in Squat
        messageView = findViewById(R.id.message);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonDisconnect.setOnClickListener(buttonDisonnectOnClickListener);

        udpClientHandler = new com.example.trainawearapplication.UdpClientHandlerPlank();

        Log.d(TAG, "Start listening for messages");
        udpListener = new Thread(new com.example.trainawearapplication.ClientListen(PORT, textViewStatePlank, udpClientHandler));
        udpListener.start();

        udpSender = new ClientSend("255.255.255.255", PORT);
        Log.d(TAG, "sending to " + textViewStatePlank.getText().toString());
        udpSender.execute("train-a-wear ready");

    }

    // when user presses START
    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Log.d(TAG,"Connecting");
                    setInstruction(1);
                    Log.d(TAG,"connect button state");
                    buttonConnect.setEnabled(false);
                    buttonConnect.setVisibility(View.GONE);
                    buttonDisconnect.setEnabled(true);
                    buttonDisconnect.setVisibility(View.VISIBLE);

                    // start Send thread

                    udpSender = new ClientSend(textViewStatePlank.getText().toString(), PORT);
                    Log.d(TAG, textViewStatePlank.getText().toString());
                    Log.d(TAG, "sending to " + textViewStatePlank.getText().toString());
                    udpSender.execute("STARTplank");
                }
            };
    // disconnect on button press
    View.OnClickListener buttonDisonnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Log.d(TAG,"Disconnecting");
                    setInstruction(0);
//                    udpClientThread.stopClient();
//                    textViewState.setText("clientEnd()");
                    Log.d(TAG,"disconnect button state");
                    buttonConnect.setEnabled(true);
                    buttonConnect.setVisibility(View.VISIBLE);
                    buttonDisconnect.setEnabled(false);
                    buttonDisconnect.setVisibility(View.GONE);

                    udpSender = new ClientSend(textViewStatePlank.getText().toString(), PORT);
                    Log.d(TAG, "sending stop msg " + textViewStatePlank.getText().toString());
                    udpSender.execute("STOPplank");
                }
            };


    public static String showFeedback (String instructionDef){
        switch (instructionDef){
            case "0":
                return("Ready to start");

            case "1":
                return ("Let's go!");

            case "2":
                return("Knees caving in: Keep your knees in line with your toes");

            case "3":
                return("Your back is bending, keep it straight while tensing your abs");

            case "4":
                return ("Bring the weight down slowly and increase speed when lifting");

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
