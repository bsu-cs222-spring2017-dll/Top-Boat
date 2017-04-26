package edu.bsu.css22.topboat.controllers;

import edu.bsu.css22.topboat.Game;
import edu.bsu.css22.topboat.UI;
import edu.bsu.css22.topboat.Util.SocketConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.util.Callback;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MultiSelectController implements Initializable{
    @FXML Button backButton;
    @FXML Button hostButton;
    @FXML Button joinButton;
    @FXML ListView playerListView;
    @FXML Text statusText;
    @FXML ProgressIndicator progressIndicator;
    private SocketConnection socket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backButton.setOnAction(new BackButtonListener());
        hostButton.setOnAction(new HostButtonListener());
        joinButton.setOnAction(new JoinButtonListener());
        connectToServer();
        initHostsView();
    }

    private void connectToServer() {
        socket = new SocketConnection();
        socket.connect(SocketConnection.DEFAULT_HOST, 5000);
        socket.onSocketConnected(() -> {
            System.out.println("connection established");
            Platform.runLater(() -> {
                statusText.setText("Fetching players...");
            });
        });
        socket.onDataReceived(data -> {
            JSONObject dataObject = new JSONObject(data);
            String responseType = dataObject.getString("responseType");
            JSONObject responseObject = dataObject.getJSONObject("response");
            parseResponseType(responseType, responseObject);
        });

        socket.addParam("reqType", "getHosts")
                .writeParams();
    }

    private void initHostsView() {
        playerListView.setCellFactory(param -> {
            ListCell<JSONObject> cell = new ListCell<JSONObject>() {
                @Override
                protected void updateItem(JSONObject host, boolean empty) {
                    super.updateItem(host, empty);
                    if(!empty || host != null) {
                        setText(host.getString("name") + "\t @" + host.getString("ip"));
                    }
                }
            };
            return cell;
        });
    }

    private void parseResponseType(String responseType, JSONObject response) {
        switch(responseType) {
            case "hostList":
                displayHosts(response);
                break;
            case "hostStatus":
                showHostingStatus(response);
                break;
            default:
                defaultResponse(response);
        }
    }

    private void displayHosts(JSONObject response) {
        JSONArray hostsArray = response.getJSONArray("hosts");

        Platform.runLater(() -> {
            playerListView.getItems().clear();
            for(Object hostObject : hostsArray) {
                JSONObject host = (JSONObject) hostObject;
                playerListView.getItems().add(host);
            }
            statusText.setText("waiting for your selection...");
            progressIndicator.setVisible(false);
        });
        playerListView.refresh();
    }

    private void showHostingStatus(JSONObject response) {

    }

    private void defaultResponse(JSONObject response) {
        System.out.println("default response");
    }

    private class BackButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            socket.disconnect();
            UI.changeView(UI.Views.MAIN_MENU);
        }
    }

    private class HostButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            socket.addParam("reqType", "host")
                    .addParam("name", Game.player1.getName())
                    .writeParams();
        }
    }

    private class JoinButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            //Check if playerListView has selected then attempt to join
        }
    }
}
