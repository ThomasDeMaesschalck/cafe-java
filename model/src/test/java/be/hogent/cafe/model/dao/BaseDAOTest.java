package be.hogent.cafe.model.dao;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

public class BaseDAOTest {

       @Test
    public void testGetConnection() throws DAOException, SQLException {
           BaseDAO testDAO = new BaseDAO();

           Connection connectionTest = testDAO.getConnection();
           assertNotNull(connectionTest);
           assertThat(connectionTest.isValid(0)).isTrue();
       }

    @Test
    public void testDAOException() throws DAOException {
        BaseDAO testDAO = mock(BaseDAO.class);
        willThrow(new DAOException()).given(testDAO).getConnection();
    }

}
