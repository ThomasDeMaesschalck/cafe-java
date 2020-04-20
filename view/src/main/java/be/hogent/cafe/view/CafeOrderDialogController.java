package be.hogent.cafe.view;

import be.hogent.cafe.model.Beverage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CafeOrderDialogController {

    public Label tableIDNumber;
    @FXML
    private TableView<Beverage> beverageTable;
    @FXML
    private TableColumn<Beverage, String> beverageNameColumn;
    @FXML
    private TableColumn<Beverage, String> beveragePriceColumn;

    private Stage dialogStage;

    // Reference to the main application.
    private MainApp mainApp;

    public CafeOrderDialogController(){
    }

    @FXML
    private void initialize () {
        PropertyValueFactory<Beverage, String> beverageNameProperty =
                new PropertyValueFactory<> ("beverageName");
        PropertyValueFactory<Beverage, String> beveragePriceProperty =
                new PropertyValueFactory<> ("price");
        beverageNameColumn.setCellValueFactory (beverageNameProperty);
        beveragePriceColumn.setCellValueFactory (beveragePriceProperty);


    }

    public void setDialogStage (Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        beverageTable.setItems (mainApp.getBeverageData ());
        beverageTable.getSortOrder().add(beverageNameColumn);
        beverageTable.sort();

        int tableID = mainApp.getModel().getActiveTable().getTableID();
        tableIDNumber.setText(String.valueOf(tableID));
    }

    @FXML
    private void handleClose () {
        dialogStage.close ();
    }

}
