package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaidOrderDAOImpl extends BaseDAO implements PaidOrderDAO {
        private static final String GET_ALL_ORDERS = "SELECT * from orders";
        private static final String INSERT_ORDER = "INSERT into orders (orderNumber, beverageID, qty, date, waiterID) VALUES (?, ?,?,?,?)";


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
                    double beveragePrice = 0;
                    String beverageName = null;

                    for (Beverage beverage : Cafe.getBeverages()) { //prijs en naam van beverage zoeken op basis van ID
                        if (beverage.getBeverageID() == resultSet.getInt("beverageID"))
                        {
                            beveragePrice = beverage.getPrice();
                            beverageName = beverage.getBeverageName();
                        }
                    }
                    Beverage beverage = new Beverage(resultSet.getInt("beverageID"), beverageName, beveragePrice);

                    OrderItem orderItem = new OrderItem(resultSet.getInt("ID"), beverage, resultSet.getInt("qty"));
                    if (paidOrders.contains(paidOrder)) //als er een order in collectie zit met zelfde orderNumber => geen nieuw order aanmaken maar orderiTem toevoegen
                    {
                        for (Order paidOrderFromSet: paidOrders)
                        {
                            if(paidOrderFromSet.getOrderNumber() == paidOrder.getOrderNumber()) {
                                paidOrderFromSet.getOrderLines().add(orderItem);
                            }
                        }
                    }
                    else {
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
        public int insertOrder(Order o) {
            int result = 0;

            for (OrderItem oi: o.getOrderLines()) {

                try (Connection connection = getConnection();
                     PreparedStatement pStatement = connection.prepareStatement(INSERT_ORDER)) {
                    pStatement.setInt(1, o.getOrderNumber());
                    pStatement.setInt(2, oi.getBeverage().getBeverageID());
                    pStatement.setInt(3, oi.getQty());
                    pStatement.setDate(4, Date.valueOf(o.getDate()));
                    pStatement.setInt(5, o.getWaiterID());


                    pStatement.executeUpdate();
                } catch (Exception e) {
                    logger.error("Error insert person. " + e.getMessage());
                }
            }
            return result;
        }
    }
