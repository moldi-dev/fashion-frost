package test;

import model.Clothing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.ClothingRepository;
import utility.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.*;

public class ClothingRepositoryTest {
    private Connection connection;
    private ClothingRepository clothingRepository;

    @Before
    public void setUp() throws SQLException {
        connection = DatabaseConnection.connect();
        clothingRepository = new ClothingRepository();
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void searchClothingByIdTest() {
        Clothing retrievedClothing = clothingRepository.getClothingById(3);
        Clothing retrievedClothing2 = clothingRepository.getClothingById(-2);

        System.out.println(retrievedClothing.toString());

        assertNotNull(retrievedClothing);
        assertNull(retrievedClothing2);
    }

    @Test
    public void searchClothingsByNameTest() {
        List<Clothing> clothingList = clothingRepository.getClothingByName("Sweater");

        assertNotNull(clothingList);
        assertFalse(clothingList.isEmpty());

        for (Clothing clothing : clothingList) {
            assertTrue(clothing.getName().toLowerCase().contains("sweater"));
        }
    }


    @Test
    public void insertClothingTest() {
        Clothing newClothing = new Clothing("Test Clothing", 1, 1, "Description", 49.99, 50, new Timestamp(System.currentTimeMillis()), "image_url");
        clothingRepository.createClothing(newClothing);

        Clothing retrievedClothing = clothingRepository.getClothingById(newClothing.getClothingId());

        assertNotNull(retrievedClothing);
        assertEquals(newClothing.getName(), retrievedClothing.getName());

        clothingRepository.deleteClothing(newClothing.getClothingId());
    }

    @Test
    public void deleteClothingByIdTest() {
        Clothing newClothing = new Clothing("Test Clothing", 1, 1, "Description", 49.99, 50, new Timestamp(System.currentTimeMillis()), "image_url");
        clothingRepository.createClothing(newClothing);

        clothingRepository.deleteClothing(newClothing.getClothingId());

        assertNull(clothingRepository.getClothingById(newClothing.getClothingId()));
    }

    @Test
    public void updateClothingTest() {
        Clothing newClothing = new Clothing("Test Clothing", 1, 1, "Description", 49.99, 50, new Timestamp(System.currentTimeMillis()), "image_url");
        clothingRepository.createClothing(newClothing);

        Clothing newClothing2 = new Clothing("Updated Clothing", 2, 2, "New Description", 59.99, 60, new Timestamp(System.currentTimeMillis()), "new_image_url");
        clothingRepository.updateClothing(newClothing, newClothing2);

        Clothing searchedClothing = clothingRepository.getClothingById(newClothing.getClothingId());
        assertEquals("Updated Clothing", searchedClothing.getName());

        clothingRepository.deleteClothing(newClothing.getClothingId());
    }
}
