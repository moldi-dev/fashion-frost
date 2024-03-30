package repository;

import model.Basket;
import model.Clothing;
import utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BasketRepository {
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private String sqlQuery;

    // Adds a new basket in the database
    public void createBasket(Basket basket) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "INSERT INTO baskets (user_id, clothing_id, total_amount) VALUES (?, ?, ?)";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, basket.getUserId());
            preparedStatement.setInt(2, basket.getClothingId());
            preparedStatement.setDouble(3, basket.getTotalAmount());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT lastval()");

                if (resultSet.next()) {
                    int generatedBasketId = resultSet.getInt(1);
                    basket.setBasketId(generatedBasketId);

                    System.out.println("[Database]: Successfully inserted a new basket in the database!");
                } else {
                    System.out.println("[Database]: Failed to insert a new basket in the database!");
                }
            } else {
                System.out.println("[Database]: Failed to insert a new basket in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "UPDATE clothings SET stock_quantity = stock_quantity - 1 WHERE clothing_id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, basket.getClothingId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully updated a clothing in the database!");
            } else {
                System.out.println("[Database]: Failed to update a clothing in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Retrieves a basket with the specified id from the database
    public Basket getBasketById(int basketId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM baskets WHERE basket_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, basketId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractBasketFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    public void placeOrder(int userId) {
        try {
            // Use the basket repo to get the clothings in the basket for the specified user
            List<Clothing> clothingList = getClothingsInBasketForUser(userId);

            // For each clothing in the list returned by the basket repo
            for (Clothing clothing : clothingList) {
                connection = DatabaseConnection.connect();

                try {
                    // Insert the clothing in the orders table
                    sqlQuery = "INSERT INTO orders (user_id, clothing_id, total_amount, status) VALUES (?, ?, ?, ?)";

                    preparedStatement = connection.prepareStatement(sqlQuery);

                    preparedStatement.setInt(1, userId);
                    preparedStatement.setInt(2, clothing.getClothingId());
                    preparedStatement.setDouble(3, clothing.getPrice());
                    preparedStatement.setString(4, "PENDING");

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("[Database]: Successfully inserted a new order in the database!");
                    } else {
                        System.out.println("[Database]: Failed to insert a new order in the database!");
                    }

                    // Removes the current clothing in the basket and it doesn't update the stock quantity
                    // The stock quantity decrements by 1 if a user adds a product to his basket
                    // and increments by 1 if the user removes that product from his basket
                    // If the user places an order, the stock quantity for that product doesn't increment by 1
                    deleteClothingInBasketForUserWithoutUpdating(userId, clothing.getClothingId());
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    closeResources();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Computes the total revenue which the user has to pay for the products in his basket
    public double getTotalPriceToPayForUser(int userId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT SUM(baskets.total_amount) AS total_to_pay " +
                    "FROM " +
                    "    baskets " +
                    "        JOIN users ON baskets.user_id = ? " +
                    "GROUP BY baskets.user_id, users.user_id " +
                    "HAVING users.user_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("total_to_pay");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return 0;
    }

    // Removes a certain product from the given user's basket without changing the product's stock quantity afterwards
    public void deleteClothingInBasketForUserWithoutUpdating(int userId, int clothingId) {
        connection = null;
        statement = null;
        preparedStatement = null;

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "DELETE FROM baskets" +
                    " WHERE (user_id, clothing_id) = (?, ?)" +
                    "  AND ctid = (" +
                    "    SELECT ctid" +
                    "    FROM baskets" +
                    "    WHERE user_id = ? AND clothing_id = ?" +
                    "    LIMIT 1" +
                    ")";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, clothingId);
            preparedStatement.setInt(3, userId);
            preparedStatement.setInt(4, clothingId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully deleted a basket from the database!");
            } else {
                System.out.println("[Database]: Failed to delete a basket from the database!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Removes a certain product from the user's basket and then it increments the product's stock quantity by 1
    // Used for when the user removes an item from his basket without placing an order
    public void deleteClothingInBasketForUser(int userId, int clothingId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "DELETE FROM baskets" +
                    " WHERE (user_id, clothing_id) = (?, ?)" +
                    "  AND ctid = (" +
                    "    SELECT ctid" +
                    "    FROM baskets" +
                    "    WHERE user_id = ? AND clothing_id = ?" +
                    "    LIMIT 1" +
                    ")";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, clothingId);
            preparedStatement.setInt(3, userId);
            preparedStatement.setInt(4, clothingId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully deleted a basket from the database!");
            } else {
                System.out.println("[Database]: Failed to delete a basket from the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "UPDATE clothings SET stock_quantity = stock_quantity + 1 WHERE clothing_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, clothingId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully updated a clothing in the database!");
            } else {
                System.out.println("[Database]: Failed to update a clothing in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Retrieves all clothings which are in a user's basket
    public List<Clothing> getClothingsInBasketForUser(int userId) {
        List<Clothing> clothingList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT clothings.* " +
                    "FROM baskets " +
                    "        JOIN clothings ON baskets.clothing_id = clothings.clothing_id " +
                    "WHERE baskets.user_id = ?";

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

    // Retrieves every basket in the baskets table
    public List<Basket> getAllBaskets() {
        List<Basket> basketList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM baskets";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                Basket basket = extractBasketFromResultSet(resultSet);
                basketList.add(basket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return basketList;
    }

    // Updates a given basket
    public void updateBasket(Basket searchedBasket, Basket updatedBasket) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "UPDATE baskets SET user_id = ?, clothing_id = ?, total_amount = ? WHERE basket_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, updatedBasket.getUserId());
            preparedStatement.setInt(2, updatedBasket.getClothingId());
            preparedStatement.setDouble(3, updatedBasket.getTotalAmount());
            preparedStatement.setInt(4, searchedBasket.getBasketId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully updated a basket in the database!");
            } else {
                System.out.println("[Database]: Failed to update a basket in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Deletes a basket with the given id
    public void deleteBasket(int basketId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "DELETE FROM baskets WHERE basket_id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, basketId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully deleted a basket from the database!");
            } else {
                System.out.println("[Database]: Failed to delete a basket from the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Extracts a basket from the givne result set
    private Basket extractBasketFromResultSet(ResultSet resultSet) throws SQLException {
        Basket basket = new Basket();

        basket.setUserId(resultSet.getInt("user_id"));
        basket.setBasketId(resultSet.getInt("basket_id"));
        basket.setClothingId(resultSet.getInt("clothing_id"));
        basket.setTotalAmount(resultSet.getDouble("total_amount"));

        return basket;
    }

    // Extracts a clothing from the given result set
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
