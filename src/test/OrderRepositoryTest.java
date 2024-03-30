package test;

import model.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.OrderRepository;
import utility.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.Assert.*;

public class OrderRepositoryTest {
    private Connection connection;
    private OrderRepository orderRepository;

    @Before
    public void setUp() throws SQLException {
        connection = DatabaseConnection.connect();
        orderRepository = new OrderRepository();
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void searchOrderByIdTest() {
        Order retrievedOrder = orderRepository.getOrderById(3);
        Order retrievedOrder2 = orderRepository.getOrderById(-2);

        System.out.println(retrievedOrder.toString());

        assertNotNull(retrievedOrder);
        assertNull(retrievedOrder2);
    }

    @Test
    public void insertOrderTest() {
        Order newOrder = new Order(1, new Timestamp(System.currentTimeMillis()), 1, 100.0, "SHIPPING", 1);
        orderRepository.createOrder(newOrder);

        Order retrievedOrder = orderRepository.getOrderById(newOrder.getOrderId());

        assertNotNull(retrievedOrder);
        assertEquals(newOrder.getStatus(), retrievedOrder.getStatus());

        orderRepository.deleteOrder(newOrder.getOrderId());
    }

    @Test
    public void deleteOrderByIdTest() {
        Order newOrder = new Order(1, new Timestamp(System.currentTimeMillis()), 1, 100.0, "SHIPPING", 1);
        orderRepository.createOrder(newOrder);

        orderRepository.deleteOrder(newOrder.getOrderId());

        assertNull(orderRepository.getOrderById(newOrder.getOrderId()));
    }

    @Test
    public void updateOrderTest() {
        Order newOrder = new Order(1, new Timestamp(System.currentTimeMillis()), 1, 100.0, "SHIPPING", 1);
        orderRepository.createOrder(newOrder);

        Order newOrder2 = new Order(1, new Timestamp(System.currentTimeMillis()), 1, 120.0, "SHIPPING", 1);
        orderRepository.updateOrder(newOrder, newOrder2);

        Order searchedOrder = orderRepository.getOrderById(newOrder.getOrderId());
        assertEquals(120.0, searchedOrder.getTotalAmount(), 0.01);

        orderRepository.deleteOrder(newOrder.getOrderId());
    }
}
