package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;


public class AutoFunctionController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnConfig;

    @FXML
    private ToggleButton btnStart;

    @FXML
    private ToggleButton btnStop;

    @FXML
    private CheckBox chkAutoFollow;

    @FXML
    private CheckBox chkAutoReFollow;

    @FXML
    private CheckBox chkAutoUnFollow;

    @FXML
    private CheckBox chkNewsTweet;

    @FXML
    private CheckBox chkRegularTweet;

    @FXML
    private ToggleGroup funcState;

    @FXML
    private TextArea logArea;

    @FXML
    private ListView<?> targetAccount;


    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        assert btnConfig != null : "fx:id=\"btnConfig\" was not injected: check your FXML file 'AutoFunction.fxml'.";
        assert btnStart != null : "fx:id=\"btnStart\" was not injected: check your FXML file 'AutoFunction.fxml'.";
        assert btnStop != null : "fx:id=\"btnStop\" was not injected: check your FXML file 'AutoFunction.fxml'.";
        assert chkAutoFollow != null : "fx:id=\"chkAutoFollow\" was not injected: check your FXML file 'AutoFunction.fxml'.";
        assert chkAutoReFollow != null : "fx:id=\"chkAutoReFollow\" was not injected: check your FXML file 'AutoFunction.fxml'.";
        assert chkAutoUnFollow != null : "fx:id=\"chkAutoUnFollow\" was not injected: check your FXML file 'AutoFunction.fxml'.";
        assert chkNewsTweet != null : "fx:id=\"chkNewsTweet\" was not injected: check your FXML file 'AutoFunction.fxml'.";
        assert chkRegularTweet != null : "fx:id=\"chkRegularTweet\" was not injected: check your FXML file 'AutoFunction.fxml'.";
        assert funcState != null : "fx:id=\"funcState\" was not injected: check your FXML file 'AutoFunction.fxml'.";
        assert logArea != null : "fx:id=\"logArea\" was not injected: check your FXML file 'AutoFunction.fxml'.";
        assert targetAccount != null : "fx:id=\"targetAccount\" was not injected: check your FXML file 'AutoFunction.fxml'.";


    }

}