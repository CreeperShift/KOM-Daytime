package info.creepershift.daytime.client;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    public Button btnUDP;
    public Button btnTCP;
    public TextArea responseField;
    public Button btnEXIT;
    public Label mode;
    public TextField fieldIP;
    public TextField fieldPort;
    private boolean protocol = false;
    public Button btnSend;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        responseField.setEditable(false);

        BooleanBinding ipBinding = fieldIP.textProperty().isEmpty();
        BooleanBinding portBinding = fieldPort.textProperty().isEmpty();

        BooleanBinding combinedBinding = Bindings.or(ipBinding, portBinding);

        btnSend.disableProperty().bind(combinedBinding);


    }


    public void onUDP(ActionEvent actionEvent) {
        mode.setText("Mode: UDP");
        protocol = false;

    }

    public void onTCP(ActionEvent actionEvent) {
        mode.setText("Mode: TCP");
        protocol = true;
    }

    public void onQuit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void onSend(ActionEvent actionEvent) {

        try {
            Socket clientSocket = new Socket(fieldIP.getText(), Integer.parseInt(fieldPort.getText()));

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        outToServer.writeBytes("SYN" + '\n');

        if(inFromServer.readLine().equals("SYN,ACK")){
            outToServer.writeBytes("ACK"+ '\n');
        }

        responseField.setText(inFromServer.readLine());

        outToServer.writeBytes("ACK"+ '\n');

        if(inFromServer.readLine().equals("FIN")){
            outToServer.writeBytes("ACK"+ '\n' + "FIN" + '\n');
        }

        clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
