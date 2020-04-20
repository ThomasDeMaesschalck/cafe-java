package be.hogent.cafe.view;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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

    @FXML
    private TableView<BeverageSales> allSalesTable;
    @FXML
    private TableColumn<BeverageSales, String> beverageNameColumn;
    @FXML
    private TableColumn<BeverageSales, Double> beveragePriceColumn;
    @FXML
    private TableColumn<BeverageSales, Integer> beverageQtyColumn;
    @FXML
    private TableColumn<BeverageSales, BigDecimal> beverageSubTotalColumn;
    @FXML
    private Label salesTotal;

    List<BeverageSales> salesItems = new ArrayList<>();
    private ObservableList<BeverageSales> salesItemList;
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
        allSalesTab = generateAllSalesTab("All sales of waiter:");
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

    private Tab generateAllSalesTab(String tabName){
        Tab tab = new Tab(tabName);
        final Group root = new Group();
        tab.setContent(root);
        AtomicReference<BigDecimal> waiterSalesTotal = new AtomicReference<>(new BigDecimal(0).setScale(2, RoundingMode.HALF_EVEN));


        Map<Beverage, Integer> salesMap = mainApp.getModel().getAllWaiterSales();
        salesMap.forEach((k,v) ->
        {
            BeverageSales beverageLine = new BeverageSales(k.getBeverageName(), k.getPrice(), v.intValue(), BigDecimal.valueOf(k.getPrice()*v.intValue()) );

            salesItems.add(beverageLine);
        }
        );
        salesMap.forEach((k,v) -> {
           waiterSalesTotal.updateAndGet(v1 -> v1.add(BigDecimal.valueOf(k.getPrice() * v.intValue()).setScale(2, RoundingMode.HALF_EVEN)));
        });

        salesTotal.setText(String.valueOf(waiterSalesTotal));

        salesItemList = FXCollections.observableArrayList (salesItems);

        allSalesTable.setItems((salesItemList));
        allSalesTable.getSortOrder().add(beverageNameColumn);


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

    public class BeverageSales{
        public String beverageName;
        public Double beveragePrice;
        public Integer beverageQty;
        public BigDecimal subTotal;

    public BeverageSales(String beverageName, Double beveragePrice, Integer beverageQty, BigDecimal subTotal) {
        this.beverageName = beverageName;
        this.beveragePrice = beveragePrice;
        this.beverageQty = beverageQty;
        this.subTotal = subTotal.setScale(2, RoundingMode.HALF_EVEN);
    }

    public String getBeverageName() {
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