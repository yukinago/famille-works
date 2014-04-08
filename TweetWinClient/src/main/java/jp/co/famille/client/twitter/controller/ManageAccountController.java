package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

public class ManageAccountController implements Initializable {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private TableView tblAccount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert btnAdd != null : "fx:id=\"btnAdd\" was not injected: check your FXML file 'ManageAccount.fxml'.";
        assert btnDelete != null : "fx:id=\"btnDelete\" was not injected: check your FXML file 'ManageAccount.fxml'.";
        assert tblAccount != null : "fx:id=\"tblAccount\" was not injected: check your FXML file 'ManageAccount.fxml'.";
        
        btnDelete.setDisable(true);
        
        btnAdd.setOnAction(e -> {
            // TODO 追加ボタン押下時の処理
            // 認証画面を開く
            // 確認ダイアログを開く
            // PINコードの認証を行う
            // DBに追加したアカウント情報を保存する
            // テーブルにアカウントを追加する
        });
        
        btnDelete.setOnAction(e -> {
            // TODO 削除ボタン押下時の処理
            // テーブルのアカウントを削除する
            // 当該レコードをDBから削除する
        });
    }

}
