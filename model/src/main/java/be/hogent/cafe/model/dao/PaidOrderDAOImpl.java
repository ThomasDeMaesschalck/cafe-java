package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaidOrderDAOImpl extends BaseDAO implements PaidOrderDAO {
    private static final String GET_ALL_ORDERS = "SELECT * from orders";
    private static final String INSERT_ORDER = "INSERT into orders (orderNumber, beverageID, qty, date, waiterID) VALUES (?, ?,?,?,?)";
    private static final String MAX_ORDER_NUMBER = "SELECT MAX(orderNumber) from orders";
    private static final String DELETE_FROM_ORDERS = "DELETE from orders WHERE orderNumber >= ?";
    private static final String GET_ALL_DATES_SQL = "SELECT date from orders where waiterID = ?";


    private final Logger logger = LogManager.getLogger(PaidOrderDAOImpl.class.getName());

    private static PaidOrderDAOImpl instance;

    private PaidOrderDAOImpl() {
    }

    public synchronized static PaidOrderDAO getInstance() {
        if (instance == null) {
            instance = new PaidOrderDAOImpl();
        }
        return instance;
    }


    @Override
    public Set<Order> getOrders() {
        Set<Order> paidOrders = new HashSet<>();

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_ORDERS);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            while (resultSet.next()) {

                Order paidOrder = new Order();
                paidOrder.setOrderNumber(resultSet.getInt("orderNumber"));
                paidOrder.setWaiterID(resultSet.getInt("waiterID"));


                paidOrder.setDate(resultSet.getDate("date").toLocalDate());

                int beverageIDFromDB = resultSet.getInt("beverageID");

                Beverage beverage = Cafe.getBeverageByID(beverageIDFromDB);

                OrderItem orderItem = new OrderItem(beverage, resultSet.getInt("qty"));
                if (paidOrders.contains(paidOrder)) //als er een order in collectie zit met zelfde orderNumber => geen nieuw order aanmaken maar orderiTem toevoegen
                {
                    for (Order paidOrderFromSet : paidOrders) {
                        if (paidOrderFromSet.getOrderNumber() == paidOrder.getOrderNumber()) {
                            paidOrderFromSet.getOrderLines().add(orderItem);
                        }
                    }
                } else {
                    paidOrder.setOrderItems(orderItem);
                    paidOrders.add(paidOrder);
                }
            }
            logger.info("paidOrders are loaded from database");

        } catch (Exception e) {
            logger.error("Error getting paidOrders from database. " + e.getMessage());
        }
        return paidOrders;
    }

    @Override
    public boolean insertOrder(Order o) throws DAOException {

        for (OrderItem oi : o.getOrderLines()) {

            try (Connection connection = getConnection();

                 PreparedStatement pStatement = connection.prepareStatement(INSERT_ORDER)) {
                pStatement.setInt(1, o.getOrderNumber());
                pStatement.setInt(2, oi.getBeverage().getBeverageID());
                pStatement.setInt(3, oi.getQty());
                pStatement.setDate(4, Date.valueOf(o.getDate()));
                pStatement.setInt(5, o.getWaiterID());
                pStatement.executeUpdate();
                logger.info("Inserted orderLine from orderNumber " + o.getOrderNumber() + " into database");

            } catch (SQLException e) {
                logger.error("Error insert order in DB. " + e.getMessage());
                throw new DAOException("Failed to insert order in DB " + e.getMessage());
            }

        }
        return true;
    }

    @Override
    public boolean deleteOrders(int orderNumbersToDelete) throws DAOException { //gebruikt om orders van de testen terug uit de DB te halen
        try (Connection connection = getConnection();

             PreparedStatement pStatement = connection.prepareStatement(DELETE_FROM_ORDERS)) {
            pStatement.setInt(1, orderNumbersToDelete);
            pStatement.executeUpdate();
            logger.info("Deleted orderNumber(s) equal or higher than " + orderNumbersToDelete + " from database");

        } catch (SQLException e) {
            logger.error("Error deleting orders in DB. " + e.getMessage());
            throw new DAOException("Failed to delete order(s) in DB " + e.getMessage());
        }
        return true;
    }

    @Override
    public int highestOrderNumber() {
        int result = 0;

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(MAX_ORDER_NUMBER);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            logger.info("Returned highest orderNumber " + result + " from database");

        } catch (Exception e) {
            logger.error("Error getting highest orderNumber from database. " + e.getMessage());
        }
        return result;
    }

    @Override
    public Set<LocalDate> waiterSalesDates(int waiterID) throws DAOException {
        Set<LocalDate> waiterSalesDates = new TreeSet<>();


        try (Connection connection = getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_DATES_SQL)) {
            preparedStatement.setInt(1, waiterID);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LocalDate result = resultSet.getDate(1).toLocalDate();
                waiterSalesDates.add(result);

            }
            logger.info("Retrieved waiter sales dates from DB");

        } catch (SQLException e) {
            logger.error("Error getting dates from DB. " + e.getMessage());
            throw new DAOException("Failed to insert order in DB " + e.getMessage());
        }
        return waiterSalesDates;
    }
}