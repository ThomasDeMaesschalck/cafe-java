package be.hogent.cafe.model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import be.hogent.cafe.model.reporting.AllWaiterSales;
import be.hogent.cafe.model.reporting.MakePDFSalesReport;
import be.hogent.cafe.model.reporting.MakeTopWaitersChart;
import be.hogent.cafe.model.reporting.WaitersByRevenue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cafe {

    private static final Logger logger = LogManager.getLogger (Cafe.class.getName ());
    private final String cafeName;
    private Set<Waiter> waiters = new HashSet<>();
    private Waiter loggedInWaiter;
    private Table activeTable;
    private List<Table> tables = new ArrayList<>();
    private List<Beverage> beverages = new ArrayList<>();
    private int orderNumber = 0;
    private int IDCount = 0;
    private HashMap<Table, Order> unpaidOrders = new HashMap<>();
    private Set<Order> paidOrders = new HashSet<>();

    public Cafe(String cafeName, int numberOfTables) {
        logger.info("Cafe started");
        this.cafeName = cafeName;
        createTables(numberOfTables);
    }

    public boolean logIn(String name, String password)
    {
         for (Waiter waiter: getWaiterCollection())
        {
            if (waiter.toString().equals(name) && !waiter.getPassword().equals(password))
            {
                logger.error("Login failed, wrong password: " + "username: " + waiter.toString() + " password: " + password);
                throw new IllegalArgumentException("password incorrect");
            }
            if (waiter.toString().equals(name) && waiter.getPassword().equals(password))
            {
                logger.info(waiter.toString() + " logged in");
                setLoggedInWaiter(waiter);
                return true;
            }
        }
        logger.error("Login failed, username incorrect: " + name + " password: " + password);
        throw new IllegalArgumentException("username incorrect");
    }

    public boolean logOut() {
          logger.info(getLoggedInWaiter() + " logged out");
          setLoggedInWaiter(null);
          return true;
        }

    public void placeOrder(Beverage beverage, int quantity) //waiter order laten maken. Versie zonder datum, neemt huidige datum.
    {
        LocalDate date =  LocalDate.now();
        placeOrder(beverage, quantity, date);
    }

    public void placeOrder(Beverage beverage, int quantity, LocalDate date) //waiter een order laten maken
    {
        if (getActiveTable().getBelongsToWaiter() == null) //lege tafel koppelen aan de waiter
        {
            getActiveTable().setBelongsToWaiter(getLoggedInWaiter());
            logger.info("Assigned  " + getActiveTable().toString() + " to " + getLoggedInWaiter().toString());
        }

        if (!getActiveTable().getBelongsToWaiter().equals(getLoggedInWaiter()))
        {
            logger.error( getActiveTable().toString() + " does not belong to: " + getLoggedInWaiter().toString());
            throw new IllegalArgumentException("Table belongs to other waiter");
        }

        if (!getActiveTable().isActiveOrder())
        { //nieuw order aanmaken indien actieve tafel niet bezet is
            increaseOrderNumber();
            increaseIDCount();
            logger.debug("orderNumber count increased to: " + getOrderNumber());
            logger.debug("ID count increased to: " + getIDCount());
            Order order = new Order(getOrderNumber(), date, new OrderItem(getIDCount(), beverage, quantity), getLoggedInWaiter().getID(), getActiveTable().getTableID());
            getUnpaidOrders().put(getActiveTable(), order);
            getActiveTable().setActiveOrder(true);
            logger.info("New order made: " + order.toString());
        }
        else
            { //in een bestaand order een lijn toevoegen of updaten
                if (!getUnpaidOrders().get(getActiveTable()).orderLineExists(new OrderItem(beverage, quantity)))
                {
                increaseIDCount(); //increasen indien de lijn niet bestaat
                logger.debug("ID count increased to: " + getIDCount());
                }
            getUnpaidOrders().get(getActiveTable()).AddOrUpdateOrderLine(new OrderItem(getIDCount(), beverage, quantity));
            }
        }

    public void removeOrder(OrderItem orderItem) { //orderlijn verwijderen
        if (getActiveTable() == null)
        {
            logger.error("Remove of orderline failed, no active table");
            throw new IllegalArgumentException("No active table");
        }
        getUnpaidOrders().get(getActiveTable()).getOrderLines().remove(orderItem);
        logger.info("Removed orderline " + orderItem.toString());
    }

    public void clearTable()  //tafel leegmaken na betaling
    {
        logger.info("Cleared table " + getActiveTable().toString());
        getActiveTable().setBelongsToWaiter(null);
        getActiveTable().setActiveOrder(false);
        removeActiveTable();
    }

    public void pay() { //order betalen en verplaatsen naar paidorder collectie
        if (getActiveTable() == null)
        {
            logger.error("Pay failed, no active table");
            throw new IllegalArgumentException("No active table");
        }
        logger.info("Table " + getActiveTable().toString() + " paid orderNumber " + getUnpaidOrders().get(getActiveTable()).getOrderNumber());
        getPaidOrders().add(getUnpaidOrders().get(getActiveTable()));
        getUnpaidOrders().remove(getActiveTable());
        clearTable();
     }

    public double getTotalWaiterRevenue(){ //voor actieve waiter
        return getTotalWaiterRevenue( getLoggedInWaiter().getID());
    }

    public double getTotalWaiterRevenue(int waiterID){ //indien op basis van waiterID
        return getPaidOrders().stream().filter(order -> order.getWaiterID() == waiterID).mapToDouble(Order::getTotalOrderPrice).sum();
    }

    public HashMap<Waiter, Double> getTopWaitersByRevenue(int numberOfHowMany) //sales report van x aantal waiters
    {
        WaitersByRevenue waitersByRevenue = new WaitersByRevenue(getWaiterCollection(),numberOfHowMany,getPaidOrders());
        return waitersByRevenue.getTopWaitersMap();
    }

    public void topWaiterPieChart() throws Exception {
        new MakeTopWaitersChart(getTopWaitersByRevenue(3));
        logger.info(getLoggedInWaiter().toString() + " created top waiter pie chart");

    }

    public Map<Beverage, Integer> getAllWaiterSales(){ //alle omzet van waiter
        //null meegeven als datum om via method overloading alle sales te krijgen
        return getAllWaiterSales(null);
    }

    public Map<Beverage, Integer> getAllWaiterSales(LocalDate date){ //sales voor specifieke datum indien date niet null
        AllWaiterSales allWaiterSales = new AllWaiterSales(date, getPaidOrders(), getLoggedInWaiter());
        logger.info(getLoggedInWaiter().toString() + " retrieved his sales data");
        return  allWaiterSales.getSalesMap();
    }

    public void waiterSalesReportPDF() throws IOException {
        new MakePDFSalesReport(getAllWaiterSales(), getLoggedInWaiter().toString(), null);
        logger.info(getLoggedInWaiter().toString() + " created a waiter sales PDF report");
    }

    public void waiterSalesReportPDF(LocalDate date) throws IOException {
        new MakePDFSalesReport(getAllWaiterSales(date), getLoggedInWaiter().toString(), date);
        logger.info(getLoggedInWaiter().toString() + " created a waiter sales PDF report for " + date);
    }

    public void addWaiter(Waiter waiter){
        waiters.add(waiter);
    }

    public Set<Waiter> getWaiterCollection() {
        return waiters;
    }

    public Waiter getLoggedInWaiter() {
               return loggedInWaiter;
    }

    public String getNameOfLoggedInWaiter(){
        return getLoggedInWaiter().getFirstName() + " " + getLoggedInWaiter().getLastName();
    }

    public void createTables(int numberOfTables){
        for(int i = 0; i < numberOfTables; i++)
            tables.add(new Table(i));
    }

    public List<Table> getTables() {
        return tables;
    }

    public Table getActiveTable() {
        return activeTable;
    }

    public Table getTable(int tableID) {
        return tables.get(tableID);
    }

    public void setActiveTable(int tableID) {
        this.activeTable = tables.get(tableID);
        logger.info("Set " + activeTable + " as active table");
    }

    private void removeActiveTable() {
        this.activeTable = null;
    }

    public Set<Order> getPaidOrders() {
        return paidOrders;
    }

    public String getCafeName() {
        return cafeName;
    }

    public List<Beverage> getBeverages() {
        return beverages;
    }

    public HashMap<Table, Order> getUnpaidOrders() {
        return unpaidOrders;
    }

    private int getOrderNumber() {
        return orderNumber;
    }

    private void increaseOrderNumber(){
        orderNumber++;
    }

    private int getIDCount() {
        return IDCount;
    }

    private void increaseIDCount(){
        IDCount++;
    }

    public HashMap<Waiter, Double> getTopWaitersMap() {
        return getTopWaitersByRevenue(3);
    }

    private void setLoggedInWaiter(Waiter loggedInWaiter) {
        this.loggedInWaiter = loggedInWaiter;
    }
}