package jp.co.famille.client.twitter.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class PeriodFromToController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox chkUsePeriod;

    @FXML
    private GridPane periodGrid;

    @FXML
    private RadioButton radioBetween;

    @FXML
    private RadioButton radioLatest;
    
    @FXML
    private TextField latestValue;
    
    @FXML
    private ChoiceBox latestUnit;

    @FXML
    private ToggleGroup periodOptions;
    
    private DatePicker fromDate;
    private DatePicker toDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert chkUsePeriod != null : "fx:id=\"chkUsePeriod\" was not injected: check your FXML file 'PeriodFromTo.fxml'.";
        assert latestUnit != null : "fx:id=\"latestUnit\" was not injected: check your FXML file 'PeriodFromTo.fxml'.";
        assert latestValue != null : "fx:id=\"latestValue\" was not injected: check your FXML file 'PeriodFromTo.fxml'.";
        assert periodGrid != null : "fx:id=\"periodGrid\" was not injected: check your FXML file 'PeriodFromTo.fxml'.";
        assert periodOptions != null : "fx:id=\"periodOptions\" was not injected: check your FXML file 'PeriodFromTo.fxml'.";
        assert radioBetween != null : "fx:id=\"radioBetween\" was not injected: check your FXML file 'PeriodFromTo.fxml'.";
        assert radioLatest != null : "fx:id=\"radioLatest\" was not injected: check your FXML file 'PeriodFromTo.fxml'.";

        latestUnit.getSelectionModel().selectFirst();
        
        fromDate = addDatePicker(periodGrid, 1, 1, 2, 1);
        toDate = addDatePicker(periodGrid, 4, 1, 1, 1);
        
        chkUsePeriod.setOnAction(e -> {
            boolean disabled = !chkUsePeriod.isSelected();
            radioLatest.setDisable(disabled);
            radioBetween.setDisable(disabled);
            latestValue.setDisable(disabled);
            latestUnit.setDisable(disabled);
            fromDate.setDisable(disabled);
            toDate.setDisable(disabled);
        });
    }

    private DatePicker addDatePicker(GridPane grid, int column, int row, int colspan, int rowspan) {
        final DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setDisable(true);
        grid.add(datePicker, column, row, colspan, rowspan);
        return datePicker;
    }
}
