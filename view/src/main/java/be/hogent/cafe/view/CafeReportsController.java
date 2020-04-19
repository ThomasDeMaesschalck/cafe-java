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

public class CafeReportsController {


    @FXML
    private Label loggedInUserName;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public CafeReportsController () {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize () {

    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     *
     */
    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;


        String loggedInWaiter = mainApp.getModel().getNameOfLoggedInWaiter();
        loggedInUserName.setText("Logged in user: " + loggedInWaiter);


    }
    public void logout()
    {
        mainApp.getModel().logOut();
        mainApp.showLogIn();
    }


    public void home() {
        mainApp.showCafeOverview();
    }

    public void pieChart() {

    }
}