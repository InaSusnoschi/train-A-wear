package com.example.udpclient;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    public static int instruction = 1;
    int num;

    public static int getInstruction() {
        return instruction;
    }

    public static void setInstruction(int instruction) {
        MainActivity.instruction = instruction;
    }

    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonDisconnect;
    static TextView textViewState, textViewRx, textViewNumber, textFeedback;

    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress =  findViewById(R.id.address);
        editTextPort =  findViewById(R.id.port);
        buttonConnect =  findViewById(R.id.connect);
        buttonDisconnect =  findViewById(R.id.disconnect);
        textViewState = findViewById(R.id.state);
        textViewRx = findViewById(R.id.received); // number received
        textFeedback = findViewById(R.id.feedback);

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
                    udpClientThread = new UdpClientThread(
                            editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()),
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

//    private void clientEnd(){
//        udpClientThread = null;
//        textViewState.setText("clientEnd()");
//        buttonConnect.setEnabled(true);
//
//    }

    public static class UdpClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private MainActivity parent;

        public UdpClientHandler(MainActivity parent) {
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




}

