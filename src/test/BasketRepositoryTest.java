package test;

import model.Basket;
import model.Clothing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.BasketRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class BasketRepositoryTest {
    private Connection connection;
    private BasketRepository basketRepository;

    @Before
    public void setUp() throws SQLException {
        basketRepository = new BasketRepository();
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void insertDeleteBasketTest() {
        Basket newBasket = new Basket(1, 1, 10.0);
        basketRepository.createBasket(newBasket);

        basketRepository.deleteBasket(newBasket.getBasketId());

        assertNull(basketRepository.getBasketById(newBasket.getBasketId()));
    }

    @Test
    public void insertUpdateDeleteBasketTest() {
        Basket newBasket = new Basket(1, 1, 10.0);
        basketRepository.createBasket(newBasket);

        Basket newBasket2 = new Basket(2, 1, 15.0);
        basketRepository.updateBasket(newBasket, newBasket2);

        Basket searchedBasket = basketRepository.getBasketById(newBasket.getBasketId());
        assertEquals(15.0, searchedBasket.getTotalAmount(), 0.1);

        basketRepository.deleteBasket(newBasket.getBasketId());
    }

    @Test
    public void getAllClothingsForUserId() {
        List<Clothing> clothingList = basketRepository.getClothingsInBasketForUser(1);

        for (Clothing clothing : clothingList) {
            System.out.println(clothing.toString());

            assertNotNull(clothing.getName());
        }
    }
}
