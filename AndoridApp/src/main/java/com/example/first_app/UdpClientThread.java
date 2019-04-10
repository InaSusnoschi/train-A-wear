package com.example.first_app;

import android.os.Message;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class UdpClientThread extends Thread{

    final String TAG = "UpdCl";

//    public int instruction = 0;

    String dstAddress;
    int dstPort;
    private final AtomicBoolean running = new AtomicBoolean(false);
    ClassicSquat.UdpClientHandler handler;

    DatagramSocket socket;
    InetAddress address;

    /**
     * \brief Constructor that takes port and address from History activity
     * @param addr
     * @param port
     * @param udpClientHandler
     */

    public UdpClientThread(String addr, int port, History.UdpClientHandler udpClientHandler) {
        super();
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;
    }

    /**
     * \brief This is a setter. Sets running value and can be used alternatively
     * (set true or false) for stopping the thread
     * @param newValue
     */
    public void setRunning(boolean newValue){
        running.set(newValue);
    }

    /**
     * \brief Used to display whether the app is connected to the socket
     * @param state takes values connected(), disconnected, not-oconfigured
     */

    private void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler,
                        ClassicSquat.UdpClientHandler.UPDATE_STATE, state));
    }

    /**
     * To stop the thread on button press
     */
    public void stopClient(){
        running.set(false);
    }

    @Override
    public void run() {

        Log.d(TAG, "connecting...");
        sendState("connecting...");

        running.set(true);
        while (running.get()) {
            Log.d(TAG, "receive running state");
            try {


                socket = new DatagramSocket();
                address = InetAddress.getByName(dstAddress);
                Log.d(TAG, "send request...");
                // send request
                byte[] buf = ("heya \n \r").getBytes();

                DatagramPacket packet =
                        new DatagramPacket(buf, buf.length, address, dstPort);
                byte[] bufR = new byte[256];
                DatagramPacket packetR =
                        new DatagramPacket(bufR, bufR.length, address, dstPort);
//
                sendState("connected");

                Log.d(TAG, "send packet");
                socket.send(packet);
                Log.d(TAG, "receive packet");
                socket.receive(packetR);
//            socket.send(pack);


                Log.d(TAG, "packet received");
                String line = new String(packetR.getData(), 0, packetR.getLength());
                Log.d(TAG, "message received is: " + line);
                //System.out.println("line: " + line);
                //     String[] lines = line.split( "\n" );

                /** get message and store in int */
                Log.d(TAG, "parse int?");
                int A = Integer.parseInt(line);

                Log.d(TAG, "sending feedback to main thread");

                handler.sendMessage(
                        Message.obtain(handler, ClassicSquat.UdpClientHandler.UPDATE_MSG, line));
                this.sleep(3000);


            } catch (SocketException e) {
                Log.e(TAG, "socket excetopm h0 happed", e);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    socket.close();
                    handler.sendEmptyMessage(ClassicSquat.UdpClientHandler.UPDATE_END);
                }
            }

        }
    }

}
