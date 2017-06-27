package info.creepershift.daytime.server;

import info.creepershift.daytime.common.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 * <p>
 * ConnectionTCP waits for an incoming connection.
 * Once the connection is established, it gets handed off into the corresponding Connection type.
 */

public class ConnectionTCP implements Runnable {

    private boolean running = true;
    private ServerSocket serverSocket;
    private static ConnectionTCP INSTANCE;

    public ConnectionTCP(int port) throws IOException {
        Thread thread = new Thread(this);
        INSTANCE = this;
        serverSocket = new ServerSocket(port);
        Logger.info("Initializing TCP on port " + port + ".");
        thread.start();
    }

    public ConnectionTCP(int port, String address) throws IOException {
        Thread thread = new Thread(this);
        INSTANCE = this;
        serverSocket = new ServerSocket(port, 0, InetAddress.getByName(address));
        Logger.info("Initializing TCP on port " + port + " bound to interface " + address + ".");
        thread.start();
    }

    /*
    Stops our thread, closes the socket.
     */
    public static void stopWatcher() {
        INSTANCE.running = false;
        try {
            INSTANCE.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        Logger.info("TCP Watchthread initiated successful.");

        while (running) {
            try {
                /*
                Hurray, we got a connection!
                 */
                Socket connectionSocket = serverSocket.accept();
                Logger.info("TCP Connection from " + connectionSocket.getInetAddress() + " on port " + connectionSocket.getPort() + ".");
                connectionSocket.setSoTimeout(5000);
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.writeBytes(TimeHelper.getDate() + '\n');
                connectionSocket.close();
                outToClient.close();
                Logger.info("Date and Time sent, closing connection.");

            } catch (IOException e) {
                Logger.error("Socket timed out.");
                e.printStackTrace();
            }
        }
    }
}
