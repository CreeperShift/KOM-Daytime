package info.creepershift.daytime.server.connection;

import info.creepershift.daytime.common.Logger;
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
public class TCPConnection extends Thread implements Connection {


    private Socket socket;

    @Override
    public void connect(Socket socket) throws IOException {
        Thread thread = new Thread(this);
        this.socket = socket;
        thread.start();
    }

    @Override
    public void run() {
        Logger.info("TCPConnection Thread created.");
        try {
            DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            outToClient.writeBytes("SYN,ACK" + '\n');

            if (inFromClient.readLine().equalsIgnoreCase("ACK")) {
                outToClient.writeBytes(TimeHelper.getDate() + '\n');
            }
            if (inFromClient.readLine().equalsIgnoreCase("ACK")) {
                outToClient.writeBytes("FIN\n");
            }

            if (inFromClient.readLine().equalsIgnoreCase("ACK") && inFromClient.readLine().equalsIgnoreCase("FIN")) {
                outToClient.writeBytes("ACK");
            }
            socket.close();
            inFromClient.close();
            outToClient.close();
            Logger.info("Date and Time sent, closing connection.");

        } catch (IOException e)

        {
            Logger.error("Something went wrong.");
            e.printStackTrace();
        }
    }

}
