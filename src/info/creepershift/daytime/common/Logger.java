package info.creepershift.daytime.common;

import info.creepershift.daytime.server.ConnectionWatcher;
import info.creepershift.daytime.server.TimeHelper;
import javafx.application.Platform;

import java.util.Scanner;

/**
 * Daytime
 * Created by Max on 6/15/2017.
 */
public class Logger extends Thread {

    private Thread thread;
    private boolean client;

    public Logger(boolean client) {
        thread = this;
        this.client = client;
    }

    public static void info(String out) {
        System.out.println("[INFO]" + TimeHelper.getTime() + out);
    }

    public static void error(String out) {
        System.out.println("[ERROR]" + TimeHelper.getTime() + out);
    }


    @Override
    public void run() {

        while (thread != null) {

            Scanner scan = new Scanner(System.in);

            /*
            Separate the command arguments.
             */
            String[] split = scan.nextLine().split(" ");
            processCommand(split);
        }

    }


    private void processCommand(String[] command) {

        if (command.length > 0) switch (command[0]) {
            case "stop":
            case "STOP":
                if (!client) {
                    info("Stopping server.");
                    ConnectionWatcher.stopWatcher();
                    thread = null;
                }
                Platform.exit();
                break;
        }

    }

}
