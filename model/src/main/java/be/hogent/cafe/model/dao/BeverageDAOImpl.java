package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Beverage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


public class BeverageDAOImpl extends BaseDAO implements BeverageDAO {
    private static final String GET_ALL_BEVERAGES = "SELECT * from beverages";
    private static final String GET_BEVERAGE_BY_ID = "SELECT * from beverages where beverageID = ?";

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
    public  Set<Beverage> getBeverages() {
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

/*    @Override  //werkt maar zeer trage code door veelvoudige DB lookups
    public Beverage getBeverageByID(int beverageID){
        Beverage beverage = new Beverage();
        try ( Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(GET_BEVERAGE_BY_ID)) {
                preparedStatement.setInt(1, beverageID);
                ResultSet resultSet = preparedStatement.executeQuery();
        {

            while (resultSet.next()) {
                beverage.setBeverageID(resultSet.getInt("beverageID"));
                beverage.setBeverageName(resultSet.getString("beverageName"));
                beverage.setPrice(resultSet.getDouble("price"));
            }
            logger.info("beverage are loaded from database");
        }
        } catch (Exception e) {
            logger.error("Error getting beverage. " + e.getMessage());
        }
        return beverage;
    }

 */
}

