package info.creepershift.daytime.server.connection;

import info.creepershift.daytime.server.TimeHelper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public class UDPConnection implements Connection {

    @Override
    public void connect(Socket socket) throws IOException {
        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
        outToClient.writeBytes(TimeHelper.getCurrentTime());
        socket.close();
    }
}
