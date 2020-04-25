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

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Cafe implements Serializable {

    private static final long serialVersionUID = -7985844604654656130L;
    private static final Logger logger = LogManager.getLogger(Cafe.class.getName());
    private static Set<Beverage> beverages;
    private String cafeName;
    private int numberOfTables;
    private int numberOfWaitersInPieChart;
    private static String reportsDirectory;
    private Set<Waiter> waiters = new HashSet<>();
    private Waiter loggedInWaiter;
    private Table activeTable;
    private List<Table> tables = new ArrayList<>();
    private HashMap<Table, Order> unpaidOrders = new HashMap<>();
    //private Set<Order> paidOrders = new HashSet<>();
    private int highestOrderNumber;
    private int orderNumber;

    public Cafe() {
        logger.info("Cafe started");
        readProperties();
        createTables(numberOfTables);
        this.setBeverages(BeverageDAOImpl.getInstance().getBeverages());
        this.setWaiters(WaiterDAOImpl.getInstance().getWaiters());
        //this.setPaidOrders(PaidOrderDAOImpl.getInstance().getOrders());
        this.setHighestOrderNumber(PaidOrderDAOImpl.getInstance().highestOrderNumber());
        orderNumber = highestOrderNumber;
    }

    private void readProperties() {
        Properties cafeProperties = new Properties();

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("cafe.properties")) {

            assert inputStream != null;
            cafeProperties.load(inputStream);
            cafeName = cafeProperties.getProperty("cafeName");
            numberOfTables = Integer.parseInt(cafeProperties.getProperty("numberOfTables"));
            numberOfWaitersInPieChart = Integer.parseInt(cafeProperties.getProperty("numberOfWaitersInPieChart"));
            reportsDirectory = cafeProperties.getProperty("reportsDirectory");

        } catch (IOException ioe) {
            logger.error("cafe properties not loaded");
            ioe.printStackTrace();
        }
    }

    public void serializeCafe() {
        try {
            FileOutputStream fs = new FileOutputStream("tables.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(tables);
            os.close();

            FileOutputStream fs2 = new FileOutputStream("unpaidorders.ser");
            ObjectOutputStream os2 = new ObjectOutputStream(fs2);
            os2.writeObject(unpaidOrders);
            os2.close();
            logger.info("Serialized cafe");

        } catch (
                Exception e) {
            logger.error("Error, failed to serialize the cafe");
            e.printStackTrace();
        }
    }

    public void deSerializeCafe() {
        try {
            FileInputStream fis = new FileInputStream("tables.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            tables = (List<Table>) ois.readObject();
            ois.close();

            FileInputStream fis2 = new FileInputStream("unpaidorders.ser");
            ObjectInputStream ois2 = new ObjectInputStream(fis2);
            unpaidOrders = (HashMap<Table, Order>) ois2.readObject();
            ois2.close();

            logger.info("Deserialized cafe");
        } catch (
                Exception e) {
            logger.error("Error, failed to deserialize the cafe");
            e.printStackTrace();
        }
    }

    public boolean logIn(String name, String password) {
        for (Waiter waiter : getWaiterCollection()) {
            if (waiter.toString().equals(name) && !waiter.getPassword().equals(password)) {
                logger.error("Login failed, wrong password: " + "username: " + waiter.toString() + " password: " + password);
                throw new IllegalArgumentException("password incorrect");
            }
            if (waiter.toString().equals(name) && waiter.getPassword().equals(password)) {
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
        LocalDate date = LocalDate.now();
        placeOrder(beverage, quantity, date);
    }

    public void placeOrder(Beverage beverage, int quantity, LocalDate date) //waiter een order laten maken
    {

        //if (!getActiveTable().getBelongsToWaiter().equals(getLoggedInWaiter())) //taken care of in front-end
        //{
        //   logger.error( getActiveTable().toString() + " does not belong to: " + getLoggedInWaiter().toString());
        //   throw new IllegalArgumentException("Table belongs to other waiter");
        //}

        if (!getUnpaidOrders().containsKey(getActiveTable())) {
            increaseOrderNumber();
            logger.debug("orderNumber count increased to: " + getOrderNumber());
            Order order = new Order(getOrderNumber(), date, getLoggedInWaiter().getID(), getActiveTable().getTableID());
            getUnpaidOrders().put(getActiveTable(), order);
            logger.info("New order made: " + order.toString());
        }
        getUnpaidOrders().get(getActiveTable()).AddOrUpdateOrderLine(new OrderItem(beverage, quantity));
    }

    public void removeOrder(OrderItem orderItem) { //orderlijn verwijderen
        getUnpaidOrders().get(getActiveTable()).getOrderLines().remove(orderItem);
        logger.info("Removed orderline " + orderItem.toString());
    }

    public void clearTable()  //tafel leegmaken na betaling
    {
        logger.info("Cleared table " + getActiveTable().toString());
        getActiveTable().setBelongsToWaiter(null);
        removeActiveTable();
    }

    public void pay() { //order betalen en verplaatsen naar paidorder collectie
        logger.info("Table " + getActiveTable().toString() + " paid orderNumber " + getUnpaidOrders().get(getActiveTable()).getOrderNumber());
        try {
            PaidOrderDAOImpl.getInstance().insertOrder(getUnpaidOrders().get(getActiveTable()));
        } catch (DAOException e) {
            e.printStackTrace();
        }
        getUnpaidOrders().remove(getActiveTable());
        clearTable();
        //setPaidOrders(PaidOrderDAOImpl.getInstance().getOrders());
    }

    public double getTotalWaiterRevenue() { //voor actieve waiter
        return getTotalWaiterRevenue(getLoggedInWaiter().getID());
    }

    public double getTotalWaiterRevenue(int waiterID) { //indien op basis van waiterID
        return getPaidOrders().stream().filter(order -> order.getWaiterID() == waiterID).mapToDouble(Order::getTotalOrderPrice).sum();
    }

    public HashMap<Waiter, Double> getTopWaitersByRevenue(int numberOfHowMany) //sales report van x aantal waiters
    {
        return WaitersByRevenue.calculate(getWaiterCollection(), numberOfHowMany, getPaidOrders());
    }

    public boolean topWaiterPieChart() throws Exception {
        logger.info(getLoggedInWaiter().toString() + " created top waiter pie chart");
        return MakeTopWaitersChart.getInstance().createJPG(getTopWaitersByRevenue(numberOfWaitersInPieChart));
    }

    public Map<Beverage, Integer> getAllWaiterSales() { //alle omzet van waiter
        //null meegeven als datum om via method overloading alle sales te krijgen
        return getAllWaiterSales(null);
    }

    public Map<Beverage, Integer> getAllWaiterSales(LocalDate specificDate) { //sales voor specifieke datum indien date niet null
        logger.info(getLoggedInWaiter().toString() + " retrieved sales data");
        return AllWaiterSales.calculate(specificDate, PaidOrderDAOImpl.getInstance().getOrders(), getLoggedInWaiter());
    }

    public boolean waiterSalesReportPDF(LocalDate date) throws IOException {
        if (date == null) {
            logger.info(getLoggedInWaiter().toString() + " created a total waiter sales PDF report");
        } else {
            logger.info(getLoggedInWaiter().toString() + " created a waiter sales PDF report for " + date);
        }
        return MakePDFSalesReport.getInstance().createPDF(getAllWaiterSales(date), getLoggedInWaiter().toString(), date);
    }

    public void addWaiter(Waiter waiter) {
        waiters.add(waiter);
    }

    public Set<Waiter> getWaiterCollection() {
        return waiters;
    }

    public void setWaiters(Set<Waiter> waiter) {
        this.waiters = waiter;
    }

    public Waiter getLoggedInWaiter() {
        return loggedInWaiter;
    }

    public String getNameOfLoggedInWaiter() {
        return getLoggedInWaiter().getFirstName() + " " + getLoggedInWaiter().getLastName();
    }

    public void createTables(int numberOfTables) {
        File serializedTables = new File("tables.ser");
        File serializedOrders = new File("unpaidorders.ser");
        if (serializedTables.exists() && serializedOrders.exists())
        {
            deSerializeCafe();
        }
        else {
            for (int i = 0; i < numberOfTables; i++) {
                tables.add(new Table(i + 1)); //start table number at 1 instead of 0
            }
        }
    }

    public List<Table> getTables() {
        return tables;
    }

    public Table getActiveTable() {
        return activeTable;
    }

    public Table getTable(int tableID) {
        return tables.get(tableID - 1); //difference between index position in array and table ID in GUI
    }

    public void setActiveTable(int tableID) {
        this.activeTable = getTable(tableID);
        logger.info("Set " + activeTable + " as active table");
        if (getActiveTable().getBelongsToWaiter() == null) //lege tafel koppelen aan de waiter
        {
            getActiveTable().setBelongsToWaiter(getLoggedInWaiter());
            logger.info("Assigned  " + getActiveTable().toString() + " to " + getLoggedInWaiter().toString());
        }
    }

    private void removeActiveTable() {
        this.activeTable = null;
    }

    public Set<Order> getPaidOrders() {
        return PaidOrderDAOImpl.getInstance().getOrders();
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

    public void setBeverages(Set<Beverage> beverage) {
        beverages = beverage;
    }

    public static Beverage getBeverageByID(int beverageID) {
        return beverages.stream().filter(b -> b.getBeverageID() == beverageID).findFirst().orElse(null);
    }

    public void setHighestOrderNumber(int number) {
        highestOrderNumber = number;
    }

    //public void setPaidOrders(Set<Order> paidOrder) {
    //  paidOrders = paidOrder;
    //}

    public HashMap<Table, Order> getUnpaidOrders() {
        return unpaidOrders;
    }

    private int getOrderNumber() {
        return orderNumber;
    }

    private void increaseOrderNumber() {
        orderNumber++;
    }

    public HashMap<Waiter, Double> getTopWaitersMap() {
        return getTopWaitersByRevenue(numberOfWaitersInPieChart);
    }

    private void setLoggedInWaiter(Waiter loggedInWaiter) {
        this.loggedInWaiter = loggedInWaiter;
    }

    public static String getReportsDirectory() {
        return reportsDirectory;
    }

    public int getNumberOfTables() {
        return numberOfTables;
    }
}