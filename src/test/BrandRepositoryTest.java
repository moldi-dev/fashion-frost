package test;

import model.Brand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.BrandRepository;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class BrandRepositoryTest {
    private Connection connection;
    private BrandRepository brandRepository;

    @Before
    public void setUp() throws SQLException {
        brandRepository = new BrandRepository();
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void searchBrandByIdTest() {
        Brand retrievedBrand = brandRepository.getBrandById(1);
        Brand retrievedBrand2 = brandRepository.getBrandById(-3);

        System.out.println(retrievedBrand.toString());

        assertNotNull(retrievedBrand);
        assertNull(retrievedBrand2);
    }

    @Test
    public void insertBrandTest() {
        Brand newBrand = new Brand("TEST", "TEST");
        brandRepository.createBrand(newBrand);

        Brand retrievedBrand = brandRepository.getBrandById(newBrand.getBrandId());

        assertNotNull(retrievedBrand);
        assertEquals(newBrand.getName(), retrievedBrand.getName());

        brandRepository.deleteBrand(newBrand.getBrandId());
    }

    @Test
    public void deleteBrandByIdTest() {
        Brand newBrand = new Brand("TEST", "TEST");
        brandRepository.createBrand(newBrand);

        brandRepository.deleteBrand(newBrand.getBrandId());

        assertNull(brandRepository.getBrandById(newBrand.getBrandId()));
    }

    @Test
    public void updateBrandTest() {
        Brand newBrand = new Brand("TEST", "TEST");
        brandRepository.createBrand(newBrand);

        Brand newBrand2 = new Brand("TEST2", "TEST2");
        brandRepository.updateBrand(newBrand, newBrand2);

        Brand searchedBrand = brandRepository.getBrandById(newBrand.getBrandId());
        assertEquals("TEST2", searchedBrand.getName());

        brandRepository.deleteBrand(newBrand.getBrandId());
    }
}
