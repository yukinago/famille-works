package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Setter;

public class AutoFunctionConfigController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @Setter
    private Stage primaryStage;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

    }

}
