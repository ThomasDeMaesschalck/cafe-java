package be.hogent.cafe.model.dao;

import java.sql.*;

public interface DAO {
    Connection getConnection () throws DAOException;
}