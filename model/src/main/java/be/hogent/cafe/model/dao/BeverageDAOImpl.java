package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Beverage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class BeverageDAOImpl extends BaseDAO implements BeverageDAO {
    private static final String GET_ALL_BEVERAGES = "SELECT * from beverages";

    private final Logger logger = LogManager.getLogger(BeverageDAOImpl.class.getName());

    private static BeverageDAOImpl instance;

    private BeverageDAOImpl() {
    }

    public synchronized static BeverageDAO getInstance() {
        if (instance == null) {
            instance = new BeverageDAOImpl();
        }
        return instance;
    }

    @Override
    public Set<Beverage> getBeverages() {
        Set<Beverage> beverages = new HashSet<>();

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_BEVERAGES);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            while (resultSet.next()) {
                Beverage beverage = new Beverage();
                beverage.setBeverageID(resultSet.getInt("beverageID"));
                beverage.setBeverageName(resultSet.getString("beverageName"));
                beverage.setPrice(resultSet.getDouble("price"));
                beverages.add(beverage);
            }
            logger.info("beverages are loaded from database");

        } catch (Exception e) {
            logger.error("Error getting beverages. " + e.getMessage());
        }
        return beverages;
    }

}

