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


public class ClientListen implements Runnable {
    /**
     * @file TrainAwearapplication, ClientListen
     * @author Ioana Susnoschi
     * @brief Declaration of ClientListen thread
     * @version 1.0
     * @date 2019-04-12
     *
     * @copyright Copyright (c) 2019
     *
     */
    final String TAG = "ClientListen";

    private int port;
    private DatagramSocket udpSocket;
    private TextView displayField;

    UdpClientHandler handler;
    UdpClientHandlerPlank handlerPlank;
    public InetAddress serverAddr;
    /**
     * Class constructor
     * @param portNumber port to listen to
     * @param field View where the messages from the server are displayed in @link.MainActivity
     * @param handler This is the thread handler, employed to display messages from the server
     */

    public ClientListen (int portNumber, TextView field, UdpClientHandler handler){
        port = portNumber;
        displayField = field;
        this.handler = handler;
        try {
            udpSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public ClientListen (int portNumber, TextView field, UdpClientHandlerPlank handlerPlank){
        port = portNumber;
        displayField = field;
        this.handlerPlank = handlerPlank;
        try {
            udpSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    //    /**
//     * \brief Used to display whether the app is connected to the socket
//     * @param state takes values connected(), disconnected, not-oconfigured
//     */
//

    private void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler,
                        UdpClientHandler.UPDATE_STATE, state));
    }


    @Override
    public void run() {

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
                            Message.obtain(handler, UdpClientHandlerPlank.UPDATE_STATE, packet.getAddress().toString().substring(1)));
                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandler.UPDATE_STATE, packet.getAddress().toString().substring(1)));


                    Log.d(TAG, "IPaddr:  " +  packet.getAddress().toString().substring(1));
                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandler.UPDATE_MSG, "0"));
                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandlerPlank.UPDATE_MSG, "0"));
                } else if ("train-a-wear ready".equals(text)) {
                    // check for own messages broadcasted to network
                    Log.d(TAG, "mirror on the wall");
                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandler.UPDATE_MSG, "3"));
                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandlerPlank.UPDATE_MSG, "3"));
                    Log.d(TAG, "no shaky hands");
                } else {
                    // obtain messages from RPI
                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandler.UPDATE_MSG, text));
                    handler.sendMessage(
                            Message.obtain(handler, UdpClientHandlerPlank.UPDATE_MSG, text));
                }

                // check for own messages broadcasted to network


            }catch (IOException e) {
                Log.e("UDP client: IOException", "error: ", e);
                run = false;
            }
        }
    }
}

