package info.creepershift.daytime.client;

import info.creepershift.daytime.common.Logger;
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
    public ChoiceBox<String> connectionBox;
    public Button btnSend;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Logger.info("Client starting up. Debug mode is " + (ClientMain.DEBUG ? "enabled." : "disabled."));
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

        /*
        We check if the port field contains anything but numbers.
         */
        try {
            Integer.parseInt(fieldPort.getText());
        } catch (Exception e) {
            displayError("Number Format Error", "Port can only contain numbers!");
            return;
        }

        if (connectionBox.getValue().equalsIgnoreCase("tcp")) {
            Logger.info("Starting TCP request.");
            sendTCP();
        } else {
            Logger.info("Starting UDP request.");
            sendUDP();
        }
    }


    /*
    Sends our request via TCP.
     */
    private void sendTCP() {

        //TODO: CLEANUP
        try {
            Socket clientSocket = new Socket(fieldIP.getText(), Integer.parseInt(fieldPort.getText()));
            Logger.info("Socket created successfully.");

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            outToServer.writeBytes("SYN" + '\n');
            if (inFromServer.readLine().equals("SYN,ACK")) {
                outToServer.writeBytes("ACK\n");
            }

            responseField.setText(inFromServer.readLine());

            outToServer.writeBytes("ACK" + '\n');

            if (inFromServer.readLine().equals("FIN")) {
                outToServer.writeBytes("ACK" + '\n' + "FIN" + '\n');
            }

            if (inFromServer.readLine().equalsIgnoreCase("ACK")) {
                clientSocket.close();
            }

            Logger.info("Received Date and Time, closing Socket.");

        } catch (IOException e) {
            displayError("Error", "Something went wrong.");
            e.printStackTrace();
        }

    }

    /*
    Sends our request via UDP.
     */
    private void sendUDP() {
        try {

            Socket clientSocket = new Socket(fieldIP.getText(), Integer.parseInt(fieldPort.getText()));
            Logger.info("Socket created successfully.");

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            /*
            We send an empty packet.
             */
            outToServer.writeByte('\n');

            responseField.setText(inFromServer.readLine());
            Logger.info("Received Date and Time, closing Socket.");

            clientSocket.close();
        } catch (ConnectException ce) {
            displayError("Connection Exception", "Could not connect to the server. Server might be offline or the connection was refused remotely.");
        } catch (IOException ioException) {
            displayError("Connection Reset", "The server reset the connection.");
            ioException.printStackTrace();
        }
    }


    /*
    Error handling.
    Pass in a header and the error description.
     */
    private void displayError(String header, String error) {
        Logger.error(header + ": " + error);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(error);
        alert.showAndWait();
    }

}
