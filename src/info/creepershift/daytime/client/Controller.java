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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextArea responseField;
    public Button btnEXIT;
    public TextField fieldIP;
    public TextField fieldPort;
    public ChoiceBox<String> connectionBox;
    public Button btnSend;
    public TextField fieldRetrys;
    private int retries;

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
        fieldRetrys.setText("3");

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
            retries = Integer.parseInt(fieldRetrys.getText());
        } catch (Exception e) {
            displayError("Number Format Error", "Port and Retries can only contain numbers!");
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
        Socket clientSocket = null;
        try {

            clientSocket = new Socket(fieldIP.getText(), Integer.parseInt(fieldPort.getText()));
            clientSocket.setSoTimeout(5000);
        } catch (IOException e) {
            displayError("Error", "Could not establish connection with the server.");
            e.printStackTrace();
            if (retries > 0) {
                retries--;
                sendTCP();
            }
        }

        try {
            Logger.info("Socket created successfully with " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort() + ".");
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            responseField.setText(inFromServer.readLine());
            clientSocket.close();
            inFromServer.close();
            Logger.info("Received Date and Time, closing Socket.");
        } catch (IOException e) {
            displayError("Error", "Error during data receive.");
            e.printStackTrace();
            if (retries > 0) {
                retries--;
                sendTCP();
            }
        }

    }

    private void sendUDP() {
        DatagramSocket clientSocket = null;
        DatagramPacket packet = null;
        byte[] buf = new byte[256];

        try {
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(5000);
            InetAddress address = InetAddress.getByName(fieldIP.getText());
            packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(fieldPort.getText()));
            clientSocket.send(packet);
            Logger.info("Packet sent to " + packet.getAddress() + " on port " + packet.getPort() + ".");
        } catch (IOException e) {
            displayError("Connection Error", "Error happened trying to send packet. Possible timeout?");
            e.printStackTrace();
            if (retries > 0) {
                retries--;
                sendUDP();
            }
        }

        try {
            packet = new DatagramPacket(buf, buf.length);
            clientSocket.receive(packet);
            Logger.info("Received packet.");
            String received = new String(packet.getData(), 0, packet.getLength());
            responseField.setText(received);
            Logger.info("Received Date and Time, closing Socket.");
            clientSocket.close();
        } catch (IOException e) {
            displayError("Connection Error", "Error happened trying to receive packet.");
            e.printStackTrace();
            if (retries > 0) {
                retries--;
                sendUDP();
            }
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
