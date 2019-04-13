package com.example.udpclient;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ClientListen implements Runnable {

    private int port;
    private DatagramSocket udpSocket;
    private TextView displayField;

    public ClientListen (int portNumber, TextView field){
        port = portNumber;
        displayField = field;
        try {
            udpSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean run = true;
        while (run) {
            try {
                byte[] message = new byte[8000];
                DatagramPacket packet = new DatagramPacket(message,message.length);
                Log.i("UDP client: ", "about to wait to receive");
                udpSocket.receive(packet);
                String text = new String(message, 0, packet.getLength());
                Log.d("Received data", text);
                Log.d("Remote IP: ", packet.getAddress().toString());

                //Magic code that updates the display field
                displayField.setText(packet.getAddress().toString() + " : " + text);


            }catch (IOException e) {
                Log.e("UDP client: IOException", "error: ", e);
                run = false;
            }
        }
    }
}
