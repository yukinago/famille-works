package jp.co.famille.client.twitter.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jp.co.famille.client.twitter.TwitterWinClientApplication;
import lombok.Setter;

public class MenuController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button autoFunction;

    @FXML
    private Button basicFunction;

    @FXML
    private Button exit;

    @FXML
    private Button manageAccount;
    
    @Setter
    private Stage primaryStage;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        assert autoFunction != null : "fx:id=\"autoFunction\" was not injected: check your FXML file 'Menu.fxml'.";
        assert basicFunction != null : "fx:id=\"basicFunction\" was not injected: check your FXML file 'Menu.fxml'.";
        assert exit != null : "fx:id=\"exit\" was not injected: check your FXML file 'Menu.fxml'.";
        assert manageAccount != null : "fx:id=\"manageAccount\" was not injected: check your FXML file 'Menu.fxml'.";

        manageAccount.setOnAction(e -> {
            initManageAccount();
        });
        basicFunction.setOnAction(e -> {
            initBasicFunction();
        });
        autoFunction.setOnAction(e -> {
            initAutoFunction();
        });
        exit.setOnAction(e -> Platform.exit());
    }

    /**
     * アカウント管理画面を生成する。
     */
    private void initManageAccount() {
        // アカウント管理画面を生成する
        AnchorPane root = loadRootFromFxml("/jp/co/famille/client/twitter/ManageAccount.fxml");
        Stage stage = createStage(primaryStage, root, "アカウント管理", true);
        // 画面を表示する
        stage.show();
    }

    /**
     * 基本機能画面を生成する。
     */
    private void initBasicFunction() {
        // 基本機能画面を生成する
        AnchorPane root = loadRootFromFxml("/jp/co/famille/client/twitter/BasicFunction.fxml");
        Stage stage = createStage(primaryStage, root, "基本機能", true);

        // 画面を表示する
        stage.show();
    }

    /**
     * 自動処理機能画面を生成する。
     */
    private void initAutoFunction() {
        // 基本機能画面を生成する
        AnchorPane root = loadRootFromFxml("/jp/co/famille/client/twitter/AutoFunction.fxml");
        Stage stage = createStage(primaryStage, root, "自動処理機能", true);

        // 画面を表示する
        stage.show();
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
}
