package be.hogent.cafe.model.dao;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DAOExceptionTest {
    @Test
    public void testDAOException() {
        DAOException daoException = new DAOException();
        assertEquals("Database error", daoException.getMessage());
    }

    @Test
    public void testDAOExceptionToString() {
        DAOException daoException = new DAOException("A more detailed error message");
        assertEquals("A more detailed error message", daoException.toString());
    }
}
