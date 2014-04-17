package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import jfxtras.scene.control.CalendarTimePicker;

public class TimingConfigWithDateTimeController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private RadioButton timingOptionDateTime;

    @FXML
    private RadioButton timingOptionMargin;

    @FXML
    private ToggleGroup dateTimeOption;

    @FXML
    private TextField day;

    @FXML
    private RadioButton everyDay;

    @FXML
    private RadioButton everyMonth;

    @FXML
    private RadioButton everyWeek;

    @FXML
    private TextField fluctuationDateTime;

    @FXML
    private TextField fluctuationMargin;

    @FXML
    private TextField margin;

    @FXML
    private ToggleGroup timingOption;

    @FXML
    private ChoiceBox<?> weekday;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        assert timingOptionDateTime != null : "fx:id=\"TimingOptionDateTime\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert timingOptionMargin != null : "fx:id=\"TimingOptionMargin\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert dateTimeOption != null : "fx:id=\"dateTimeOption\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert day != null : "fx:id=\"day\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert everyDay != null : "fx:id=\"everyDay\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert everyMonth != null : "fx:id=\"everyMonth\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert everyWeek != null : "fx:id=\"everyWeek\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert fluctuationDateTime != null : "fx:id=\"fluctuationDateTime\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert fluctuationMargin != null : "fx:id=\"fluctuationMargin\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert margin != null : "fx:id=\"margin\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert timingOption != null : "fx:id=\"timingOption\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        assert weekday != null : "fx:id=\"weekday\" was not injected: check your FXML file 'TimingConfigWithDateTime.fxml'.";
        
        CalendarTimePicker timePicker = new CalendarTimePicker();
        ((GridPane)timingOptionDateTime.getParent()).add(timePicker, 0, 3, 3, 1);
        
        timingOptionMargin.setOnAction(e -> setDisabled());
        timingOptionDateTime.setOnAction(e -> setDisabled());
    }
    
    private void setDisabled() {
        boolean flg = timingOptionMargin.isSelected();
        
        margin.setDisable(flg);
        fluctuationMargin.setDisable(flg);
        
        everyDay.setDisable(flg);
        everyWeek.setDisable(flg);
        weekday.setDisable(flg);
        everyMonth.setDisable(flg);
        day.setDisable(flg);
        fluctuationDateTime.setDisable(flg);
    }
}
