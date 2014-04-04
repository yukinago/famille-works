/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.famille.client.twitter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.annotation.Resource;

/**
 * Windows向けツイッタークライアントのメインクラス。
 *
 * @author yukinago
 */
public class TwitterWinClientApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
//            btn.setOnAction(new EventHandler<ActionEvent>() {
//                
//                @Override
//                public void handle(ActionEvent event) {
//                    System.out.println("Hello World!");
//                }
//            });

            // fxmlファイルをロードする
            AnchorPane root = FXMLLoader.load(getClass().getResource("Menu.fxml"));

            for (Node node : root.getChildren()) {
                Button btn = (Button) node;
                switch (node.getId()) {
                    case "manageAccount":
                        // アカウント管理ボタン押下時の処理
                        btn.setOnAction(e -> {
                            initManageAccount(primaryStage);
                        });
                        break;
                    case "basicFunction":
                        // 基本機能ボタン押下時の処理
                        break;
                    case "autoFunction":
                        // 自動処理機能ボタン押下時の処理
                        break;
                    case "exit":
                        // 終了ボタン押下時の処理
                        btn.setOnAction(e -> Platform.exit());
                        break;
                    default:
                        break;
                }
            }

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

    /**
     * 新しい画面を生成する。
     *
     * @param owner 親画面
     * @param root ルート要素
     * @param title 画面タイトル
     * @param isModal モーダルか否か
     * @return 新規画面
     */
    private Stage createStage(Stage owner, AnchorPane root, String title, boolean isModal) {
        // 新しいウインドウを生成
        Stage stage = new Stage();

        // オーナーを設定
        stage.initOwner(owner);

        // モーダルフラグがONの場合はモーダルウインドウとする
        if (isModal) {
            stage.initModality(Modality.APPLICATION_MODAL);
        }

        // タイトルを設定する
        stage.setTitle(title);

        // シーンを設定する
        stage.setScene(new Scene(root));

        return stage;
    }

    /**
     * FXMLファイルからルート要素を生成する。
     *
     * @param fxmlName FXMLファイル名
     * @return ルート要素
     */
    private AnchorPane loadRootFromFxml(String fxmlName) {
        AnchorPane root = null;
        try {
            // fxmlファイルをロードする
            root = FXMLLoader.load(getClass().getResource(fxmlName));
        } catch (IOException ex) {
            Logger.getLogger(TwitterWinClientApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return root;
    }

    private void initManageAccount(Stage primaryStage) {
        // アカウント管理画面を生成する
        AnchorPane root = loadRootFromFxml("ManageAccount.fxml");
        Stage stage = createStage(primaryStage, root, "アカウント管理", true);

        for (Node node : root.getChildren()) {
            if(!(node instanceof Button)) {
                continue;
            }
            
            Button btn = (Button)node;
            
            switch (node.getId()) {
                case "add":
                    // 追加ボタン押下時の処理
//                    btn.setOnAction(e -> {
//                        initManageAccount(primaryStage);
//                    });
                    break;
                case "delete":
                    // 削除ボタン押下時の処理
                    break;
                case "autoFunction":
                    // 自動処理機能ボタン押下時の処理
                    break;
                case "exit":
                    // 終了ボタン押下時の処理
                    btn.setOnAction(e -> stage.close());
                    break;
                default:
                    break;
            }
        }

        // 画面を表示する
        stage.show();
    }

}
