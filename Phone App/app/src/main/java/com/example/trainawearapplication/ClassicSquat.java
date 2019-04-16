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


public class ClassicSquat extends AppCompatActivity implements UpdateFeedback{
    /**
     * @brief Classic squat activity that uses the UpdateFeedback interface to show the real time feedback from the sensors
     *
     * @param mImages Array containing the drawable resources for the Carousel view showing bicep curls
     * @param mImagesTitle Holds the titles of the images
     * @thread udpListener listens to messages from the server continuously
     * @param PORT Port number. 31415 - sounds familiar, Pi?
     * @param udpSender object
     *
     */

    final String TAG = "ClassicSquat";
    public static int instruction = 1;
    /**
     * @brief Create Carousel view with three images
     */
    private int squatImages[] = new int[]{
            R.drawable.squatstart1, R.drawable.squatmed3, R.drawable.squatend1
    };

    private String[] squatImagesTitle = new String[]{
            "Squat1", "Squat2", "Squat3"
    };

    int num;

    public static int getInstruction() {
        return instruction;
    }

    /**
     * Implements the instruction obtained from the switch cases
     * @param instruction - variable used to decide on the message to be displayed
     */
    public static void setInstruction(int instruction) {
        ClassicSquat.instruction = instruction;
    }

    //    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonDisconnect;
    static TextView textViewState, textViewRx, textFeedback, messageView;

    UdpClientHandler udpClientHandler;

    private final int PORT = 31415;
    private Thread udpListener;
    private ClientSend udpSender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_squat);

        // create activity from parent
        Intent classicSquat = getIntent();

        // set carousel view
        CarouselView carouselView = findViewById(R.id.carousel);
        carouselView.setPageCount(squatImages.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(squatImages[position]);
            }
        });

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(ClassicSquat.this, squatImagesTitle[position], Toast.LENGTH_SHORT).show();
            }
        });

        // buttons and text fields for UDPclient thread and feedback

        buttonConnect =  findViewById(R.id.buttonStartSquatClassic); // in History
        buttonDisconnect =  findViewById(R.id.buttonStopSquatClassic); // in History
        textViewState = findViewById(R.id.state); // store IP address after handshake
        textViewRx = findViewById(R.id.received); // store numbers for feedback
        textFeedback = findViewById(R.id.feedback); // in Squat
        messageView = findViewById(R.id.message);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonDisconnect.setOnClickListener(buttonDisonnectOnClickListener);

        udpClientHandler = new com.example.trainawearapplication.UdpClientHandler();

        Log.d(TAG, "Start listening for messages");
        udpListener = new Thread(new com.example.trainawearapplication.ClientListen(PORT, textViewState, udpClientHandler));
        udpListener.start();

        udpSender = new ClientSend("255.255.255.255", PORT);
        Log.d(TAG, "sending to " + textViewState.getText().toString());
        udpSender.execute("train-a-wear ready");
    }

    // method to enter daughter activity - instructions
    public void enterSquatInstr(View view) {
        Intent squat_instr = new Intent(this, SquatInstr.class);
        startActivity(squat_instr);
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

                    udpSender = new ClientSend(textViewState.getText().toString(), PORT);
                    Log.d(TAG, textViewState.getText().toString());
                    Log.d(TAG, "sending to " + textViewState.getText().toString());
                    udpSender.execute("START");
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

                    udpSender = new ClientSend(textViewState.getText().toString(), PORT);
                    Log.d(TAG, "sending stop msg " + textViewState.getText().toString());
                    udpSender.execute("STOP");
                }
            };

    private void clientEnd(){
//        udpClientThread = null;
        textViewState.setText("clientEnd()");
        buttonConnect.setEnabled(true);
    }

    /**
     * @brief Switch case that decides on the feedback to be printed
     * @param instructionDef The message from RPI that encodes a defect/situation found by the algorithm
     *                       on the RPI
     * @return The feedback that will be printed using the thread handler, UdpClientHandler
     */

    public static String showFeedback (String instructionDef){
        switch (instructionDef){
            case "0":
                return("Ready to start");

            case "1":
                return ("Let's go!");

            case "3":
                return("Knees caving in: Keep your knees in line with your toes");

            case "2":
                return("Your back is bending, keep it straight while tensing your abs");

            case "4":
                return ("It looks like your form is asymmetrical when lowering in squat");

            default:
                return("You're doing great, keep going!");
        }
    }

    /**
     * Method required by the UpdateFeedback interface
     * @param view updates the specified textView
     * @param text String that corresponds to the feedback in showFeedback method
     */
    @Override
    public void setViewText(TextView view, String text) {
        view.setText(text);
    }


}
