package com.example.trainawearapplication;

import android.os.Message;
import android.util.Log;

import java.util.logging.Handler;

import static com.example.trainawearapplication.Plank.showFeedback;
import static com.example.trainawearapplication.Plank.textFeedbackPlank;
import static com.example.trainawearapplication.Plank.textViewRxPlank;
import static com.example.trainawearapplication.Plank.textViewStatePlank;

/**
 * @file UdpClientHandlerPlank
 * @author Ioana Susnoschi
 * @brief Thread handler that enables passing the messages to the ClassicSquat activity to display feedback
 */

public class UdpClientHandlerPlank extends android.os.Handler{

    final String TAG = "UdpClientHandlerPlank";

    UdpClientHandlerPlank udpClientHandler;

    public static final int UPDATE_STATE = 0;
    public static final int UPDATE_MSG = 1;
    public static final int UPDATE_END = 2;
    private MainActivity parent;

    /**
     *
     */
    public UdpClientHandlerPlank() {
        super();
        this.parent = parent;
    }


    @Override
    public void handleMessage(Message msg) {
        udpClientHandler = new UdpClientHandlerPlank();
        switch (msg.what){
            case UPDATE_STATE:
                udpClientHandler.updateStatePlank((String)msg.obj);
                break;
            case UPDATE_MSG:
                udpClientHandler.updateRxMsgPlank((String)msg.obj);
                textFeedbackPlank.setText(showFeedback((String)msg.obj));
                break;
            default:
                super.handleMessage(msg);
        }
    }

    /**
     * @brief Method that updates the receive field with the received String; used to read the String
     * @param rxmsg
     */
    private void updateRxMsgPlank(String rxmsg) {
        textViewRxPlank.setText(rxmsg + "\n");
        Log.d(TAG, "on text update");
    }

    // ip address here

    /**
     * @brief Method that displays the IP address in invisible field
     * @param state Variable displays the IP address when the handskahe occurs
     */
    private void updateStatePlank(String state) {
        Log.d(TAG,"update state");
        textViewStatePlank.setText(state);
    }

}

