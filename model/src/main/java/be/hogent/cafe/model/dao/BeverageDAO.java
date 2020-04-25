
package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Beverage;

import java.util.Set;


public interface BeverageDAO {
    Set<Beverage> getBeverages();
    //   Beverage getBeverageByID(int beverageID);
}
