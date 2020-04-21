package be.hogent.cafe.model;

import be.hogent.cafe.model.dao.BeverageDAOImpl;
import be.hogent.cafe.model.dao.DAOException;
import be.hogent.cafe.model.dao.PaidOrderDAOImpl;
import be.hogent.cafe.model.dao.WaiterDAOImpl;
import be.hogent.cafe.model.reporting.AllWaiterSales;
import be.hogent.cafe.model.reporting.MakePDFSalesReport;
import be.hogent.cafe.model.reporting.MakeTopWaitersChart;
import be.hogent.cafe.model.reporting.WaitersByRevenue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

public class Cafe {

    private static final Logger logger = LogManager.getLogger (Cafe.class.getName ());
    private static Set<Beverage> beverages;
    private  String cafeName;
    private int numberOfTables;
    private int numberOfWaitersInPieChart;
    private static String reportsDirectory;
    private Set<Waiter> waiters = new HashSet<>();
    private Waiter loggedInWaiter;
    private Table activeTable;
    private List<Table> tables = new ArrayList<>();
    private HashMap<Table, Order> unpaidOrders = new HashMap<>();
    private Set<Order> paidOrders = new HashSet<>();
    private int highestOrderNumber;
    private int orderNumber;

    public Cafe()  {
        logger.info("Cafe started");
        readProperties();
        createTables(numberOfTables);
        this.setBeverages(BeverageDAOImpl.getInstance().getBeverages());
        this.setWaiters(WaiterDAOImpl.getInstance().getWaiters());
        this.setPaidOrders(PaidOrderDAOImpl.getInstance().getOrders(getBeverages()));
        this.setHighestOrderNumber(PaidOrderDAOImpl.getInstance().highestOrderNumber());
        orderNumber = highestOrderNumber;
    }

