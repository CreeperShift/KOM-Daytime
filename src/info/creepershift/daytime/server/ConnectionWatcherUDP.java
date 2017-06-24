package info.creepershift.daytime.server;

import info.creepershift.daytime.common.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 * <p>
 * ConnectionWatcherTCP waits for an incoming connection.
 * Once the connection is established, it gets handed off into the corresponding Connection type.
 */

public class ConnectionWatcherUDP implements Runnable {

    private boolean running = true;
    private DatagramSocket serverSocket;
    private static ConnectionWatcherUDP INSTANCE;

    public ConnectionWatcherUDP(int port) throws IOException {
        Thread thread = new Thread(this);
        INSTANCE = this;
        serverSocket = new DatagramSocket(port);
        Logger.info("Initializing UDP on port " + port + ".");
        thread.start();
    }

    /*
    Stops our thread, closes the socket.
     */
    public static void stopWatcher() {
        INSTANCE.running = false;
        INSTANCE.serverSocket.close();
    }

    @Override
    public void run() {

        Logger.info("UDP Watchthread initiated successfully.");

        while (running) {
            try {
                byte[] buf = new byte[256];

                /*
                Ready to receive
                 */
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                serverSocket.receive(packet);
                Logger.info("Received UDP Packet.");
                String time = TimeHelper.getDate();
                buf = time.getBytes();

                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                packet = new DatagramPacket(buf, buf.length, address, port);
                serverSocket.send(packet);
                Logger.info("Date and Time packet sent");

            } catch (IOException e) {
                Logger.error("Timed out.");
                e.printStackTrace();
            }
        }

        serverSocket.close();

    }
}
