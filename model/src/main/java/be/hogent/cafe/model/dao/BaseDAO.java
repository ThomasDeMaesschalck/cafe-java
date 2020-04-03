package be.hogent.cafe.model.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseDAO implements DAO {
    private final Logger logger = LogManager.getLogger (BaseDAO.class.getName ());

    private String dbUrl, dbPassword, dbUser;


    private  void readProperties() {

        Properties dbProperties = new Properties ();

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream ("db.properties")) {

            assert inputStream != null;
            dbProperties.load (inputStream);
            dbUrl = dbProperties.getProperty("dbUrl");
            dbPassword = dbProperties.getProperty("dbPassword");
            dbUser = dbProperties.getProperty("dbUser");

        } catch (IOException ioe) {
            logger.error("db properties not loaded");
            ioe.printStackTrace ();
        }
    }

    public Connection getConnection () throws DAOException {
        try {
            readProperties();
            return DriverManager.getConnection (dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DAOException (e.getMessage());
        }
    }
}