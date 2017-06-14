package info.creepershift.daytime.server;

import java.io.IOException;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public class ServerMain {

    public static void main(String[] args) {


        try {
            ConnectionWatcher thread = new ConnectionWatcher();
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
