package be.hogent.cafe.view;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;

public class CafeOverViewController {

    @FXML
    private TableView<Beverage> beverageTable;
    @FXML
    private TableColumn<Beverage, String> beverageNameColumn;
    @FXML
    private TableColumn<Beverage, String> beveragePriceColumn;


    // Reference to the main application.
    private MainApp cafeView;

    private Cafe model;

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
     * @param cafeView
     */
    public void setCafeView (MainApp cafeView) {
        this.cafeView = cafeView;

        // Add observable list data to the table
        beverageTable.setItems (cafeView.getBeverageData ());
    }

    public void setModel (Cafe model) {
        this.model = model;
    }



}