    private  void readProperties() {
        Properties cafeProperties = new Properties ();

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream ("cafe.properties")) {

            assert inputStream != null;
            cafeProperties.load (inputStream);
            cafeName = cafeProperties.getProperty("cafeName");
            numberOfTables = Integer.parseInt(cafeProperties.getProperty("numberOfTables"));
            numberOfWaitersInPieChart = Integer.parseInt(cafeProperties.getProperty("numberOfWaitersInPieChart"));
            reportsDirectory = cafeProperties.getProperty("reportsDirectory");

        } catch (IOException ioe) {
            logger.error("cafe properties not loaded");
            ioe.printStackTrace ();
        }
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
          removeActiveTable();
          return true;
        }

    public void placeOrder(Beverage beverage, int quantity) //waiter order laten maken. Versie zonder datum, neemt huidige datum.
    {
        LocalDate date =  LocalDate.now();
        placeOrder(beverage, quantity, date);
    }

    public void placeOrder(Beverage beverage, int quantity, LocalDate date) //waiter een order laten maken
    {

        if (!getActiveTable().getBelongsToWaiter().equals(getLoggedInWaiter()))
        {
            logger.error( getActiveTable().toString() + " does not belong to: " + getLoggedInWaiter().toString());
            throw new IllegalArgumentException("Table belongs to other waiter");
        }

        if (!getUnpaidOrders().containsKey(getActiveTable())) {
            Order order = new Order(getOrderNumber(), date, getLoggedInWaiter().getID(), getActiveTable().getTableID());
            getUnpaidOrders().put(getActiveTable(), order);
            logger.info("New order made: " + order.toString());
        }
            getUnpaidOrders().get(getActiveTable()).AddOrUpdateOrderLine(new OrderItem(beverage, quantity));
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
        removeActiveTable();
    }

    public void pay() throws DAOException { //order betalen en verplaatsen naar paidorder collectie
        if (getActiveTable() == null)
        {
            logger.error("Pay failed, no active table");
            throw new IllegalArgumentException("No active table");
        }
        logger.info("Table " + getActiveTable().toString() + " paid orderNumber " + getUnpaidOrders().get(getActiveTable()).getOrderNumber());
        PaidOrderDAOImpl.getInstance().insertOrder(getUnpaidOrders().get(getActiveTable()));
        getUnpaidOrders().remove(getActiveTable());
        clearTable();
        setPaidOrders(PaidOrderDAOImpl.getInstance().getOrders(getBeverages()));
    }

    public double getTotalWaiterRevenue(){ //voor actieve waiter
        return getTotalWaiterRevenue( getLoggedInWaiter().getID());
    }

    public double getTotalWaiterRevenue(int waiterID){ //indien op basis van waiterID
        return getPaidOrders().stream().filter(order -> order.getWaiterID() == waiterID).mapToDouble(Order::getTotalOrderPrice).sum();
    }

    public HashMap<Waiter, Double> getTopWaitersByRevenue(int numberOfHowMany) //sales report van x aantal waiters
    {
        return WaitersByRevenue.calculate(getWaiterCollection(), numberOfHowMany, getPaidOrders());
    }

    public boolean topWaiterPieChart() throws Exception {
        logger.info(getLoggedInWaiter().toString() + " created top waiter pie chart");
        return  MakeTopWaitersChart.createJPG(getTopWaitersByRevenue(3));
    }

    public Map<Beverage, Integer> getAllWaiterSales(){ //alle omzet van waiter
        //null meegeven als datum om via method overloading alle sales te krijgen
        return getAllWaiterSales(null);
    }

    public Map<Beverage, Integer> getAllWaiterSales(LocalDate specificDate){ //sales voor specifieke datum indien date niet null
        logger.info(getLoggedInWaiter().toString() + " retrieved his sales data");
        return  AllWaiterSales.calculate(specificDate, PaidOrderDAOImpl.getInstance().getOrders(getBeverages()), getLoggedInWaiter());
    }

    public boolean waiterSalesReportPDF() throws IOException {
        logger.info(getLoggedInWaiter().toString() + " created a waiter sales PDF report");
        return MakePDFSalesReport.createPDF(getAllWaiterSales(), getLoggedInWaiter().toString(), null);
    }

    public boolean waiterSalesReportPDF(LocalDate date) throws IOException {
        logger.info(getLoggedInWaiter().toString() + " created a waiter sales PDF report for " + date);
    return   MakePDFSalesReport.createPDF(getAllWaiterSales(date), getLoggedInWaiter().toString(), date);
    }

    public void addWaiter(Waiter waiter){
        waiters.add(waiter);
    }

    public Set<Waiter> getWaiterCollection() {
        return waiters;
    }

    public void setWaiters(Set<Waiter> waiter)
    {
        this.waiters = waiter;
    }

    public Waiter getLoggedInWaiter() {
               return loggedInWaiter;
    }

    public String getNameOfLoggedInWaiter(){
        return getLoggedInWaiter().getFirstName() + " " + getLoggedInWaiter().getLastName();
    }

    public void createTables(int numberOfTables){
        for(int i = 0; i < numberOfTables; i++) {
            tables.add(new Table(i +1)); //start table number at 1 instead of 0
        }
    }

    public List<Table> getTables() {
        return tables;
    }

    public Table getActiveTable() {
        return activeTable;
    }

    public Table getTable(int tableID) {
        return tables.get(tableID - 1); //difference between index position in array and table ID
    }

    public void setActiveTable(int tableID) {
        this.activeTable = getTable(tableID);
        logger.info("Set " + activeTable + " as active table");
        if (getActiveTable().getBelongsToWaiter() == null) //lege tafel koppelen aan de waiter
        {
            getActiveTable().setBelongsToWaiter(getLoggedInWaiter());
            logger.info("Assigned  " + getActiveTable().toString() + " to " + getLoggedInWaiter().toString());
            increaseOrderNumber();
            logger.debug("orderNumber count increased to: " + getOrderNumber());
        }
    }

    private void removeActiveTable() {
        this.activeTable = null;
    }

    public Set<Order> getPaidOrders() {
        return paidOrders;
    }

    public int getHighestOrderNumber() {
        return highestOrderNumber;
    }

    public String getCafeName() {
        return cafeName;
    }

    public Set<Beverage> getBeverages() {
        return beverages;
    }

    public void setBeverages(Set<Beverage> beverage)
    {
        beverages = beverage;
    }

    public void setHighestOrderNumber(int number) {highestOrderNumber = number;}

    public void setPaidOrders(Set<Order> paidOrder)
    {
        paidOrders = paidOrder;
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

    public HashMap<Waiter, Double> getTopWaitersMap() {
        return getTopWaitersByRevenue(numberOfWaitersInPieChart);
    }

    private void setLoggedInWaiter(Waiter loggedInWaiter) {
        this.loggedInWaiter = loggedInWaiter;
    }

    public static String getReportsDirectory()
    {
     return reportsDirectory;
    }
}