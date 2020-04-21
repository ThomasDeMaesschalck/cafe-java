package be.hogent.cafe.view;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.*;
import be.hogent.cafe.model.dao.DAOException;
import be.hogent.cafe.model.dao.PaidOrderDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CafeReportsController {


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
    private TableView<BeverageSales> allSalesTable;
    @FXML
    private TableView<BeverageSales> allSalesByDateTable;
    @FXML
    private TableColumn<BeverageSales, String> beverageNameColumn;
    @FXML
    private TableColumn<BeverageSales, Double> beveragePriceColumn;
    @FXML
    private TableColumn<BeverageSales, Integer> beverageQtyColumn;
    @FXML
    private TableColumn<BeverageSales, BigDecimal> beverageSubTotalColumn;
    @FXML
    private TableColumn<BeverageSales, String> beverageNameByDateColumn;
    @FXML
    private TableColumn<BeverageSales, Double> beveragePriceByDateColumn;
    @FXML
    private TableColumn<BeverageSales, Integer> beverageQtyByDateColumn;
    @FXML
    private TableColumn<BeverageSales, BigDecimal> beverageSubTotalByDateColumn;
    @FXML
    private Label salesTotal;
    @FXML
    private Label salesByDateTotal;
    @FXML
    private ComboBox<LocalDate> selectDatesBox;

    final List<BeverageSales> salesItems = new ArrayList<>();
    final List<BeverageSales> salesByDateItems = new ArrayList<>();
    private ObservableList<BeverageSales> salesByDateItemList;
    private LocalDate selectedDate = null;


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
        PropertyValueFactory<BeverageSales, String> beverageNameProperty =
                new PropertyValueFactory<> ("beverageName");
        PropertyValueFactory<BeverageSales, Double> beveragePriceProperty =
                new PropertyValueFactory<> ("beveragePrice");
        PropertyValueFactory<BeverageSales, Integer> beverageQtyProperty =
                new PropertyValueFactory<> ("beverageQty");
        PropertyValueFactory<BeverageSales, BigDecimal> beverageSubTotalProperty =
                new PropertyValueFactory<> ("subTotal");
        beverageNameColumn.setCellValueFactory (beverageNameProperty);
        beveragePriceColumn.setCellValueFactory (beveragePriceProperty);
        beverageQtyColumn.setCellValueFactory (beverageQtyProperty);
        beverageSubTotalColumn.setCellValueFactory (beverageSubTotalProperty);

        beverageNameByDateColumn.setCellValueFactory (beverageNameProperty);
        beveragePriceByDateColumn.setCellValueFactory (beveragePriceProperty);
        beverageQtyByDateColumn.setCellValueFactory (beverageQtyProperty);
        beverageSubTotalByDateColumn.setCellValueFactory (beverageSubTotalProperty);
    }

    public void setMainApp (MainApp mainApp) throws Exception {
        this.mainApp = mainApp;

        String loggedInWaiter = mainApp.getModel().getNameOfLoggedInWaiter();
        loggedInUserName.setText("Logged in user: " + loggedInWaiter);
        allSalesTab = generateAllSalesTab();
        byDateTab = generateSalesByDateTab();
        pieTab.setContent(generatePieTab());
    }

    private Node generatePieTab() throws Exception {
        mainApp.getModel().topWaiterPieChart();
        String pieChartjpg = Cafe.getReportsDirectory() + "/topwaiterchart.jpg";
        File file = new File(pieChartjpg);
        Image image = new Image(file.toURI().toString());
        return new ImageView(image);
    }

    private Tab generateAllSalesTab(){
        Tab tab = new Tab("All sales of waiter:");
        AtomicReference<BigDecimal> waiterSalesTotal = new AtomicReference<>(new BigDecimal(0).setScale(2, RoundingMode.HALF_EVEN));


        Map<Beverage, Integer> salesMap = mainApp.getModel().getAllWaiterSales();
        salesMap.forEach((k,v) ->
        {
            BeverageSales beverageLine = new BeverageSales(k.getBeverageName(), k.getPrice(), v, BigDecimal.valueOf(k.getPrice()* v) );
            salesItems.add(beverageLine);
        }
        );
        salesMap.forEach((k,v) -> waiterSalesTotal.updateAndGet(v1 -> v1.add(BigDecimal.valueOf(k.getPrice() * v).setScale(2, RoundingMode.HALF_EVEN))));

        salesTotal.setText(String.valueOf(waiterSalesTotal));

        ObservableList<BeverageSales> salesItemList = FXCollections.observableArrayList(salesItems);

        allSalesTable.setItems(FXCollections.observableArrayList((salesItemList)));
        allSalesTable.getSortOrder().add(beverageNameColumn);

        return tab;
    }

    private Tab generateSalesByDateTab() throws DAOException {
        Tab tab = new Tab("Sales by date:");

        int waiterID = mainApp.getModel().getLoggedInWaiter().getID();
        Set<LocalDate> waiterDatesFromDB = PaidOrderDAOImpl.getInstance().waiterSalesDates(waiterID);
        selectDatesBox.setItems(FXCollections.observableArrayList(waiterDatesFromDB));

        return tab;
    }

    public void generatePDF() throws IOException {
        mainApp.getModel().waiterSalesReportPDF(selectedDate);
    }

    public void logout()
    {
        mainApp.getModel().logOut();
        mainApp.showLogIn();
    }


    public void home() {
        mainApp.showCafeOverview();
    }

    public void comboBoxClick() {
        AtomicReference<BigDecimal> waiterSalesTotalByDate = new AtomicReference<>(new BigDecimal(0).setScale(2, RoundingMode.HALF_EVEN));

        salesByDateItems.clear();

        selectedDate = selectDatesBox.getSelectionModel().getSelectedItem();
        Map<Beverage, Integer> salesByDateMap = mainApp.getModel().getAllWaiterSales(selectedDate);
        salesByDateMap.forEach((k,v) ->
                {
                    BeverageSales beverageLine = new BeverageSales(k.getBeverageName(), k.getPrice(), v, BigDecimal.valueOf(k.getPrice()* v) );

                    salesByDateItems.add(beverageLine);
                }
        );
        salesByDateMap.forEach((k,v) -> waiterSalesTotalByDate.updateAndGet(v1 -> v1.add(BigDecimal.valueOf(k.getPrice() * v).setScale(2, RoundingMode.HALF_EVEN))));

        salesByDateTotal.setText(String.valueOf(waiterSalesTotalByDate));

        salesByDateItemList = FXCollections.observableArrayList (salesByDateItems);

        allSalesByDateTable.getSortOrder().add(beverageNameColumn);
        allSalesByDateTable.setItems(salesByDateItemList);
    }


    public class BeverageSales{
        public final String beverageName;
        public final Double beveragePrice;
        public final Integer beverageQty;
        public final BigDecimal subTotal;

    public BeverageSales(String beverageName, Double beveragePrice, Integer beverageQty, BigDecimal subTotal) {
        this.beverageName = beverageName;
        this.beveragePrice = beveragePrice;
        this.beverageQty = beverageQty;
        this.subTotal = subTotal.setScale(2, RoundingMode.HALF_EVEN);
    }

    public String getBeverageName() { //these are necessary for the TableView
        return beverageName;
    }

    public Double getBeveragePrice() {
        return beveragePrice;
    }

    public Integer getBeverageQty() {
        return beverageQty;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }
}
    }