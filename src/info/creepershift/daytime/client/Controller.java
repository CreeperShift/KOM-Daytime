package info.creepershift.daytime.client;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextArea responseField;
    public Button btnEXIT;
    public TextField fieldIP;
    public TextField fieldPort;
    public ChoiceBox connectionBox;
    public Button btnSend;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        responseField.setEditable(false);

        BooleanBinding ipBinding = fieldIP.textProperty().isEmpty();
        BooleanBinding portBinding = fieldPort.textProperty().isEmpty();

        BooleanBinding combinedBinding = Bindings.or(ipBinding, portBinding);

        btnSend.disableProperty().bind(combinedBinding);
        ArrayList<String> cnts = new ArrayList<>();
        cnts.add("UDP");
        cnts.add("TCP");
        ObservableList<String> obsList = FXCollections.observableList(cnts);
        connectionBox.setItems(obsList);
        connectionBox.setValue("UDP");

    }

    public void onQuit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void onSend(ActionEvent actionEvent) {


        if (connectionBox.getValue().toString().equalsIgnoreCase("tcp")) {
            sendTCP();
        } else {
            sendUDP();
        }
    }


    private void sendTCP() {

        try {
            Socket clientSocket = new Socket(fieldIP.getText(), Integer.parseInt(fieldPort.getText()));

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            outToServer.writeBytes("SYN" + '\n');

            if (inFromServer.readLine().equals("SYN,ACK")) {
                outToServer.writeBytes("ACK" + '\n');
            }

            responseField.setText(inFromServer.readLine());

            outToServer.writeBytes("ACK" + '\n');

            if (inFromServer.readLine().equals("FIN")) {
                outToServer.writeBytes("ACK" + '\n' + "FIN" + '\n');
            }

            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendUDP() {
        try {
            Socket clientSocket = new Socket(fieldIP.getText(), Integer.parseInt(fieldPort.getText()));

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            outToServer.writeByte('\n');

            responseField.setText(inFromServer.readLine());

            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
