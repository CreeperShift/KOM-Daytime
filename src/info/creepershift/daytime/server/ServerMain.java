package info.creepershift.daytime.server;

import java.io.IOException;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public class ServerMain {

    public static void main(String[] args) {


        try {
            Thread thread = new Thread(new ConnectionWatcher());
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
