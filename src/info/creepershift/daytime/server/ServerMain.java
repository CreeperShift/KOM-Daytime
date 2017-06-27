package info.creepershift.daytime.server;

import info.creepershift.daytime.common.Logger;
import javafx.application.Application;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public class ServerMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextInputDialog dialog = new TextInputDialog("6789");
        dialog.setTitle("Server Configuration");
        dialog.setHeaderText("Server Configuration");
        dialog.setContentText("Enter port:");

        Optional<String> result = dialog.showAndWait();
        int port = 6789;
        if (result.isPresent()) {
            port = Integer.parseInt(result.get());
        }

        TextInputDialog bind = new TextInputDialog();
        bind.setTitle("Server Configuration");
        bind.setHeaderText("Server Configuration");
        bind.setContentText("Enter interface:");

        String address = "";

        Optional<String> resultBind = bind.showAndWait();
        if (resultBind.isPresent()) {
            address = resultBind.get();
        }

        try {
            if(address.isEmpty()) {
                new ConnectionTCP(port);
                new ConnectionUDP(port);
            }else{
                new ConnectionTCP(port, address);
                new ConnectionUDP(port, address);
            }
            Logger log = new Logger(false);
            log.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
