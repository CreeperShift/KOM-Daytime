package info.creepershift.daytime.server;

import info.creepershift.daytime.common.Logger;
import info.creepershift.daytime.server.connection.TCPConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 * <p>
 * ConnectionWatcherTCP waits for an incoming connection.
 * Once the connection is established, it gets handed off into the corresponding Connection type.
 */

public class ConnectionWatcherTCP implements Runnable {

    private boolean running = true;
    private ServerSocket serverSocket;
    private static ConnectionWatcherTCP INSTANCE;

    public ConnectionWatcherTCP(int port) throws IOException {
        Thread thread = new Thread(this);
        INSTANCE = this;
        serverSocket = new ServerSocket(port);
        Logger.info("Initializing TCP on port " + port + ".");
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
                connectionSocket.setSoTimeout(5000);

                new Thread(new TCPConnection(connectionSocket)).start();

            } catch (IOException e) {
                Logger.error("Socket timed out.");
                e.printStackTrace();
            }
        }
    }
}
