package test;

import model.Category;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.CategoryRepository;
import utility.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class CategoryRepositoryTest {
    private Connection connection;
    private CategoryRepository categoryRepository;

    @Before
    public void setUp() throws SQLException {
        connection = DatabaseConnection.connect();
        categoryRepository = new CategoryRepository();
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void searchCategoryByIdTest() {
        Category retrievedCategory = categoryRepository.getCategoryById(3);
        Category retrievedCategory2 = categoryRepository.getCategoryById(-2);

        System.out.println(retrievedCategory.toString());

        assertNotNull(retrievedCategory);
        assertNull(retrievedCategory2);
    }

    @Test
    public void insertCategoryTest() {
        Category newCategory = new Category("Test Category", "Test Description");
        categoryRepository.createCategory(newCategory);

        Category retrievedCategory = categoryRepository.getCategoryById(newCategory.getCategoryId());

        assertNotNull(retrievedCategory);
        assertEquals(newCategory.getName(), retrievedCategory.getName());

        categoryRepository.deleteCategory(newCategory.getCategoryId());
    }

    @Test
    public void deleteCategoryByIdTest() {
        Category newCategory = new Category("Test Category", "Test Description");
        categoryRepository.createCategory(newCategory);

        categoryRepository.deleteCategory(newCategory.getCategoryId());

        assertNull(categoryRepository.getCategoryById(newCategory.getCategoryId()));
    }

    @Test
    public void updateCategoryTest() {
        Category newCategory = new Category("Test Category", "Test Description");
        categoryRepository.createCategory(newCategory);

        Category newCategory2 = new Category("Updated Category", "New Description");
        categoryRepository.updateCategory(newCategory, newCategory2);

        Category searchedCategory = categoryRepository.getCategoryById(newCategory.getCategoryId());
        assertEquals("Updated Category", searchedCategory.getName());

        categoryRepository.deleteCategory(newCategory.getCategoryId());
    }
}
