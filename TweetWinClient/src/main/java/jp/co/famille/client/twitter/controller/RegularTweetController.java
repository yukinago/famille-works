/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;


public class RegularTweetController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button deleteItem;

    @FXML
    private Button downItem;

    @FXML
    private AnchorPane regularTweetDetailOption;

    @FXML
    private AnchorPane timingConfigNewsTweet;

    @FXML
    private Button upItem;


    @FXML
    void initialize() {
        assert deleteItem != null : "fx:id=\"deleteItem\" was not injected: check your FXML file 'RegularTweet.fxml'.";
        assert downItem != null : "fx:id=\"downItem\" was not injected: check your FXML file 'RegularTweet.fxml'.";
        assert regularTweetDetailOption != null : "fx:id=\"regularTweetDetailOption\" was not injected: check your FXML file 'RegularTweet.fxml'.";
        assert timingConfigNewsTweet != null : "fx:id=\"timingConfigNewsTweet\" was not injected: check your FXML file 'RegularTweet.fxml'.";
        assert upItem != null : "fx:id=\"upItem\" was not injected: check your FXML file 'RegularTweet.fxml'.";


    }

}
