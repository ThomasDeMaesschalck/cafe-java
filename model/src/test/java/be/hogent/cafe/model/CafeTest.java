package be.hogent.cafe.model;

import be.hogent.cafe.model.dao.DAOException;
import be.hogent.cafe.model.dao.PaidOrderDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import static java.time.LocalDate.of;
import static java.time.Month.FEBRUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class CafeTest {

    private Cafe cafe;
    private Waiter wout;
    private Waiter patrick;
    private Waiter ilse;
    private Waiter thomas;
    private Waiter tina;
    private Waiter tom;
    private Beverage cola;
    private Beverage leffe;
    private Beverage duvel;
    private Beverage fanta;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe();

        wout = new Waiter(1, "Peters", "Wout", "password");
        ilse = new Waiter(3, "Vandenbroeck", "Ilse", "password");
        patrick = new Waiter(4, "Desmet", "Patrick", "password");
        thomas = new Waiter(5, "DM", "Thomas", "password");
        tina = new Waiter(7, "DM", "Tina", "password");
        tom = new Waiter(8, "DM", "Tom", "password");

        cola = new Beverage(1, "Cola", 2.40);
        leffe = new Beverage(2, "Leffe", 3.00);
        duvel = new Beverage(3, "Duvel", 2.40);
        fanta = new Beverage(4, "Fanta", 3.00);
        cafe.getBeverages().add(cola);
        cafe.getBeverages().add(leffe);


    }

    @Test
    public void testCafe() {
        assertEquals("Cafe Thomas", cafe.getCafeName(), "Test cafe() 01 failed");
    }

    @Test
    public void testAddWaiter() {
        cafe.addWaiter(thomas);
        assertTrue(cafe.getWaiterCollection().contains(thomas), "Test addWaiter() 012 failed");
    }

    @Test
    public void testLogIn() {
        cafe.addWaiter(wout);
        cafe.addWaiter(patrick);
        cafe.addWaiter(ilse);
        assertTrue(cafe.logIn("Wout Peters", "password"), "logIn1 failed");
        cafe.logOut();
        try {
            cafe.logIn("Wouter Peters", "password");
        } catch (IllegalArgumentException expected) {
            assertEquals("username incorrect", expected.getMessage(), "logIn2 passed when it should have failed");
        }
        try {
            cafe.logIn("Wouters Peter", "password");
        } catch (IllegalArgumentException expected) {
            assertEquals("username incorrect", expected.getMessage(), "logIn3 passed when it should have failed");
        }
        try {
            cafe.logIn("Wout Peeters", "password");
        } catch (IllegalArgumentException expected) {
            assertEquals("username incorrect", expected.getMessage(), "logIn4 passed when it should have failed");
        }
        try {
            cafe.logIn("Wout Peters", "passwoord");
        } catch (IllegalArgumentException expected) {
            assertEquals("password incorrect", expected.getMessage(), "logIn5 passed when it should have failed");
        }
        assertTrue(cafe.logIn("Patrick Desmet", "password"), "logIn6 failed");
        cafe.logOut();
        try {
            cafe.logIn("Patrick Desmeet", "password");
        } catch (IllegalArgumentException expected) {
            assertEquals("username incorrect", expected.getMessage(), "logIn7 passed when it should have failed");
        }
    }


    @Test
    public void testGetLoggedInWaiter() {
        cafe.addWaiter(wout);
        cafe.addWaiter(patrick);
        cafe.logIn("Wout Peters", "password");
        assertEquals(wout, cafe.getLoggedInWaiter(), "Test getLoggedInWaiter() 01 failed");
        cafe.logOut();
        cafe.logIn("Patrick Desmet", "password");
        assertEquals(patrick, cafe.getLoggedInWaiter(), "Test getLoggedInWaiter() 02 failed");
    }

    @Test
    public void testGetNameOfLoggedInWaiter() {
        cafe.addWaiter(wout);
        cafe.addWaiter(patrick);
        cafe.logIn("Wout Peters", "password");
        assertEquals("Wout Peters", cafe.getNameOfLoggedInWaiter(), "Test GetNameOfLoggedInWaiter() 01 failed");
        cafe.logOut();
        cafe.logIn("Patrick Desmet", "password");
        assertEquals("Patrick Desmet", cafe.getNameOfLoggedInWaiter(), "Test GetNameOfLoggedInWaiter() 02 failed");

    }

    @Test
    public void testLogOut() {
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters", "password");
        assertTrue(cafe.logOut(), "Test logOut() 01 failed");
    }

    @Test
    public void testCreateTables() {
        assertEquals(cafe.getNumberOfTables(), cafe.getTables().size(), "Test CreateTables() 01 failed"); //tables generated in constructor
    }

    @Test
    public void testSetActiveTable() {
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        assertEquals(cafe.getTables().get(0), cafe.getActiveTable(), "Test SetActiveTable() 01 failed"); //difference between index in array and tableID
        cafe.setActiveTable(9);
        assertEquals(cafe.getTables().get(8), cafe.getActiveTable(), "Test SetActiveTable() 02 failed"); //difference between index in array and tableID
    }

    @Test
    public void testGetBeverageByID() {
        Beverage beverage = Cafe.getBeverageByID(1);
        assertEquals("Cola", beverage.getBeverageName(), "Test getBeverageById() 01 failed");
    }

    @Test
    public void testPlaceOrder() {
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 5); //ID1, orderNumber 1
        assertEquals(1, cafe.getUnpaidOrders().size(), "Test PlaceOrder() 01 failed");
        int thisOrderNumber = cafe.getHighestOrderNumber() + 1; //nieuwe order nummer verkrijgen, daarna voor volgende orders terug eentje bijtellen voor de test
        assertEquals(thisOrderNumber, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderNumber(), "Test PlaceOrder() 02 failed - problem with orderNumber count");

        cafe.placeOrder(leffe, 2); //ID2, orderNumber 1
        assertEquals(2, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderLines().size(), "Test PlaceOrder() 03 failed");
        assertEquals(thisOrderNumber, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderNumber(), "Test PlaceOrder() 04 failed - problem with orderNumber count");
        OrderItem leffeTest = new OrderItem(leffe, 2);
        assertTrue(cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderLines().contains(leffeTest), "Test PlaceOrder() 05 failed");

        cafe.setActiveTable(2);
        cafe.placeOrder(leffe, 2); //ID3, orderNumber 2
        assertEquals(2, cafe.getUnpaidOrders().size(), "Test PlaceOrder() 06 failed");
        thisOrderNumber += 1;
        assertEquals(thisOrderNumber, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderNumber(), "Test PlaceOrder() 07 failed - problem with orderNumber count");

        cafe.placeOrder(leffe, 2); //blijft ID3 orderNumber 2
        cafe.placeOrder(leffe, 5); //blijft ID3 orderNumber 2

        assertEquals(1, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderLines().size(), "Test PlaceOrder() 08 failed - order not added to existing orderline");
        assertEquals(thisOrderNumber, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderNumber(), "Test PlaceOrder() 09 failed - problem with orderNumber count");


        cafe.logOut();

        cafe.addWaiter(patrick);
        cafe.logIn("Patrick Desmet", "password");
        //cafe.setActiveTable(2);
        //try {
        //    cafe.placeOrder(leffe, 2);
        //}
        //catch (IllegalArgumentException expected){
        //    assertEquals("Table belongs to other waiter", expected.getMessage(), "Test PlaceOrder() 10 failed");
        //}
        cafe.setActiveTable(3);
        cafe.placeOrder(leffe, 3); //ID4
        assertEquals(1, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderLines().size(), "Test PlaceOrder() 11 failed");
        thisOrderNumber += 1;
        assertEquals(thisOrderNumber, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderNumber(), "Test PlaceOrder() 12 failed - problem with orderNumber count");

        cafe.placeOrder(cola, 2); //ID5
        assertEquals(2, cafe.getUnpaidOrders().get((cafe.getActiveTable())).getOrderLines().size(), "Test PlaceOrder() 13 failed");
        assertEquals(thisOrderNumber, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderNumber(), "Test PlaceOrder() 14 failed - problem with orderNumber count");

        cafe.logOut();

        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(leffe, 2); //ID2
        assertEquals(2, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderLines().size(), "Test PlaceOrder() 15 failed");
        thisOrderNumber -= 2;

        assertEquals(thisOrderNumber, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderNumber(), "Test PlaceOrder() 16 failed - problem with orderNumber count");
    }


    @Test
    public void testGetTotalOrderLinePrice() {
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 10);
        cafe.placeOrder(cola, 10);
        cafe.placeOrder(leffe, 10);
        assertEquals(78.0, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getTotalOrderPrice(), "Test getTotalOrderLinePrice() 01 failed");
    }

    @Test
    public void testRemoveOrder() {
        OrderItem orderItem = new OrderItem(cola, 2);
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 2);
        cafe.placeOrder(leffe, 2);
        cafe.placeOrder(duvel, 2);
        cafe.placeOrder(fanta, 2);
        cafe.removeOrder(orderItem);
        assertEquals(3, cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderLines().size(), "Test removeOrder 02 failed");
    }


    @Test
    public void testClearTable() throws DAOException {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber(); //hoogste huidige orderNumber ophalen, daarna in eindcode eentje bijtellen
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 2);
        cafe.placeOrder(leffe, 2);
        cafe.placeOrder(duvel, 2);
        cafe.placeOrder(fanta, 2);
        int paidOrderOriginalSize = cafe.getPaidOrders().size();
        cafe.pay();
        assertEquals(0, cafe.getUnpaidOrders().size(), "Test clearTable 01 failed");
        cafe.logOut();
        cafe.addWaiter(patrick);
        cafe.logIn("Patrick Desmet", "password");
        cafe.setActiveTable(10);
        cafe.placeOrder(cola, 20);
        cafe.placeOrder(leffe, 30);
        cafe.placeOrder(duvel, 40);
        cafe.logOut();
        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 2);
        cafe.placeOrder(leffe, 2);
        cafe.logOut();
        cafe.logIn("Patrick Desmet", "password");
        cafe.setActiveTable(10);
        cafe.pay();
        assertEquals(1, cafe.getUnpaidOrders().size(), "Test clearTable 02 failed");
        assertEquals(paidOrderOriginalSize + 2, cafe.getPaidOrders().size(), "Test clearTable 03 failed - interfered with other table");
        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }


    @Test
    public void testPay() throws DAOException {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 5);
        cafe.placeOrder(leffe, 2);
        cafe.placeOrder(duvel, 10);
        int paidOrderOriginalSize = cafe.getPaidOrders().size();
        cafe.pay();
        assertEquals(0, cafe.getUnpaidOrders().size(), "Test pay() 01 failed - table not cleared");
        assertEquals(paidOrderOriginalSize + 1, cafe.getPaidOrders().size(), "Test pay() 02 failed - paidOrders collection not OK");
        cafe.logOut();
        cafe.addWaiter(patrick);
        cafe.logIn("Patrick Desmet", "password");
        cafe.setActiveTable(10);
        cafe.placeOrder(cola, 1);
        cafe.placeOrder(leffe, 3);
        cafe.placeOrder(duvel, 5);
        cafe.placeOrder(fanta, 4);
        cafe.pay();
        assertEquals(0, cafe.getUnpaidOrders().size(), "Test pay() 03 failed - table not cleared");
        assertEquals(paidOrderOriginalSize + 2, cafe.getPaidOrders().size(), "Test pay() 04 failed - paidOrders collection not OK");
        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }

    @Test
    public void testGetTotalWaiterRevenue() throws DAOException {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        cafe.addWaiter(thomas);
        cafe.logIn("Thomas DM", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 10); //24
        cafe.placeOrder(cola, 10); //24 - test om te zien of qty updaten werkt
        cafe.placeOrder(leffe, 10); //30
        cafe.pay();
        cafe.setActiveTable(2);
        cafe.placeOrder(leffe, 30); //90
        cafe.placeOrder(cola, 20); //48
        cafe.pay();
        assertEquals(216, cafe.getTotalWaiterRevenue(), "Test getTotalWaiterRevenue() 01 failed");
        assertEquals(216, cafe.getTotalWaiterRevenue(5), "Test getTotalWaiterRevenue() 02 failed");
        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }

    @Test
    public void testGetTopThreeWaitersByRevenue() throws DAOException {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        cafe.addWaiter(thomas);
        cafe.logIn("Thomas DM", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 10000); //24000
        cafe.placeOrder(leffe, 10000); //30000
        cafe.pay();
        cafe.setActiveTable(2);
        cafe.placeOrder(leffe, 30000); //90000
        cafe.placeOrder(cola, 20000); //48000  == 192000 totaal
        cafe.pay();
        cafe.logOut();
        cafe.addWaiter(tina);
        cafe.logIn("Tina DM", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 10000); //24000
        cafe.placeOrder(leffe, 10000); //30000
        cafe.pay();
        cafe.setActiveTable(2);
        cafe.placeOrder(leffe, 100000); //300000
        cafe.placeOrder(cola, 100000); //240000 == 594000 totaal
        cafe.pay();
        cafe.logOut();
        cafe.addWaiter(tom);
        cafe.logIn("Tom DM", "password");
        cafe.setActiveTable(5);
        cafe.placeOrder(cola, 100000); //240000
        cafe.placeOrder(leffe, 10000); //30000  == 270000 totaal
        cafe.pay();
        cafe.getTopWaitersByRevenue(3);
        assertEquals(594000, cafe.getTopWaitersMap().get(tina).doubleValue(), "Test getTopThreeWaitersByRevenue() 03 failed");
        assertEquals(270000, cafe.getTopWaitersMap().get(tom).doubleValue(), "Test getTopThreeWaitersByRevenue() 01 failed");
        assertEquals(192000, cafe.getTopWaitersMap().get(thomas).doubleValue(), "Test getTopThreeWaitersByRevenue() 02 failed");
        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }

    @Test
    public void testgetAllWaiterSales() throws DAOException {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        cafe.addWaiter(thomas);
        cafe.logIn("Thomas DM", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 10);
        cafe.placeOrder(leffe, 10);
        cafe.pay();
        cafe.setActiveTable(2);
        cafe.placeOrder(leffe, 30);
        cafe.placeOrder(cola, 20);
        cafe.pay();
        cafe.logOut();
        cafe.addWaiter(tom);
        cafe.logIn("Tom DM", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 10);
        cafe.placeOrder(leffe, 10);
        cafe.pay();
        cafe.logOut();
        cafe.logIn("Thomas DM", "password");
        assertEquals(2, cafe.getAllWaiterSales().size(), "Test getAllWaiterSales() 01 failed");
        cafe.logOut();
        cafe.logIn("Tom DM", "password");
        assertEquals(2, cafe.getAllWaiterSales().size(), "Test getAllWaiterSales() 02 failed");
        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }


    @Test
    public void testgetAllWaiterSalesByDate() throws DAOException {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        cafe.addWaiter(thomas);
        cafe.logIn("Thomas DM", "password");
        cafe.setActiveTable(1);
        LocalDate date = LocalDate.of(2020, Month.FEBRUARY, 5);
        cafe.placeOrder(cola, 10, date);
        cafe.pay();
        cafe.setActiveTable(2);
        cafe.placeOrder(leffe, 30, date);
        cafe.placeOrder(cola, 20, date);
        cafe.pay();
        cafe.logOut();
        cafe.addWaiter(tom);
        cafe.logIn("Tom DM", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 10);
        cafe.placeOrder(leffe, 10);
        cafe.pay();
        cafe.logOut();
        cafe.logIn("Thomas DM", "password");
        assertEquals(2, cafe.getAllWaiterSales(date).size(), "Test getAllWaiterSalesByDate() 01 failed");
        cafe.logOut();
        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }

    @Test
    public void testWaiterSalesReportPDF() throws DAOException, IOException {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        LocalDate date = of(2020, FEBRUARY, 5);
        cafe.logIn("Thomas DM", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 1000, date);
        cafe.pay();
        cafe.setActiveTable(2);
        cafe.placeOrder(leffe, 3000, date);
        cafe.placeOrder(cola, 2000, date);
        cafe.pay();
        assertThat(cafe.waiterSalesReportPDF(null)).isTrue();

        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }

    @Test
    public void testWaiterSalesReportPDFSpecificDate() throws IOException, DAOException {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        LocalDate firstDate = LocalDate.of(2020, FEBRUARY, 5);
        cafe.logIn("Thomas DM", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 666, firstDate);
        cafe.pay();
        cafe.setActiveTable(2);
        cafe.placeOrder(leffe, 222, firstDate);
        cafe.placeOrder(cola, 111, firstDate);
        cafe.pay();
        cafe.logOut();

        LocalDate secondDate = LocalDate.of(2020, FEBRUARY, 10);
        cafe.logIn("Thomas DM", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 100000, secondDate);
        cafe.pay();
        cafe.setActiveTable(2);
        cafe.placeOrder(leffe, 100000, secondDate);
        cafe.placeOrder(cola, 100000, secondDate);
        cafe.pay();

        assertThat(cafe.waiterSalesReportPDF(firstDate)).isTrue();
        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }

    @Test
    public void testGetTopThreeWaitersJPEG() throws Exception {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        cafe.getTopWaitersByRevenue(3);
        assertThat(cafe.topWaiterPieChart()).isTrue();
        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }

    @Test
    public void testSerializationCafe() throws IOException {
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 5);
        cafe.serializeCafe();
        File tables = new File("tables.ser");
        File unpaidOrders = new File("unpaidorders.ser");
        assertTrue(tables.exists(), "Test serializationCafe() 01 failed");
        assertTrue(unpaidOrders.exists(), "Test serializationCafe() 02 failed");
        if (!tables.delete() | !unpaidOrders.delete()) { //files terug verwijderen, anders werken bovenstaande testen niet correct
            throw new IOException("failed to delete ser files");
        }
    }


    @Test
    public void testDeSerializationCafe() throws IOException {
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters", "password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 5);
        cafe.serializeCafe();
        cafe.getUnpaidOrders().clear();
        cafe.getTables().clear();
        cafe.deSerializeCafe();
        assertFalse(cafe.getTables().isEmpty(), "Test deSerializationCafe() 01 failed");
        assertFalse(cafe.getUnpaidOrders().isEmpty(), "Test deSerializationCafe() 02 failed");
        File tables = new File("tables.ser");
        File unpaidOrders = new File("unpaidorders.ser");
        if (!tables.delete() | !unpaidOrders.delete()) { //files terug verwijderen, anders werken bovenstaande testen niet correct
            throw new IOException("failed to delete ser files");
        }
    }

}