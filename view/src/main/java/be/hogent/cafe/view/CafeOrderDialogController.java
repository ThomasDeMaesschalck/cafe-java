package be.hogent.cafe.view;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.Order;
import be.hogent.cafe.model.OrderItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class CafeOrderDialogController {

    public Label tableIDNumber;
    @FXML
    private TableView<Beverage> beverageTable;
    @FXML
    private TableColumn<Beverage, String> beverageNameColumn;
    @FXML
    private TableColumn<Beverage, String> beveragePriceColumn;

    @FXML
    private TableView<OrderLines>  orderTable;
    @FXML
    private TableColumn<OrderLines, String> orderBeverageNameColumn;
    @FXML
    private TableColumn<OrderLines, Integer> orderBeverageQtyColumn;
    @FXML
    private TableColumn<OrderLines, BigDecimal> orderSubTotalColumn;
    @FXML
    private Label orderTotal;

    private final List<OrderLines> orderItems = new ArrayList<>();


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


        PropertyValueFactory<OrderLines, String> orderBeverageNameProperty =
                new PropertyValueFactory<> ("orderBeverageName");
        PropertyValueFactory<OrderLines, Integer> orderBeverageQtyProperty =
                new PropertyValueFactory<> ("orderBeverageQty");
        PropertyValueFactory<OrderLines, BigDecimal> orderSubTotalProperty =
                new PropertyValueFactory<> ("orderSubTotal");
        orderBeverageNameColumn.setCellValueFactory (orderBeverageNameProperty);
        orderBeverageQtyColumn.setCellValueFactory (orderBeverageQtyProperty);
        orderSubTotalColumn.setCellValueFactory (orderSubTotalProperty);

    }



    public void setDialogStage (Stage dialogStage) {
        this.dialogStage = dialogStage;
        dialogStage.setOnCloseRequest(evt -> {
            // prevent window from closing
            evt.consume();
            // execute own shutdown procedure
            handleClose();
        });
    }


    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        beverageTable.setItems (mainApp.getBeverageData ());
        beverageTable.getSortOrder().add(beverageNameColumn);
        beverageTable.sort();

        int tableID = mainApp.getModel().getActiveTable().getTableID();
        tableIDNumber.setText(String.valueOf(tableID));

        if (!(mainApp.getModel().getUnpaidOrders().get(mainApp.getModel().getActiveTable()) == null))
        {
            populateOrderLinesTable();
        }
    }

    public void populateOrderLinesTable() {
        Set<Order> orderMap = new HashSet<>();
        orderItems.clear(); //resetten
        double orderLinesTotal = 0.0;

        orderMap.add(mainApp.getModel().getUnpaidOrders().get(mainApp.getModel().getActiveTable()));

        for (Order o : orderMap) {
            for (OrderItem oi : o.getOrderLines()) {
                OrderLines orderLine = new OrderLines(oi.getBeverage().getBeverageName(), oi.getBeverage().getPrice(), oi.getQty(), BigDecimal.valueOf(oi.getBeverage().getPrice() * oi.getQty()), new OrderItem(oi.getBeverage(), oi.getQty()));
                orderLinesTotal = orderLinesTotal + ((oi.getBeverage().getPrice() * oi.getQty()));
                orderItems.add(orderLine);
            }

            ObservableList<OrderLines> orderLinesList = FXCollections.observableArrayList(orderItems);
            orderTable.setItems(FXCollections.observableArrayList((orderLinesList)));
            String orderLinesTotaltoString = String.format("%.2f", orderLinesTotal); //afronden
            orderTotal.setText(String.valueOf(orderLinesTotaltoString));

        }
    }

    @FXML
    private void handleClose () {
        dialogStage.close ();
        //order volledig verwijderen indien alle orderlines verwijderd zijn in dialog
        if(mainApp.getModel().getUnpaidOrders().containsKey(mainApp.getModel().getActiveTable())
                && mainApp.getModel().getUnpaidOrders().get(mainApp.getModel().getActiveTable()).getOrderLines().isEmpty())
        {
            mainApp.getModel().getUnpaidOrders().remove(mainApp.getModel().getActiveTable());
        }
        //waiter assignment verwijderen indien tafel aangeklikt werd maar er geen order geplaatst werd
        if(!mainApp.getModel().getUnpaidOrders().containsKey(mainApp.getModel().getActiveTable()))
        {
            mainApp.getModel().clearTable();
        }
        mainApp.showCafeOverview();
    }

    @FXML
    public void handleIncrease()
    {
        if (orderTable.getSelectionModel().getSelectedItem() == null)
        {
            nothingSelectedDialog();
        }
        else {

            OrderLines selectedItem = orderTable.getSelectionModel().getSelectedItem();
            OrderItem oi = orderTable.getSelectionModel().getSelectedItem().getOrderItem();

            for (OrderItem openOrders : mainApp.getModel().getUnpaidOrders().get(mainApp.getModel().getActiveTable()).getOrderLines()
            ) {
                if (openOrders.equals(oi)) {
                    openOrders.increaseQty();
                }
            }
            populateOrderLinesTable();
            orderTable.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void handleDecrease()
    {
        if (orderTable.getSelectionModel().getSelectedItem() == null)
        {
           nothingSelectedDialog();
        }
        else {
            OrderLines selectedItem = orderTable.getSelectionModel().getSelectedItem();

            OrderItem oi = orderTable.getSelectionModel().getSelectedItem().getOrderItem();

            for (OrderItem openOrders : mainApp.getModel().getUnpaidOrders().get(mainApp.getModel().getActiveTable()).getOrderLines()
            ) {
                if (openOrders.equals(oi) && (openOrders.getQty() == 1)) {
                    String errorMessage = "";
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initOwner(dialogStage);
                    alert.setTitle("Quantity can't go to zero");
                    alert.setHeaderText("Please remove orderline instead");
                    alert.setContentText(errorMessage);

                    alert.showAndWait();
                }
                if (openOrders.equals(oi) && (openOrders.getQty() > 1)) {
                    openOrders.decreaseQty();
                }
            }
            populateOrderLinesTable();
            orderTable.getSelectionModel().select(selectedItem);
        }
    }


    @FXML
    public void handleDelete() {
        if (orderTable.getSelectionModel().getSelectedItem() == null) {
            nothingSelectedDialog();
        } else {
            OrderItem oi = orderTable.getSelectionModel().getSelectedItem().getOrderItem();
            mainApp.getModel().removeOrder(oi);
            populateOrderLinesTable();
        }
    }

    public void handleAdd() {
        if(beverageTable.getSelectionModel().getSelectedItem() == null)
        {
            String errorMessage = "No beverage selected";
            Alert alert = new Alert (Alert.AlertType.ERROR);
            alert.initOwner (dialogStage);
            alert.setTitle ("Nothing selected");
            alert.setHeaderText ("Please select a beverage first");
            alert.setContentText (errorMessage);
            alert.showAndWait ();
        }
        else {
            Beverage beverage = beverageTable.getSelectionModel().getSelectedItem();
            mainApp.getModel().placeOrder(beverage, 1);
            populateOrderLinesTable();
        }
    }

    public void nothingSelectedDialog(){
        String errorMessage = "Nothing selected";
        Alert alert = new Alert (Alert.AlertType.ERROR);
        alert.initOwner (dialogStage);
        alert.setTitle ("Nothing selected");
        alert.setHeaderText ("Please select orderline first");
        alert.setContentText (errorMessage);
        alert.showAndWait ();
    }

    public class OrderLines{
        public final String orderBeverageName;
        public final Double orderBeveragePrice;
        public final Integer orderBeverageQty;
        public final BigDecimal orderSubTotal;
        public final OrderItem orderItem;

        public OrderLines(String beverageName, Double beveragePrice, Integer beverageQty, BigDecimal subTotal, OrderItem orderItem) {
            this.orderBeverageName = beverageName;
            this.orderBeveragePrice = beveragePrice;
            this.orderBeverageQty = beverageQty;
            this.orderSubTotal = subTotal.setScale(2, RoundingMode.HALF_EVEN);
            this.orderItem = orderItem;
        }

        public String getOrderBeverageName() {
            return orderBeverageName;
        }

        public Double getOrderBeveragePrice() {
            return orderBeveragePrice;
        }

        public Integer getOrderBeverageQty() {
            return orderBeverageQty;
        }

        public BigDecimal getOrderSubTotal() {
            return orderSubTotal;
        }

        public OrderItem getOrderItem() {
            return orderItem;
        }
    }
}
