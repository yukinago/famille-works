package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;

public class AutoFollowTargetController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox followByProfile;

    @FXML
    private CheckBox followByUserName;

    @FXML
    private AnchorPane followPeriodAutoFollow;

    @FXML
    private CheckBox followTweet;

    @FXML
    void initialize() {
        assert followByProfile != null : "fx:id=\"followByProfile\" was not injected: check your FXML file 'AutoFollowTarget.fxml'.";
        assert followByUserName != null : "fx:id=\"followByUserName\" was not injected: check your FXML file 'AutoFollowTarget.fxml'.";
        assert followPeriodAutoFollow != null : "fx:id=\"followPeriodAutoFollow\" was not injected: check your FXML file 'AutoFollowTarget.fxml'.";
        assert followTweet != null : "fx:id=\"followTweet\" was not injected: check your FXML file 'AutoFollowTarget.fxml'.";

    }

    void changeActivate(boolean flg) {
        followByProfile.setDisable(flg);
        followByUserName.setDisable(flg);
        followTweet.setDisable(flg);
    }
}
