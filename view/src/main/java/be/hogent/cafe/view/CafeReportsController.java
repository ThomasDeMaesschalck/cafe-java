package be.hogent.cafe.view;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.File;

public class CafeReportsController {


    @FXML
    public ImageView pieChart;

    @FXML
    private Label loggedInUserName;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab allSalesTab;

    @FXML
    private Tab byDateTab;

    @FXML
    private Tab pieTab;

    @FXML
    private ImageView iv;

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
    public void setMainApp (MainApp mainApp) throws Exception {
        this.mainApp = mainApp;

        mainApp.getModel().topWaiterPieChart(); //exception handling toevoegen

        String loggedInWaiter = mainApp.getModel().getNameOfLoggedInWaiter();
        loggedInUserName.setText("Logged in user: " + loggedInWaiter);
    allSalesTab.setText("All sales of waiter:");
        byDateTab.setText("All sales of date:");
        pieTab = generatePieTab("Top waiter pie chart");
    }

    private Tab generatePieTab(String tabName){
        Tab tab = new Tab(tabName);
        final Group root = new Group();
        tab.setContent(root);

        String pieChartjpg = Cafe.getReportsDirectory() + "/topwaiterchart.jpg";
        File file = new File(pieChartjpg);
        Image image = new Image(file.toURI().toString());
        iv = new ImageView(image);
        pieTab.setContent(iv);
        root.getChildren().add(iv);

        return tab;
    }

    public void logout()
    {
        mainApp.getModel().logOut();
        mainApp.showLogIn();
    }


    public void home() {
        mainApp.showCafeOverview();
    }



    }