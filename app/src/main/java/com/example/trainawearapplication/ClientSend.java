package com.example.trainawearapplication;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * The ClientSend class declares an asynchronous task used to
 * send messages from Workout activities
 *
 * @author  Ioana Susnoschi
 * @version 1.0
 * @since   2019-04-05
 */
public class ClientSend extends AsyncTask<String, Void, Void>{

    private String ip;
    private int port;

    /**
     * Class constructor for ClientSend
     * @param destIP IP address of the server/destination
     * @param destPort Destination port
     */

    public ClientSend(String destIP, int destPort){
        ip      = destIP;
        port    = destPort;
    }

    /**
     * @brief Method that has to be implemented because the class is AsyncTask
     * Called by invoking ClientSend.execute(vars);
     * @param dataToSend The data introduced by the user.
     * @return Is related to the Interface type definition
     */
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