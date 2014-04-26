package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class AutoFollowDetailOptionController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox followByFollowedCount;

    @FXML
    private CheckBox followFriendFollower;

    @FXML
    void initialize() {
        assert followByFollowedCount != null : "fx:id=\"followByFollowedCount\" was not injected: check your FXML file 'AutoFollowDetailOption.fxml'.";
        assert followFriendFollower != null : "fx:id=\"followFriendFollower\" was not injected: check your FXML file 'AutoFollowDetailOption.fxml'.";

    }

    void changeActivate(boolean flg) {
        followByFollowedCount.setDisable(flg);
        followFriendFollower.setDisable(flg);
    }
}
