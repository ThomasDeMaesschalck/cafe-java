package be.hogent.cafe.view;

import be.hogent.cafe.model.*;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;


public class CafeOverViewController {

    @FXML
    private Label loggedInUserName;

    @FXML
    private TilePane tablePane;

    @FXML
    private ScrollPane scrollPane;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public CafeOverViewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        String loggedInWaiter = mainApp.getModel().getNameOfLoggedInWaiter();
        loggedInUserName.setText("Logged in user: " + loggedInWaiter);
        showTables();

    }

    public void showTables() {
        scrollPane.setContent(tablePane);
        scrollPane.setFitToWidth(true);

        tablePane.setPadding(new Insets(5, 5, 5, 5));
        tablePane.setHgap(15);
        tablePane.setVgap(15);
        tablePane.setPrefColumns(6);
        tablePane.setMaxHeight(Region.USE_PREF_SIZE);


        for (Table table : mainApp.getModel().getTables()) {
            Button tableButton = new Button(table.toString());
            tableButton.setPrefSize(100, 100);
            tablePane.getChildren().addAll(tableButton);

            tableButton.setOnMouseClicked(mouseEvent -> {
                tableButton.setStyle("-fx-background-color: orange");

                String thisTable = tableButton.getText();
                for (Table table1 : mainApp.getModel().getTables()) {
                    if (table1.toString().equals(thisTable))
                        mainApp.getModel().setActiveTable(table1.getTableID());
                }
                mainApp.showCafeOrderDialog();
            });


            if (table.getBelongsToWaiter() == null) {
                tableButton.setStyle("-fx-background-color: grey");
            } else {

                if (table.getBelongsToWaiter().equals(mainApp.getModel().getLoggedInWaiter())) {
                    tableButton.setStyle("-fx-background-color: green");

                } else {
                    tableButton.setDisable(true);
                    tableButton.setStyle("-fx-background-color: red");
                }
            }
        }
    }

    public void logout() {
        mainApp.getModel().logOut();
        mainApp.showLogIn();
    }


    public void reports() {
        mainApp.showCafeReports();
    }
}