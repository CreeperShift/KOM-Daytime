package info.creepershift.daytime.server;

import info.creepershift.daytime.common.Logger;
import info.creepershift.daytime.server.connection.TCPConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                /*
                Read the first packet.
                 */
                String message = inFromClient.readLine();

                /*
                If the packet is both empty and NOT SYN, we are dealing with something unsupported. We abort.
                 */
                if (!message.equals("SYN")) {
                    Logger.error("Connection failed, not supported.");
                    continue;
                }

                /*
                If our packet contains SYN, we know it is TCP and launch the corresponding connection type.
                 */
                if (message.equals("SYN")) {
                    Logger.info("Connected with " + connectionSocket.getInetAddress() + " on port " + connectionSocket.getPort() + ". Using TCP.");
                    TCPConnection con = new TCPConnection();
                    con.connect(connectionSocket);
                }

            } catch (IOException e) {
                Logger.error("Socket timed out.");
                e.printStackTrace();
            }
        }
    }
}
