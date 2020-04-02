
package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.*;

import java.util.*;


public interface BeverageDAO {
    List<Beverage> getBeverages() throws DAOException;
}
