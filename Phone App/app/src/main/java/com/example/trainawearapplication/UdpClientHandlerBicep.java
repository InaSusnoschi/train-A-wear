package com.example.trainawearapplication;

import android.os.Message;
import android.util.Log;

import java.util.logging.Handler;

import static com.example.trainawearapplication.Biceps.showFeedback;
import static com.example.trainawearapplication.Biceps.textFeedback;
import static com.example.trainawearapplication.Biceps.textViewRx;
import static com.example.trainawearapplication.Biceps.textViewState;

/**
 * @file UdpClientHandlerPlank
 * @author Ioana Susnoschi
 * @brief Thread handler that enables passing the messages to the ClassicSquat activity to display feedback
 */

public class UdpClientHandlerBicep extends android.os.Handler{

    final String TAG = "UdpClientHandlerBicep";

    UdpClientHandlerBicep udpClientHandler;

    public static final int UPDATE_STATE = 0;
    public static final int UPDATE_MSG = 1;
    public static final int UPDATE_END = 2;
    private MainActivity parent;

    /**
     *
     */
    public UdpClientHandlerBicep() {
        super();
        this.parent = parent;
    }


    @Override
    public void handleMessage(Message msg) {
        udpClientHandler = new UdpClientHandlerBicep();
        switch (msg.what){
            case UPDATE_STATE:
                udpClientHandler.updateState((String)msg.obj);
                break;
            case UPDATE_MSG:
                udpClientHandler.updateRxMsg((String)msg.obj);
                textFeedback.setText(showFeedback((String)msg.obj));
                break;
            default:
                super.handleMessage(msg);
        }
    }

    /**
     * @brief Method that updates the receive field with the received String; used to read the String
     * @param rxmsg
     */
    private void updateRxMsg(String rxmsg) {
        textViewRx.setText(rxmsg + "\n");
        Log.d(TAG, "on text update");
    }

    // ip address here

    /**
     * @brief Method that displays the IP address in invisible field
     * @param state Variable displays the IP address when the handskahe occurs
     */
    private void updateState(String state) {
        Log.d(TAG,"update state");
        textViewState.setText(state);
    }

}

