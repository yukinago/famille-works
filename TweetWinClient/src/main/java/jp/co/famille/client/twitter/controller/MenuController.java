package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jp.co.famlle.javafx.FxmlUtils;
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
        AnchorPane root = FxmlUtils.loadRootFromFxml("/jp/co/famille/client/twitter/ManageAccount.fxml");
        Stage stage = FxmlUtils.createStage(primaryStage, root, "アカウント管理", true);
        // 画面を表示する
        stage.show();
    }

    /**
     * 基本機能画面を生成する。
     */
    private void initBasicFunction() {
        // 基本機能画面を生成する
        AnchorPane root = FxmlUtils.loadRootFromFxml("/jp/co/famille/client/twitter/BasicFunction.fxml");
        Stage stage = FxmlUtils.createStage(primaryStage, root, "基本機能", true);

        // 画面を表示する
        stage.show();
    }

    /**
     * 自動処理機能画面を生成する。
     */
    private void initAutoFunction() {
        // 基本機能画面を生成する
        AnchorPane root = FxmlUtils.loadRootFromFxml("/jp/co/famille/client/twitter/AutoFunction.fxml");
        Stage stage = FxmlUtils.createStage(primaryStage, root, "自動処理機能", true);

        // 画面を表示する
        stage.show();
    }
}
