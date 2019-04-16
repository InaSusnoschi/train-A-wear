package com.example.trainawearapplication;

import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class ClientListenBicep implements Runnable {
    /**
     *
     * @author Ioana Susnoschi
     * @brief Declaration of ClientListen thread
     * @version 1.0
     * @date 2019-04-12
     *
     */
    final String TAG = "ClLstBicep";

    private int port;
    private DatagramSocket udpSocket;
    private TextView displayField;

    UdpClientHandlerBicep handler;
    //    UdpClientHandlerPlank handlerPlank;
    public InetAddress serverAddr;
    /**
     * Class constructor
     * @param portNumber port to listen to
     * @param field View where the messages from the server are displayed in @link.MainActivity
     * @param handler This is the thread handler, employed to display messages from the server
     */

    public ClientListenBicep (int portNumber, TextView field, UdpClientHandlerBicep handler){
        port = portNumber;
        displayField = field;
        this.handler = handler;
        try {
            udpSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

//    public ClientListen (int portNumber, TextView field, UdpClientHandlerPlank handlerPlank){
//        port = portNumber;
//        displayField = field;
//        this.handlerPlank = handlerPlank;
//        try {
//            udpSocket = new DatagramSocket(port);
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//    }

    //    /**
//     * \brief Used to display whether the app is connected to the socket
//     * @param state takes values connected(), disconnected, not-oconfigured
//     */
//

    private void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler,
                        UdpClientHandlerBicep.UPDATE_STATE, state));
    }


    @Override
    public void run() {
        /**
         * This is a method that runs the udp client thread. It uses a variable run that sets the
         * current state of the thread (if it is running or stopped). While this variable is true,
         * a socket is created to communicate with the server on the binded port and read the
         * data from the server.
         */

        boolean run = true;
        while (run) {
            try {
                byte[] message = new byte[8000];
                DatagramPacket packet = new DatagramPacket(message,message.length);
                Log.d("UDP client: ", "about to wait to receive");
                udpSocket.receive(packet);
                String text = new String(message, 0, packet.getLength());
                Log.d("Received data", text);
                Log.d("Remote IP: ", packet.getAddress().toString());


//              pass received packet (text) to handler
//                handler.sendMessage(
//                        Message.obtain(handler, UdpClientHandler.UPDATE_MSG, text));

                // check for handshake with the server

                if ("train-A-wear online\n".equals(text)) {
                    Log.d(TAG, "handshake received");
                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandlerBicep.UPDATE_STATE, packet.getAddress().toString().substring(1)));

                    Log.d(TAG, "IPaddr:  " +  packet.getAddress().toString().substring(1));

                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandlerBicep.UPDATE_MSG, "0"));

                } else if ("train-a-wear ready".equals(text)) {
                    // check for own messages broadcasted to network
                    Log.d(TAG, "mirror on the wall");
                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandlerBicep.UPDATE_MSG, "3"));

                    Log.d(TAG, "no shaky hands");
                } else {
                    // obtain messages from RPI
                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandlerBicep.UPDATE_MSG, text));
                }

                // check for own messages broadcasted to network


            }catch (IOException e) {
                Log.e("UDP client: IOException", "error: ", e);
                run = false;
            }
        }
    }
}


