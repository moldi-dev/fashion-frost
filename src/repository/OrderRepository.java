package repository;

import model.Clothing;
import model.Order;
import utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private String sqlQuery;

    // Adds a new order in the database
    public void createOrder(Order order) {
        try {
            connection = DatabaseConnection.connect();
            sqlQuery = "INSERT INTO orders (user_id, order_date, clothing_id, total_amount, status) " +
                    "VALUES (?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, order.getUserId());
            preparedStatement.setTimestamp(2, order.getOrderDate());
            preparedStatement.setInt(3, order.getClothingId());
            preparedStatement.setDouble(4, order.getTotalAmount());
            preparedStatement.setString(5, order.getStatus());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int generatedOrderId = generatedKeys.getInt(1);
                    order.setOrderId(generatedOrderId);
                    System.out.println("[Database]: Successfully inserted a new order in the database!");
                } else {
                    System.out.println("[Database]: Failed to insert a new order in the database!");
                }
            } else {
                System.out.println("[Database]: Failed to insert a new order in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Retrieves every product ordered by a user
    public List<Clothing> getClothingsInOrdersForUser(int userId) {
        List<Clothing> clothingList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();
            sqlQuery = "SELECT clothings.* " +
                    "FROM clothings " +
                    "    JOIN orders ON clothings.clothing_id = orders.clothing_id " +
                    "WHERE orders.user_id = ? ORDER BY order_date DESC";

            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Clothing clothing = extractClothingFromResultSet(resultSet);
                clothingList.add(clothing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return clothingList;
    }

    // Retrieves the order status for a given user's ordered product
    public String getOrderStatusForUserOrder(int userId, int clothingId) {
        try {
            connection = DatabaseConnection.connect();
            sqlQuery = "SELECT orders.status FROM orders JOIN users ON orders.user_id = users.user_id WHERE users.user_id = ? AND orders.clothing_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, clothingId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return "NULL";
    }

    // Retrieves the order date for a user's ordered product
    public String getOrderDateForUserOrder(int userId, int clothingId) {
        try {
            connection = DatabaseConnection.connect();
            sqlQuery = "SELECT orders.order_date " +
                    "FROM orders " +
                    "        JOIN users ON orders.user_id = users.user_id " +
                    "WHERE users.user_id = ? AND orders.clothing_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, clothingId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getTimestamp("order_date").toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return "NULL";
    }

    // Retrieves an order by the given id
    public Order getOrderById(int orderId) {
        try {
            connection = DatabaseConnection.connect();
            sqlQuery = "SELECT * FROM orders WHERE order_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, orderId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractOrderFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves all orders
    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();
            sqlQuery = "SELECT * FROM orders";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                Order order = extractOrderFromResultSet(resultSet);
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return orderList;
    }

    // Updates an order
    public void updateOrder(Order searchedOrder, Order updatedOrder) {
        try {
            connection = DatabaseConnection.connect();
            sqlQuery = "UPDATE orders SET user_id = ?, order_date = ?, clothing_id = ?, " +
                    "total_amount = ?, status = ? WHERE order_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, updatedOrder.getUserId());
            preparedStatement.setTimestamp(2, updatedOrder.getOrderDate());
            preparedStatement.setInt(3, updatedOrder.getClothingId());
            preparedStatement.setDouble(4, updatedOrder.getTotalAmount());
            preparedStatement.setString(5, updatedOrder.getStatus());
            preparedStatement.setInt(6, searchedOrder.getOrderId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully updated an order in the database!");
            } else {
                System.out.println("[Database]: Failed to update an order in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Deletes an order from the database
    public void deleteOrder(int orderId) {
        try {
            connection = DatabaseConnection.connect();
            sqlQuery = "DELETE FROM orders WHERE order_id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, orderId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully deleted an order from the database!");
            } else {
                System.out.println("[Database]: Failed to delete an order from the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Extracts an order from the result set
    private Order extractOrderFromResultSet(ResultSet resultSet) throws SQLException {
        Order order = new Order();

        order.setOrderId(resultSet.getInt("order_id"));
        order.setUserId(resultSet.getInt("user_id"));
        order.setOrderDate(resultSet.getTimestamp("order_date"));
        order.setClothingId(resultSet.getInt("clothing_id"));
        order.setTotalAmount(resultSet.getInt("total_amount"));
        order.setStatus(resultSet.getString("status"));
        order.setQuantity(resultSet.getInt("quantity"));

        return order;
    }

    // Extracts a clothing from a result set
    private Clothing extractClothingFromResultSet(ResultSet resultSet) throws SQLException {
        Clothing clothing = new Clothing();

        clothing.setName(resultSet.getString("name"));
        clothing.setImageUrl(resultSet.getString("image_url"));
        clothing.setPrice(resultSet.getDouble("price"));
        clothing.setClothingId(resultSet.getInt("clothing_id"));
        clothing.setDescription(resultSet.getString("description"));
        clothing.setCategory(resultSet.getInt("category"));
        clothing.setCreatedAt(resultSet.getTimestamp("created_at"));
        clothing.setStockQuantity(resultSet.getInt("stock_quantity"));
        clothing.setBrand(resultSet.getInt("brand"));

        return clothing;
    }

    // Close the opened connections with the database
    private void closeResources() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }

            if (statement != null) {
                statement.close();
                statement = null;
            }

            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
