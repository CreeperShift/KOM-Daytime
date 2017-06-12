package info.creepershift.daytime.server.connection;

import info.creepershift.daytime.server.TimeHelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public class TCPConnection implements Connection {


    @Override
    public void connect(Socket socket) {

        System.out.println("Connection open");

        try {
            DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

            outToClient.writeBytes("SYN,ACK");
            System.out.println("SENDING SYN & ACK");

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if(inFromClient.readLine().equals("ACK")){
                outToClient.writeBytes(TimeHelper.getCurrentTime());
            }

            if(inFromClient.readLine().equals("ACK")){
                outToClient.writeBytes("FIN");
            }

            if(inFromClient.readLine().equals("ACK") && inFromClient.readLine().equals("FIN")){
                outToClient.writeBytes("ACK");

            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
