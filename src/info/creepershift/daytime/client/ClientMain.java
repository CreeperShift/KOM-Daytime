package info.creepershift.daytime.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Daytime
 * Created by Max on 6/7/2017.
 */
public class ClientMain extends Application{

    public static final boolean DEBUG = true;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Daytime Client");
        primaryStage.setScene(new Scene(root, 469, 211));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) throws Exception{
        launch(args);
    }

}
