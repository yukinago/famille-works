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
    private ChoiceBox<String> activateFlgAutoFollow;

    @FXML
    private AnchorPane autoFollowDetailOption;

    @FXML
    private AnchorPane followTarget;

    @FXML
    private TextField keywordAutoFollow;

    @FXML
    private AnchorPane timingConfigAutoFollow;

    @FXML
    private AutoFollowTargetController followTargetController;
    
    @FXML
    private AutoFollowDetailOptionController autoFollowDetailOptionController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert activateFlgAutoFollow != null : "fx:id=\"activateFlgAutoFollow\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert autoFollowDetailOption != null : "fx:id=\"autoFollowDetailOption\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert followTarget != null : "fx:id=\"followTarget\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert keywordAutoFollow != null : "fx:id=\"keywordAutoFollow\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        assert timingConfigAutoFollow != null : "fx:id=\"timingConfigAutoFollow\" was not injected: check your FXML file 'AutoFollow.fxml'.";
        
        activateFlgAutoFollow.setValue("有効");

        // 有効・無効の切り替え
        activateFlgAutoFollow.getSelectionModel().selectedIndexProperty().addListener(
            (ObservableValue<? extends Number> observableValue, Number oldIndex, Number newIndex) -> {
                changeActivate(newIndex.intValue() != 0);
            });
        changeActivate(activateFlgAutoFollow.getSelectionModel().getSelectedIndex() != 0);
    }

    private void changeActivate(boolean flg) {
        keywordAutoFollow.setDisable(flg);
        autoFollowDetailOptionController.changeActivate(flg);
        followTargetController.changeActivate(flg);
    }
}
