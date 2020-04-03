package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.dao.BeverageDAO;
import be.hogent.cafe.model.dao.BeverageDAOImpl;
import be.hogent.cafe.model.dao.DAOException;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BeverageDAOImplTest {
    private BeverageDAO beverageDAOimpl;

    @Before
    public void setUp() throws Exception {
        beverageDAOimpl = BeverageDAOImpl.getInstance();
    }

    @Test
    public void testGetBeverages() throws DAOException {
        List<Beverage> BeveragesDAO = new ArrayList<>();

        BeveragesDAO = BeverageDAOImpl.getInstance().getBeverages();
        assertEquals(BeveragesDAO.iterator().next(), new Beverage(1, "Cola", 2.40));
        assertEquals(17, BeveragesDAO.size());
    }

}
