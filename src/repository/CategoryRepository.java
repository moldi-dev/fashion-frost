package repository;

import model.Category;
import utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private String sqlQuery;

    // Adds a new category to the database
    public void createCategory(Category category) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "INSERT INTO categories (name, description) VALUES (?, ?)";

            preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int generatedCategoryId = generatedKeys.getInt(1);
                    category.setCategoryId(generatedCategoryId);
                    System.out.println("[Database]: Successfully inserted a new category in the database!");
                } else {
                    System.out.println("[Database]: Failed to insert a new category in the database!");
                }
            } else {
                System.out.println("[Database]: Failed to insert a new category in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Retrieves a category from the database by the given id
    public Category getCategoryById(int categoryId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM categories WHERE category_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, categoryId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractCategoryFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves a category from the database by the given name
    public Category getCategoryByName(String categoryName) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM categories WHERE name = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, categoryName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractCategoryFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Same as above, but it uses ILIKE instead of equals in the query
    public Category getCategoryByNameForAdminPanel(String categoryName) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM categories WHERE name ILIKE ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, categoryName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractCategoryFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves the associated category with the given product name
    public Category getCategoryByClothingName(String clothingName) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM categories " +
                    "JOIN clothings ON categories.category_id = clothings.category " +
                    "WHERE clothings.name = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, clothingName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractCategoryFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves all the categories
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM categories";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                Category category = extractCategoryFromResultSet(resultSet);
                categoryList.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return categoryList;
    }

    // Updates a category
    public void updateCategory(Category searchedCategory, Category updatedCategory) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, updatedCategory.getName());
            preparedStatement.setString(2, updatedCategory.getDescription());
            preparedStatement.setInt(3, searchedCategory.getCategoryId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully updated a category in the database!");
            } else {
                System.out.println("[Database]: Failed to update a category in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Deletes a category from the database
    public void deleteCategory(int categoryId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "DELETE FROM categories WHERE category_id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, categoryId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully deleted a category from the database!");
            } else {
                System.out.println("[Database]: Failed to delete a category from the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Extracts a category from the result set
    private Category extractCategoryFromResultSet(ResultSet resultSet) throws SQLException {
        Category category = new Category();

        category.setCategoryId(resultSet.getInt("category_id"));
        category.setName(resultSet.getString("name"));
        category.setDescription(resultSet.getString("description"));

        return category;
    }

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
