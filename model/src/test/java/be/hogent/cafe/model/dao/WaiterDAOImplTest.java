package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Waiter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WaiterDAOImplTest {


    @Test
    public void testGetWaiters() {
        Set<Waiter> waitersDAO;

        waitersDAO = WaiterDAOImpl.getInstance().getWaiters();
        Waiter wout = new Waiter(1, "Peters", "Wout", "password");
        Assertions.assertTrue(waitersDAO.contains(wout), "testGetWaiters 01 failed");
        assertEquals(4, waitersDAO.size(), "testGetWaiters 02 failed");
    }
}
