package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Beverage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeverageDAOImplTest {


    @Test
    public void testGetBeverages()  {
        Set<Beverage> BeveragesDAO;

        BeveragesDAO = BeverageDAOImpl.getInstance().getBeverages();
        Beverage cola = new Beverage(1, "Cola", 2.40);
        Assertions.assertTrue(BeveragesDAO.contains(cola), "testGetBeverages 01 failed");
        assertEquals(17, BeveragesDAO.size(), "testGetBeverages02 failed");
    }
    }
