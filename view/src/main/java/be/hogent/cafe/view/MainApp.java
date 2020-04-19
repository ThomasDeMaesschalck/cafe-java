
package be.hogent.cafe.view;

import be.hogent.cafe.model.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.io.*;

public class MainApp extends Application {

    private final Cafe model = new Cafe();
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
        this.primaryStage.setTitle ("Cafe Java");

        initRootLayout ();

        showLogIn ();
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
            primaryStage.setResizable(false);
            primaryStage.show ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    protected void showLogIn () {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation (getClass ().getResource ("/cafe/LogIn.fxml"));
            AnchorPane LogIn = loader.load ();

            // Set person overview into the center of root layout.
            rootLayout.setCenter (LogIn);

            // Give the controller access to the main app.
            be.hogent.cafe.view.LogInController controller = loader.getController ();
            controller.setMainApp (this);

        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void showCafeOverview () {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation (getClass ().getResource ("/cafe/CafeOverview.fxml"));
            AnchorPane cafeOverview = loader.load ();

            // Set person overview into the center of root layout.
            rootLayout.setCenter (cafeOverview);

            // Give the controller access to the main app.
            be.hogent.cafe.view.CafeOverViewController controller = loader.getController ();
            controller.setMainApp (this);

        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public void showCafeReports () {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation (getClass ().getResource ("/cafe/CafeReports.fxml"));
            AnchorPane cafeReports = loader.load ();

            // Set person overview into the center of root layout.
            rootLayout.setCenter (cafeReports);

            // Give the controller access to the main app.
            be.hogent.cafe.view.CafeReportsController controller = loader.getController ();
            controller.setMainApp (this);

        } catch (IOException e) {
            e.printStackTrace ();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cafe getModel() {
        return model;
    }

    public Stage getPrimaryStage () {
        return primaryStage;
    }
}




