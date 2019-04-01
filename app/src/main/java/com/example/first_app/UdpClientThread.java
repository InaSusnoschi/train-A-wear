package com.example.first_app;

import android.os.Message;
import android.util.Log;

import com.example.first_app.History;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpClientThread extends Thread{

    private final String TAG = "UpdCl";

    private String dstAddress;
    private int dstPort;
    private boolean running;
    private History.UdpClientHandler handler;

    private DatagramSocket socket;
    private InetAddress address;

    public UdpClientThread(String addr, int port, History.UdpClientHandler handler) {
        super();
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    private void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler,
                        History.UdpClientHandler.UPDATE_STATE, state));
    }

    @Override
    public void run() {
        Log.d(TAG, "connecting...");
        sendState("connecting...");

        running = true;

        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(dstAddress);
            Log.d(TAG, "send request...");
            // send request
            byte[] buf = ("Heya! \n \r").getBytes();
            byte[] buf_r = new byte[2048];  // received message stored here
//            byte[] bufmsg = ("heya").getBytes();

            DatagramPacket packet =
                    new DatagramPacket( buf, buf.length, address, dstPort);

//            DatagramPacket pack =
//                    new DatagramPacket( bufmsg, bufmsg.length, address, dstPort);
            Log.d(TAG, "send packet");
            socket.send(packet);
//            socket.send(pack);

            sendState("connected");

            // get response message (ACK)
//            packet = new DatagramPacket(buf, buf.length);
            DatagramPacket packetrec = new DatagramPacket(buf_r, buf_r.length);

            Log.d(TAG, "receive packet");
            socket.receive(packetrec);
  //          socket.receive(packet);

            Log.d(TAG, "get packet data");
            String line = new String(packetrec.getData(), 0, packetrec.getLength());
  //          String number = new String(packetrec.getData(), 0, packetrec.getLength());

            Log.d(TAG, "handle packet");
            handler.sendMessage(
                    Message.obtain(handler, History.UdpClientHandler.UPDATE_MSG, line));

//            handler.sendMessage(
//                    Message.obtain(handler, History.UdpClientHandler.UPDATE_MSG, number));

        } catch (SocketException e) {
            Log.e(TAG,"socket excetopm h0 happed",e);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                socket.close();

                handler.sendEmptyMessage(History.UdpClientHandler.UPDATE_END);
            }
        }

    }
}
