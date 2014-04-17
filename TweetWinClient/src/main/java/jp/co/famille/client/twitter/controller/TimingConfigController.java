package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;


public class TimingConfigController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField fluctuation;

    @FXML
    private TextField margin;

    @FXML
    private TextField numberOfPeople;

   @FXML
    public void initialize(URL location, ResourceBundle resources) {
        assert fluctuation != null : "fx:id=\"fluctuation\" was not injected: check your FXML file 'TimingConfig.fxml'.";
        assert margin != null : "fx:id=\"margin\" was not injected: check your FXML file 'TimingConfig.fxml'.";
        assert numberOfPeople != null : "fx:id=\"numberOfPeople\" was not injected: check your FXML file 'TimingConfig.fxml'.";
    }
}