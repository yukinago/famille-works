package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AutoFollowController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ChoiceBox<?> activateFlgAutoFollow;

    @FXML
    private CheckBox followByFollowedCount;

    @FXML
    private CheckBox followByProfile;

    @FXML
    private CheckBox followByUserName;

    @FXML
    private CheckBox followFriendFollower;

    @FXML
    private AnchorPane followPeriodAutoFollow;

    @FXML
    private CheckBox followTweet;

    @FXML
    private TextField keywordAutoFollow;

    @FXML
    private AnchorPane timingConfigAutoFollow;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert activateFlgAutoFollow != null : "fx:id=\"activateFlgAutoFollow\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert followByFollowedCount != null : "fx:id=\"followByFollowedCount\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert followByProfile != null : "fx:id=\"followByProfile\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert followByUserName != null : "fx:id=\"followByUserName\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert followFriendFollower != null : "fx:id=\"followFriendFollower\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert followPeriodAutoFollow != null : "fx:id=\"followPeriodAutoFollow\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert followTweet != null : "fx:id=\"followTweet\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert keywordAutoFollow != null : "fx:id=\"keywordAutoFollow\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert timingConfigAutoFollow != null : "fx:id=\"timingConfigAutoFollow\" was not injected: check your FXML file 'AutoFollow.fxml'.";

        // 有効・無効の切り替え
        activateFlgAutoFollow.getSelectionModel().selectedIndexProperty().addListener(
            (ObservableValue<? extends Number> observableValue, Number number, Number number2) -> {
                changeActivate();
            });
        changeActivate();
    }

    private void changeActivate() {
        boolean flg = !"有効".equals(activateFlgAutoFollow.getValue());
        followByFollowedCount.setDisable(flg);
        followByProfile.setDisable(flg);
        followByUserName.setDisable(flg);
        followFriendFollower.setDisable(flg);
        followTweet.setDisable(flg);
        keywordAutoFollow.setDisable(flg);
    }
}
