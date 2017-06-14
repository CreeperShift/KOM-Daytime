package info.creepershift.daytime.server.connection;

import info.creepershift.daytime.common.Logger;
import info.creepershift.daytime.server.TimeHelper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public class UDPConnection extends Thread implements Connection {

    private Socket socket;

    @Override
    public void connect(Socket socket) throws IOException {
       Thread thread = new Thread(this);
        this.socket = socket;
        thread.start();
    }

    @Override
    public void run() {
        Logger.info("UDPConnection Thread created.");
        try {

            DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
            outToClient.writeBytes(TimeHelper.getDate());

            /*
            Cleanup
             */
            socket.close();
            outToClient.close();


            Logger.info("Date and Time sent, closing connection.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
