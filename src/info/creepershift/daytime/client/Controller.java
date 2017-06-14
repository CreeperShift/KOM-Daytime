package info.creepershift.daytime.client;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
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
        btnSend.disableProperty().bind(Bindings.or(fieldIP.textProperty().isEmpty(), fieldPort.textProperty().isEmpty()));


        /*
        Too lazy to type this every time.....zZzzZzzzz
         */
        if (ClientMain.DEBUG) {
            fieldIP.setText("localhost");
            fieldPort.setText("6789");
        }


        /*
        Setup our checkbox, defaults to UDP.
         */
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

        try {
            Integer.parseInt(fieldPort.getText());
        } catch (Exception e) {
            displayError("Number Format Error", "Port can only contain numbers!");
            return;
        }

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
        } catch (ConnectException ce) {
            displayError("Connection Exception", "Could not connect to the server. Server might be offline or the connection was refused remotely.");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    private void displayError(String header, String error) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(error);
        alert.showAndWait();
    }

}
