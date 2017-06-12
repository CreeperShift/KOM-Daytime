package info.creepershift.daytime.server.connection;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public class ConnectionFactory {

    public Connection getConnection(String protocol){
        if(protocol == null){
            return null;
        }
        if(protocol.equalsIgnoreCase("tcp")){
            return new TCPConnection();
        }
        if(protocol.equalsIgnoreCase("udp")){
            return new UDPConnection();
        }

        return null;
    }

}
