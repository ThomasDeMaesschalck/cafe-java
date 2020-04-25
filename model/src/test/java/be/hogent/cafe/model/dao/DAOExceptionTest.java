package be.hogent.cafe.model.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DAOExceptionTest {
    @Test
    public void testDAOException() {
        DAOException daoException = new DAOException();
        assertThat(daoException.getMessage()).isEqualTo("Database error");
    }

    @Test
    public void testDAOExceptionToString() {
        DAOException daoException = new DAOException("A more detailed error message");
        assertThat(daoException.toString()).isEqualTo("A more detailed error message");
    }
}
