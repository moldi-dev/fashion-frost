package repository;

import model.User;
import utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private String sqlQuery;

    // Adds a new user to the database
    public void createUser(User user) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "INSERT INTO users (first_name, last_name, username, email, password, shipping_address, role) VALUES (?, ?, ?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setString(6, user.getShippingAddress());
            preparedStatement.setString(7, user.getRole());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT lastval()");

                if (resultSet.next()) {
                    int generatedUserId = resultSet.getInt(1);
                    user.setUserId(generatedUserId);

                    System.out.println("[Database]: Successfully inserted a new user in the database!");
                } else {
                    System.out.println("[Database]: Failed to insert a new user in the database!");
                }
            } else {
                System.out.println("[Database]: Failed to insert a new user in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Retrieves an user based on its id
    public User getUserById(int userId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM users WHERE user_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractUserFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves an user based on its username
    public User getUserByUsername(String username) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM users WHERE username = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractUserFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves all users
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM users";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                User user = extractUserFromResultSet(resultSet);
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return userList;
    }

    // Updates an user
    public void updateUser(User searchedUser, User updatedUser) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "UPDATE users SET first_name = ?, last_name = ?, username = ?, email = ?, password = ?, shipping_address = ?, role = ? WHERE user_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, updatedUser.getFirstName());
            preparedStatement.setString(2, updatedUser.getLastName());
            preparedStatement.setString(3, updatedUser.getUsername());
            preparedStatement.setString(4, updatedUser.getEmail());
            preparedStatement.setString(5, updatedUser.getPassword());
            preparedStatement.setString(6, updatedUser.getShippingAddress());
            preparedStatement.setString(7, updatedUser.getRole());
            preparedStatement.setInt(8, searchedUser.getUserId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully updated an user in the database!");
            } else {
                System.out.println("[Database]: Failed to update an user in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Deletes an user
    public void deleteUser(int userId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "DELETE FROM users WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, userId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully deleted an user from the database!");
            } else {
                System.out.println("[Database]: Failed to delete an user from the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Extracts an user from the result set
    private User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();

        user.setUserId(resultSet.getInt("user_id"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setShippingAddress(resultSet.getString("shipping_address"));
        user.setRole(resultSet.getString("role"));
        user.setCreatedAt(resultSet.getTimestamp("created_at"));

        return user;
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
