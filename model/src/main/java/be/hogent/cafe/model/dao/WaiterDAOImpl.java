package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WaiterDAOImpl extends BaseDAO implements WaiterDAO {
    private static final String GET_ALL_WAITERS = "SELECT * from waiters";

    private final Logger logger = LogManager.getLogger(BeverageDAOImpl.class.getName());

    private static WaiterDAOImpl instance;

    private WaiterDAOImpl() {
    }

    public synchronized static WaiterDAO getInstance() {
        if (instance == null) {
            instance = new WaiterDAOImpl();
        }
        return instance;
    }


    @Override
    public Set<Waiter> getWaiters() {
        Set<Waiter> waiters = new HashSet<>();

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_WAITERS);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            while (resultSet.next()) {
                Waiter waiter = new Waiter();
                waiter.setID(resultSet.getInt("waiterID"));
                waiter.setLastName(resultSet.getString("lastName"));
                waiter.setFirstName(resultSet.getString("firstName"));
                waiter.setPassword(resultSet.getString("password"));
                waiters.add(waiter);
            }
            logger.info("Waiters are loaded from database");

        } catch (Exception e) {
            logger.error("Error getting waiters from database. " + e.getMessage());
        }
        return waiters;
    }
}
