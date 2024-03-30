package test;

import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.UserRepository;
import utility.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.Assert.*;

public class UserRepositoryTest {
    private Connection connection;
    private UserRepository userRepository;

    @Before
    public void setUp() throws SQLException {
        connection = DatabaseConnection.connect();
        userRepository = new UserRepository();
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void searchUserByIdTest() {
        User retrievedUser = userRepository.getUserById(1);
        User retrievedUser2 = userRepository.getUserById(-2);

        System.out.println(retrievedUser.toString());

        assertNotNull(retrievedUser);
        assertNull(retrievedUser2);
    }

    @Test
    public void searchUserByUsernameTest() {
        User retrievedUser = userRepository.getUserByUsername("moldi");
        User retrievedUser2 = userRepository.getUserByUsername("TEST USER");

        assertNotNull(retrievedUser);
        assertNull(retrievedUser2);
    }

    @Test
    public void insertUserTest() {
        User newUser = new User("TEST", "TEST", "TEST", "TEST", "TEST", "TEST", new Timestamp(System.currentTimeMillis()), "CUSTOMER");
        userRepository.createUser(newUser);

        User retrievedUser = userRepository.getUserById(newUser.getUserId());

        assertNotNull(retrievedUser);
        assertEquals(newUser.getUsername(), retrievedUser.getUsername());

        userRepository.deleteUser(newUser.getUserId());
    }

    @Test
    public void insertSearchUserTest() {
        User newUser = new User("TEST", "TEST", "TEST", "TEST", "TEST", "TEST", new Timestamp(System.currentTimeMillis()), "CUSTOMER");
        userRepository.createUser(newUser);

        User retrievedUser = userRepository.getUserByUsername(newUser.getUsername());

        System.out.println(retrievedUser.toString());

        assertEquals("TEST", retrievedUser.getUsername());

        userRepository.deleteUser(newUser.getUserId());
    }

    @Test
    public void deleteUserByIdTest() {
        User newUser = new User("TEST", "TEST", "TEST", "TEST", "TEST", "TEST", new Timestamp(System.currentTimeMillis()), "CUSTOMER");
        userRepository.createUser(newUser);

        userRepository.deleteUser(newUser.getUserId());

        assertNull(userRepository.getUserById(newUser.getUserId()));
    }

    @Test
    public void updateUserTest() {
        User newUser = new User("TEST", "TEST", "TEST", "TEST", "TEST", "TEST", new Timestamp(System.currentTimeMillis()), "CUSTOMER");
        userRepository.createUser(newUser);

        User newUser2 = new User("TEST2", "TEST2", "TEST2", "TEST2", "TEST2", "TEST2", new Timestamp(System.currentTimeMillis()), "CUSTOMER");
        userRepository.updateUser(newUser, newUser2);

        User searchedUser = userRepository.getUserById(newUser.getUserId());
        assertEquals("TEST2", searchedUser.getUsername());

        userRepository.deleteUser(newUser.getUserId());
    }
}
