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
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    public TCPConnection(Socket sock) throws IOException {
        socket = sock;
        outToClient = new DataOutputStream(socket.getOutputStream());
        inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        Logger.info("TCPConnection Thread created.");
        try {
            sendPackets("SYN", "SYN,ACK");
            checkTimeout();

            sendPackets("ACK", TimeHelper.getDate());
            checkTimeout();

            sendPackets("ACK", "FIN");
            checkTimeout();

            if (inFromClient.readLine().equalsIgnoreCase("ACK") && inFromClient.readLine().equalsIgnoreCase("FIN")) {
                outToClient.writeBytes("ACK"+ '\n');
                outToClient.flush();
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

    private void checkTimeout() throws IOException, InterruptedException {
        long timeout = System.currentTimeMillis();
        while (!inFromClient.ready() && System.currentTimeMillis() < timeout + 8000) {
            Thread.sleep(50);
        }
        if (!inFromClient.ready()) {
            System.out.println("Failed at packet timeout check");
            throw new IOException();
        }
    }

    private void sendPackets(String in, String out) throws IOException {
        if (inFromClient.readLine().equalsIgnoreCase(in)) {
            outToClient.writeBytes(out + '\n');
        }
    }

}
