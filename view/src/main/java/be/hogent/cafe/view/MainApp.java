
package be.hogent.cafe.view;

import be.hogent.cafe.model.*;
import javafx.application.*;
import javafx.beans.Observable;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.util.HashSet;

public class MainApp extends Application {

    private final Cafe model;
    private Stage primaryStage;
    private BorderPane rootLayout;

    // ... AFTER THE OTHER VARIABLES

    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<Beverage> beverageData;




    /**
     * Constructor
     */
    public MainApp () {
        model = new Cafe ();
    }


    public static void main (String[] args) {
        launch (args);
    }

    /**
     * Returns the data as an observable list of Persons
     */
    public ObservableList<Beverage> getBeverageData () {
        return beverageData;
    }

    @Override
    public void start (Stage primaryStage) {
    beverageData = FXCollections.observableArrayList(model.getBeverages());

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle ("CafeApp");

        initRootLayout ();

        showCafeOverview ();
    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout () {
        try {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation (MainApp.class.getResource ("/cafe/RootLayout.fxml"));

            rootLayout = loader.load ();

            // Show the scene containing the root layout.
            Scene scene = new Scene (rootLayout);
            primaryStage.setScene (scene);
            primaryStage.show ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    private void showCafeOverview () {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation (getClass ().getResource ("/cafe/CafeOverview.fxml"));
            AnchorPane cafeOverview = loader.load ();

            // Set person overview into the center of root layout.
            rootLayout.setCenter (cafeOverview);

            // Give the controller access to the main app.
            be.hogent.cafe.view.CafeOverViewController controller = loader.getController ();
            controller.setModel (model);

            controller.setCafeView (this);

        } catch (IOException e) {
            e.printStackTrace ();
        }
    }



    public Stage getPrimaryStage () {
        return primaryStage;
    }
}




