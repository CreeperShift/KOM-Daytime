package info.creepershift.daytime.server;

import info.creepershift.daytime.server.connection.Connection;
import info.creepershift.daytime.server.connection.ConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public class ConnectionWatcher extends Thread {

    private final int port = 6789;
    ServerSocket serverSocket = new ServerSocket(port);
    ConnectionFactory connectionFactory = new ConnectionFactory();

    public ConnectionWatcher() throws IOException {

    }


    @Override
    public void run() {

        try {
            Socket connectionSocket = serverSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            String message = inFromClient.readLine();

            if (message.equals("SYN")) {

                Connection con = connectionFactory.getConnection("tcp");
                con.connect(connectionSocket);
            }
            if(message.isEmpty()){
                Connection con = connectionFactory.getConnection("udp");
                con.connect(connectionSocket);
            }




        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
