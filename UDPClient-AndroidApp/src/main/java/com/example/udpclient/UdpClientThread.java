package com.example.udpclient;

import android.os.Message;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;


public class UdpClientThread extends Thread {

    final String TAG = "UpdCl";

//    public int instruction = 0;


    int dstPort;
//    String dstAddress;
    private final AtomicBoolean running = new AtomicBoolean(false);
    MainActivity.UdpClientHandler handler;

    String dstAddress;
    DatagramSocket socket;
    MulticastSocket socketM;
    InetAddress address;
    InetAddress serverAddr;
    InetAddress group;


    /**
     * CONSTRUCTOR
     * @param addr
     * @param port
     * @param handler
     */
    public UdpClientThread(String addr, int port, MainActivity.UdpClientHandler handler) throws IOException {
        super();
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;

    }

    public class MulticastPublisher {
        final String TAG = ("MulticastSendRec");

                private DatagramSocket socket;

    private byte[] buf;
//    protected MulticastSocket socketMult = null;
//        protected byte[] buf = new byte[256];

        public InetAddress MulticastPublisherFct() throws IOException {
            socketM = new MulticastSocket(31415);
            InetAddress group = InetAddress.getByName("230.0.0.0");
            Log.d(TAG, "join group");
            socketM.joinGroup(group); // is this before or after sending multicast?

            // get message to multicast so PI sees it
//            buf = multicastMessage.getBytes();
            byte[] buf = ("heya \n").getBytes();
            // multicast
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, group, 31415);
            Log.d(TAG, "send pack to any network");
            socketM.send(packet);

            // wait
            // dunno command for wait

            DatagramPacket packetRec
                    = new DatagramPacket(buf, buf.length, group, 31415);
            socketM.receive(packetRec);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            if ("train-A-wear online\n".equals(received)) {
                serverAddr = socketM.getInetAddress();
//                break;
                return serverAddr;
            }
//            socket.leaveGroup(group);
            socketM.close();
            return null;
        }
    }

    /**
     * \brief This is a setter. Sets running value and can be used alternatively
     * (set true or false) for stopping the thread
     *
     * @param newValue
     */
    public void setRunning(boolean newValue) {
        running.set(newValue);
    }

    private void sendState(String state) {
        handler.sendMessage(
                Message.obtain(handler,
                        MainActivity.UdpClientHandler.UPDATE_STATE, state));
    }

    public void stopClient() {
        running.set(false);
    }

    public String sendReceive() {

        try {

            socket = new DatagramSocket();
            address = InetAddress.getByName(dstAddress); // get from constructor?
//            address = MainActivity.MulticastPublisher;
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

            return line;
            /** get message and store in int */
//            Log.d(TAG, "parse int?");
//            int A = Integer.parseInt(line);


            // this.sleep(3000);


        } catch (SocketException e) {
            Log.e(TAG, "socket excetopm h0 happed", e);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
                handler.sendEmptyMessage(MainActivity.UdpClientHandler.UPDATE_END);
            }
        }
        return "Error";
    }


    @Override
    public void run() {


        Log.d(TAG, "connecting...");
        sendState("connecting...");

        running.set(true);
        while (running.get()) {
            Log.d(TAG, "receive running state");
            String response = sendReceive();
            Log.d(TAG, "sending feedback to main thread");

            handler.sendMessage(
                    Message.obtain(handler, MainActivity.UdpClientHandler.UPDATE_MSG, response));

        }
    }

}
