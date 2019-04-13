package com.example.udpclient;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClientSend extends AsyncTask<String, Void, Void>{

    private String ip;
    private int port;

    public ClientSend(String destIP, int destPort){
        ip      = destIP;
        port    = destPort;
    }

    // Specific method that has to be implemented because the class is AsyncTask
    // It is called by invoking ClientSend.execute(vars);
    // The Void return is related to the interface type definition but I don't know how
    public Void doInBackground(String... dataToSend) {
        try {
            String transmissionData = dataToSend[0];
            DatagramSocket udpSocket = new DatagramSocket();
            InetAddress serverAddr = InetAddress.getByName(ip);
            byte[] buf = transmissionData.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,serverAddr, port);
            udpSocket.send(packet);
        } catch (SocketException e) {
            Log.e("Udp:", "Socket Error:", e);
        } catch (IOException e) {
            Log.e("Udp Send:", "IO Error:", e);
        }
        return null;
    }
}