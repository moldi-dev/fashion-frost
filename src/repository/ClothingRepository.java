package repository;

import model.Clothing;
import utility.DatabaseConnection;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClothingRepository {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private String sqlQuery;

    // Adds a new clothing in the database
    public void createClothing(Clothing clothing) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "INSERT INTO clothings (name, category, brand, description, price, stock_quantity, created_at, image_url) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, clothing.getName());
            preparedStatement.setInt(2, clothing.getCategory());
            preparedStatement.setInt(3, clothing.getBrand());
            preparedStatement.setString(4, clothing.getDescription());
            preparedStatement.setDouble(5, clothing.getPrice());
            preparedStatement.setInt(6, clothing.getStockQuantity());
            preparedStatement.setTimestamp(7, clothing.getCreatedAt());
            preparedStatement.setString(8, clothing.getImageUrl());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int generatedClothingId = generatedKeys.getInt(1);
                    clothing.setClothingId(generatedClothingId);
                    System.out.println("[Database]: Successfully inserted a new clothing in the database!");
                } else {
                    System.out.println("[Database]: Failed to insert a new clothing in the database!");
                }
            } else {
                System.out.println("[Database]: Failed to insert a new clothing in the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Retrieves a clothing by a given id
    public Clothing getClothingById(int clothingId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM clothings WHERE clothing_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, clothingId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractClothingFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves a clothing by a given name
    public Clothing getClothingByExactName(String clothingName) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM clothings WHERE name = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, clothingName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractClothingFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Same as before, but now the query uses ILIKE instead of equals
    public Clothing getClothingByNameForAdminPanel(String clothingName) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM clothings WHERE name ILIKE ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, clothingName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractClothingFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return null;
    }

    // Retrieves every clothing in stock which contains the given filters (the given name in the search bar, the check boxes and the radio buttons)
    public List<Clothing> getFilteredClothingsInStock(String clothingName, List<JCheckBox> categoryCheckBoxList, List<JCheckBox> brandCheckBoxList, List<JRadioButton> orderByRadioButtons) {
        List<Clothing> clothingList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT clothings.* FROM clothings " +
                    "JOIN categories ON clothings.category = categories.category_id " +
                    "JOIN brands ON clothings.brand = brands.brand_id " +
                    "WHERE clothings.name ILIKE ?";

            StringBuilder dynamicConditions = new StringBuilder();
            List<String> categoryConditions = new ArrayList<>();
            List<String> brandConditions = new ArrayList<>();

            for (JCheckBox checkBox : categoryCheckBoxList) {
                if (checkBox.isSelected()) {
                    categoryConditions.add("?");
                }
            }

            for (JCheckBox checkBox : brandCheckBoxList) {
                if (checkBox.isSelected()) {
                    brandConditions.add("?");
                }
            }

            if (!categoryConditions.isEmpty()) {
                dynamicConditions.append(" AND categories.name IN (");

                for (int i = 0; i < categoryConditions.size(); i++) {
                    if (i < categoryConditions.size() - 1) {
                        dynamicConditions.append("?, ");
                    } else {
                        dynamicConditions.append("?)");
                    }
                }
            }

            if (!brandConditions.isEmpty()) {
                dynamicConditions.append(" AND brands.name IN (");

                for (int i = 0; i < brandConditions.size(); i++) {
                    if (i < brandConditions.size() - 1) {
                        dynamicConditions.append("?, ");
                    } else {
                        dynamicConditions.append("?)");
                    }
                }
            }

            String orderQuery = "";
            String selectedOrdering = "";

            for (JRadioButton radioButton : orderByRadioButtons) {
                if (radioButton.isSelected()) {
                    selectedOrdering = radioButton.getText();
                    break;
                }
            }

            switch (selectedOrdering) {
                case "None":
                    orderQuery = "";
                    break;
                case "A - Z":
                    orderQuery = "ORDER BY clothings.name ASC";
                    break;
                case "Z - A":
                    orderQuery = "ORDER BY clothings.name DESC";
                    break;
                case "Newest":
                    orderQuery = "ORDER BY clothings.created_at DESC";
                    break;
                case "Oldest":
                    orderQuery = "ORDER BY clothings.created_at ASC";
                    break;
                case "Price ascending":
                    orderQuery = "ORDER BY clothings.price ASC";
                    break;
                case "Price descending":
                    orderQuery = "ORDER BY clothings.price DESC";
                    break;
            }

            String finalQuery = sqlQuery + dynamicConditions + " AND stock_quantity > 0 " + orderQuery;

            preparedStatement = connection.prepareStatement(finalQuery);
            preparedStatement.setString(1, "%" + clothingName + "%");

            int index = 2;
            for (JCheckBox checkBox : categoryCheckBoxList) {
                if (checkBox.isSelected()) {
                    preparedStatement.setString(index++, checkBox.getText());
                }
            }

            for (JCheckBox checkBox : brandCheckBoxList) {
                if (checkBox.isSelected()) {
                    preparedStatement.setString(index++, checkBox.getText());
                }
            }

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

    // Retrieves every clothing which contains the given name
    public List<Clothing> getClothingByName(String clothingName) {
        List<Clothing> clothingList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM clothings WHERE name ILIKE ?";

            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, "%" + clothingName + "%");
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

    // Retrieves all products in stock (stock quantity > 0)
    public List<Clothing> getAllClothingInStock() {
        List<Clothing> clothingList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM clothings WHERE stock_quantity > 0 ORDER BY clothings.clothing_id ASC";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

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

    // Retrieves all products
    public List<Clothing> getAllClothing() {
        List<Clothing> clothingList = new ArrayList<>();

        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "SELECT * FROM clothings";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

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

    // Updates a product
    public void updateClothing(Clothing searchedClothing, Clothing updatedClothing) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "UPDATE clothings SET name = ?, category = ?, brand = ?, description = ?, " +
                    "price = ?, stock_quantity = ?, created_at = ?, image_url = ? WHERE clothing_id = ?";

            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, updatedClothing.getName());
            preparedStatement.setInt(2, updatedClothing.getCategory());
            preparedStatement.setInt(3, updatedClothing.getBrand());
            preparedStatement.setString(4, updatedClothing.getDescription());
            preparedStatement.setDouble(5, updatedClothing.getPrice());
            preparedStatement.setInt(6, updatedClothing.getStockQuantity());
            preparedStatement.setTimestamp(7, updatedClothing.getCreatedAt());
            preparedStatement.setString(8, updatedClothing.getImageUrl());
            preparedStatement.setInt(9, searchedClothing.getClothingId());

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

    // Deletes a clothing from the database
    public void deleteClothing(int clothingId) {
        try {
            connection = DatabaseConnection.connect();

            sqlQuery = "DELETE FROM clothings WHERE clothing_id = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, clothingId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("[Database]: Successfully deleted a clothing from the database!");
            } else {
                System.out.println("[Database]: Failed to delete a clothing from the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    // Extracts a clothing from the result set
    private Clothing extractClothingFromResultSet(ResultSet resultSet) throws SQLException {
        Clothing clothing = new Clothing();

        clothing.setClothingId(resultSet.getInt("clothing_id"));
        clothing.setCategory(resultSet.getInt("category"));
        clothing.setBrand(resultSet.getInt("brand"));
        clothing.setDescription(resultSet.getString("description"));
        clothing.setPrice(resultSet.getDouble("price"));
        clothing.setStockQuantity(resultSet.getInt("stock_quantity"));
        clothing.setCreatedAt(resultSet.getTimestamp("created_at"));
        clothing.setImageUrl(resultSet.getString("image_url"));
        clothing.setName(resultSet.getString("name"));

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
