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
public class TCPConnection implements Runnable {

    private Socket socket;
    private long timeout = 0;
    BufferedReader inFromClient;
    DataOutputStream outToClient;

    public TCPConnection(Socket sock) throws IOException {
        socket = sock;
        outToClient = new DataOutputStream(socket.getOutputStream());
        inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        Logger.info("TCPConnection Thread created.");
        try {


            outToClient.writeBytes("SYN,ACK" + '\n');

            checkTimeout();

            if (inFromClient.readLine().equalsIgnoreCase("ACK")) {
                outToClient.writeBytes(TimeHelper.getDate() + '\n');
                checkTimeout();
            }

            if (inFromClient.readLine().equalsIgnoreCase("ACK")) {
                outToClient.writeBytes("FIN\n");
            }
            checkTimeout();
            if (inFromClient.readLine().equalsIgnoreCase("ACK") && inFromClient.readLine().equalsIgnoreCase("FIN")) {
                outToClient.writeBytes("ACK");
            }
            socket.close();
            inFromClient.close();
            outToClient.close();
            Logger.info("Date and Time sent, closing connection.");

        } catch (IOException e) {
            Logger.error("Connection failed.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Logger.error("Thread failed to sleep.");
            e.printStackTrace();
        }
    }

    private boolean checkTimeout() throws IOException, InterruptedException {
        timeout = System.currentTimeMillis();
        while (!inFromClient.ready() && System.currentTimeMillis() < timeout + 8000) {
            Thread.sleep(50);
        }
        if (!inFromClient.ready()) {
            System.out.println("Failed at packet timeout check");
            throw new IOException();
        }
        return true;
    }

}
