package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;

public class BasicFunctionController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane followPeriod;

    @FXML
    private TabPane functionTabPane;

    @FXML
    private TextField keyword;

    @FXML
    private AnchorPane reTweetPeriod;

    @FXML
    private Tab tabFollow;

    @FXML
    private Tab tabTweet;

    @FXML
    private ChoiceBox<?> targetAccount;

    @FXML
    private AnchorPane unFollowPeriod;

    @FXML
    private CheckBox useKeyword;

    @Setter
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert followPeriod != null : "fx:id=\"followPeriod\" was not injected: check your FXML file 'BasicFunction.fxml'.";
        assert functionTabPane != null : "fx:id=\"functionTabPane\" was not injected: check your FXML file 'BasicFunction.fxml'.";
        assert keyword != null : "fx:id=\"keyword\" was not injected: check your FXML file 'BasicFunction.fxml'.";
        assert reTweetPeriod != null : "fx:id=\"reTweetPeriod\" was not injected: check your FXML file 'BasicFunction.fxml'.";
        assert tabFollow != null : "fx:id=\"tabFollow\" was not injected: check your FXML file 'BasicFunction.fxml'.";
        assert tabTweet != null : "fx:id=\"tabTweet\" was not injected: check your FXML file 'BasicFunction.fxml'.";
        assert targetAccount != null : "fx:id=\"targetAccount\" was not injected: check your FXML file 'BasicFunction.fxml'.";
        assert unFollowPeriod != null : "fx:id=\"unFollowPeriod\" was not injected: check your FXML file 'BasicFunction.fxml'.";
        assert useKeyword != null : "fx:id=\"useKeyword\" was not injected: check your FXML file 'BasicFunction.fxml'.";

        functionTabPane.setVisible(false);

        useKeyword.setOnAction(e -> {
            keyword.setDisable(!useKeyword.isSelected());
        });

        targetAccount.getSelectionModel().selectedIndexProperty().addListener(
            (ObservableValue<? extends Number> observableValue, Number number, Number number2) -> {
                functionTabPane.setVisible(true);
            });
    }

}
