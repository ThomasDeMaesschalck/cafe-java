package be.hogent.cafe.view;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class CafeOverViewController {

    @FXML
    private TableView<Beverage> beverageTable;
    @FXML
    private TableColumn<Beverage, String> beverageNameColumn;
    @FXML
    private TableColumn<Beverage, String> beveragePriceColumn;

    @FXML
    private Label loggedInUserName;

    @FXML
    private TilePane tablePane;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public CafeOverViewController () {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize () {
        PropertyValueFactory<Beverage, String> beverageNameProperty =
                new PropertyValueFactory<> ("beverageName");
        PropertyValueFactory<Beverage, String> beveragePriceProperty =
                new PropertyValueFactory<> ("price");
        beverageNameColumn.setCellValueFactory (beverageNameProperty);
        beveragePriceColumn.setCellValueFactory (beveragePriceProperty);

    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     *
     */
    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        beverageTable.setItems (mainApp.getBeverageData ());
        beverageTable.getSortOrder().add(beverageNameColumn);
        beverageTable.sort();

        String loggedInWaiter = mainApp.getModel().getNameOfLoggedInWaiter();
        loggedInUserName.setText("Logged in user: " + loggedInWaiter);

        showTables();

    }

    public void showTables(){
        for (Table table: mainApp.getModel().getTables()) {
            Button tableButton = new Button(table.toString());
            tablePane.setHgap(5);
            tablePane.setVgap(5);
            tablePane.setPrefColumns(4);
            tablePane.getChildren().addAll(tableButton);
            tableButton.setMinSize(100, 100);

            tableButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {

                    //mss hier met hashmap werken om ID beter te krijgen en op 1 te laten starten met string als value

            String thisTable = tableButton.getText();
                    for (Table table: mainApp.getModel().getTables()) {
                        if (table.toString().equals(thisTable))
                            mainApp.getModel().setActiveTable(table.getTableID());
                    }
                    tableButton.setStyle("-fx-background-color: green");

                }
            });



            if (table.getBelongsToWaiter() == null)
            {
                tableButton.setStyle("-fx-background-color: grey");
            }
            else {

                if (table.getBelongsToWaiter().equals(mainApp.getModel().getLoggedInWaiter()))
                {
                    tableButton.setStyle("-fx-background-color: green");

                }
                else
                {
                    tableButton.setStyle("-fx-background-color: red");

                }
            }

        }

    }

    public void logout()
    {
            mainApp.getModel().logOut();
            mainApp.showLogIn();
    }


    public void reports() {
        mainApp.showCafeReports();
    }
}