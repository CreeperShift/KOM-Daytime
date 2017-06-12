package info.creepershift.daytime.server.connection;

import java.net.Socket;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public interface Connection {

    void connect(Socket socket);

}
