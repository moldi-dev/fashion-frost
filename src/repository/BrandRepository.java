package repository;

import model.Brand;
import utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandRepository {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private String sqlQuery;

    // Adds a new brand to the database
    public void createBrand(Brand brand) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "INSERT INTO brands (name, description) VALUES (?, ?)";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, brand.getName());
            preparedStatement.setString(2, brand.getDescription());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT lastval()");

                if (resultSet.next()) {
                    int generatedBrandId = resultSet.getInt(1);
                    brand.setBrandId(generatedBrandId);

                    System.out.println("[Database]: Successfully inserted a new brand in the database!");
                } else {
                    System.out.println("[Database]: Failed to insert a new brand in the database!");
                }
            } else {
                System.out.println("[Database]: Failed to insert a new brand in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Retrieves the brand with the given id from the database
    public Brand getBrandById(int brandId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM brands WHERE brand_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, brandId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractBrandFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves the brand with the given name from the database
    public Brand getBrandByName(String brandName) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM brands WHERE name = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, brandName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractBrandFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Same operation, but now it uses ILIKE instead of equals in the SQL query
    // Not going to lie, should've used this one for every getBrandByName operation
    // I'm too lazy now to refactor this
    // The app works, so I'm leaving it as it is
    public Brand getBrandByNameForAdminPanel(String brandName) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM brands WHERE name ILIKE ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, brandName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractBrandFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves the brand associated with the given clothing name
    public Brand getBrandByClothingName(String clothingName) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM brands " +
                    "JOIN clothings ON brands.brand_id = clothings.brand " +
                    "WHERE clothings.name = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, clothingName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractBrandFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves all the brands in the database
    public List<Brand> getAllBrands() {
        List<Brand> brandList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM brands";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                Brand brand = extractBrandFromResultSet(resultSet);
                brandList.add(brand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return brandList;
    }

    // Updates a certain brand
    public void updateBrand(Brand searchedBrand, Brand updatedBrand) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "UPDATE brands SET name = ?, description = ? WHERE brand_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, updatedBrand.getName());
            preparedStatement.setString(2, updatedBrand.getDescription());
            preparedStatement.setInt(3, searchedBrand.getBrandId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully updated a brand in the database!");
            } else {
                System.out.println("[Database]: Failed to update a brand in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Deletes a brand from the database
    public void deleteBrand(int brandId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "DELETE FROM brands WHERE brand_id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, brandId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully deleted a brand from the database!");
            } else {
                System.out.println("[Database]: Failed to delete a brand from the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Extracts a brand from a result set
    private Brand extractBrandFromResultSet(ResultSet resultSet) throws SQLException {
        Brand brand = new Brand();

        brand.setBrandId(resultSet.getInt("brand_id"));
        brand.setName(resultSet.getString("name"));
        brand.setDescription(resultSet.getString("description"));

        return brand;
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
