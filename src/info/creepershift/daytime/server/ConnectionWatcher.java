package info.creepershift.daytime.server;

import info.creepershift.daytime.server.connection.Connection;
import info.creepershift.daytime.server.connection.ConnectionFactory;
import info.creepershift.daytime.common.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 * <p>
 * ConnectionWatcher waits for an incoming connection.
 * Once the connection is established, it gets handed off into the corresponding Connection type.
 */

public class ConnectionWatcher {

    private boolean running = true;
    private ServerSocket serverSocket;
    private static ConnectionWatcher INSTANCE;

    /*
    Returns a Connection Object independent of implementation.
     */
    private ConnectionFactory connectionFactory = new ConnectionFactory();


    public ConnectionWatcher(int port) throws IOException {
        INSTANCE = this;
        serverSocket = new ServerSocket(port);
        Logger.info("Initializing server on port " + port + ".");
        startServer();
    }



    private void startServer() {
        Logger.info("Server start successful.");

        while (running) {
            try {
                /*
                Hurray, we got a connection!
                 */
                Socket connectionSocket = serverSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                /*
                Read the first packet.
                 */
                String message = inFromClient.readLine();

                /*
                If the packet is both empty and NOT SYN, we are dealing with something unsupported. We abort.
                 */
                if (!message.equals("SYN") && !message.isEmpty()) {
                    Logger.error("Connection failed, packet type not supported.");
                    continue;
                }

                /*
                If our packet contains SYN, we know it is TCP and launch the corresponding connection type.
                 */
                if (message.equals("SYN")) {
                    Logger.info("Connected with " + connectionSocket.getInetAddress() + " on port " + connectionSocket.getPort() + ". Using TCP.");
                    Connection con = connectionFactory.getConnection("tcp");
                    con.connect(connectionSocket);
                }

                /*
                If our packet is empty, we know it is UDP and launch the corresponding connection type.
                 */
                if (message.isEmpty()) {
                    Logger.info("Connected with " + connectionSocket.getInetAddress() + " on port " + connectionSocket.getPort() + ". Using UDP.");
                    Connection con = connectionFactory.getConnection("udp");
                    con.connect(connectionSocket);
                }

            } catch (IOException e) {
                Logger.error("Socket timed out.");
                e.printStackTrace();
            }
        }
    }

    /*
    Stops our thread, closes the socket.
     */
    public static void stopWatcher(){
        INSTANCE.running = false;
        try {
            INSTANCE.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
