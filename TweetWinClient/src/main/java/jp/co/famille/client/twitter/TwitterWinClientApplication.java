/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.famille.client.twitter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jp.co.famille.client.twitter.controller.MenuController;

/**
 * Windows向けツイッタークライアントのメインクラス。
 *
 * @author yukinago
 */
public class TwitterWinClientApplication extends Application {
    
    private MenuController menuController;

    @Override
    public void start(Stage primaryStage) {
        try {
            // fxmlファイルをロードする
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
            AnchorPane root = loader.load();
            
            // 親画面をコントローラに渡す
            menuController = loader.getController();
            menuController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("メニュー");
            primaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(TwitterWinClientApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * メインメソッド。 アプリケーションを起動する。
     *
     * @param args 実行時引数（使用しない）
     */
    public static void main(String[] args) {
        launch(args);
    }

}
