package com.example.first_app;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;


public class ClassicSquat extends AppCompatActivity {
    final String TAG = "ClassicSquat";
    /**
     * \brief Create Carousel view with three images
     */
    private int squatImages[] = new int[]{
            R.drawable.squatstart1, R.drawable.squatmed1, R.drawable.squatend1
    };

    private String[] squatImagesTitle = new String[]{
            "Squat1", "Squat2", "Squat3"
    };

    /**
     * @brief create UDP connection
     */
    public static int instruction = 1;
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

    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonDisconnect;
    static TextView textViewState, textViewRx, textViewNumber, textFeedback;

    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_squat);

        Intent classicSquat = getIntent();

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

        /**
         * from UDP connection: buttons and text fields
         */

//        editTextAddress =  findViewById(R.id.address); // in History
//        editTextPort =  findViewById(R.id.port); // in History
        buttonConnect =  findViewById(R.id.connect); // in History
        buttonDisconnect =  findViewById(R.id.disconnect); // in History
        textViewState = findViewById(R.id.state); // in History
        textViewRx = findViewById(R.id.received); // in History
        textFeedback = findViewById(R.id.feedback); // in Squat

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonDisconnect.setOnClickListener(buttonDisonnectOnClickListener);

        udpClientHandler = new UdpClientHandler(this);

    }

    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Log.d(TAG,"Connecting");
                    setInstruction(1);
                    udpClientThread = new com.example.first_app.UdpClientThread(
                           "192.168.1.148",31415,
                            udpClientHandler);
                    udpClientThread.start();


                    Log.d(TAG,"connect button state");
                    buttonConnect.setEnabled(false);
                    buttonConnect.setVisibility(View.GONE);
                    buttonDisconnect.setEnabled(true);
                    buttonDisconnect.setVisibility(View.VISIBLE);

                }
            };

    private void updateState(String state){
        Log.d(TAG,"update state");
        textViewState.setText(state);
    }

    private void updateRxMsg(String rxmsg) {
        textViewRx.setText(rxmsg + "\n");
        Log.d(TAG, "on text update");
    }


    /** disconnect on button press */
    View.OnClickListener buttonDisonnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Log.d(TAG,"Disconnecting");
                    setInstruction(0);
                    udpClientThread.stopClient();
                    textViewState.setText("clientEnd()");
                    Log.d(TAG,"disconnect button state");
                    buttonConnect.setEnabled(true);
                    buttonConnect.setVisibility(View.VISIBLE);
                    buttonDisconnect.setEnabled(false);
                    buttonDisconnect.setVisibility(View.GONE);
                }
            };



    public static class UdpClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private ClassicSquat parent;

        public UdpClientHandler(ClassicSquat parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_STATE:
                    parent.updateState((String)msg.obj);
                    break;
                case UPDATE_MSG:
                    parent.updateRxMsg((String)msg.obj);
                    int A = Integer.parseInt((String)msg.obj);
                    showFeedback(A);
                    break;
                default:
                    super.handleMessage(msg);
            }

        }

    }


    public static void showFeedback (int instructionDef){
        switch (instructionDef){
            case 0:
                textFeedback.setText("Let's go!");
                break;
            case 1:
                textFeedback.setText("Knees caving in: Keep your knees in line with your toes");
                break;
            case 2:
                textFeedback.setText("Your back is bending, keep it straight while tensing your abs");
                break;
            default:
                textFeedback.setText("You're doing great, keep going!");break;
        }
    }

    /**
     * \brief enter Instructions&Setup view on click
     * @param view
     */
//
//    public void enterInstructions(View view) {
//        Intent squatInstr = new Intent(this, SquatInstr.class);
//        startActivity(squatInstr);
//    }
}
