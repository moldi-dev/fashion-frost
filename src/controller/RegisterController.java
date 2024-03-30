package controller;

import manager.FormsManager;
import model.User;
import repository.UserRepository;
import view.LoginView;
import view.RegisterView;

import javax.swing.*;
import java.sql.Timestamp;

public class RegisterController {
    private final RegisterView registerView;
    private final UserRepository userRepository;

    // Initialize the controller
    public RegisterController(RegisterView registerView) {
        this.registerView = registerView;
        userRepository = new UserRepository();

    }

    public void register() {
        // Validate the given input in the registration form
        if (validateInput() && !usernameAlreadyExists()) {
            // If the input provided in the registration form is valid, extract it
            // and create a new user object with the given data
            String firstName = registerView.getTxtFirstName().getText();
            String lastName = registerView.getTxtLastName().getText();
            String username = registerView.getTxtUsername().getText();
            String email = registerView.getTxtEmail().getText();
            String shippingAddress = registerView.getTxtShippingAddress().getText();
            String password = String.valueOf(registerView.getTxtPassword().getPassword());

            User newUser = new User(
                    firstName,
                    lastName,
                    username,
                    email,
                    password,
                    shippingAddress,
                    new Timestamp(System.currentTimeMillis()),
                    "CUSTOMER"
            );

            UserRepository userRepository = new UserRepository();
            userRepository.createUser(newUser);

            showInformationMessage("User registration successful! Please log in.");

            FormsManager.getInstance().showForm(new LoginView());

        }

        // If the given username already exists, prevent the user from creating a new account
        else if (usernameAlreadyExists()) {
            showErrorMessage("This username is already taken!");
        }
    }

    // Checks if the provided username in the registration form is already in the database
    private boolean usernameAlreadyExists() {
        String inputUsername = registerView.getTxtUsername().getText();
        User databaseFoundUser = userRepository.getUserByUsername(inputUsername);

        return databaseFoundUser != null && databaseFoundUser.getUsername().equals(inputUsername);
    }

    private boolean validateInput() {
        // Regular expressions for validation
        String firstNameRegex = "^[A-Z][a-zA-Z'\\-\\s]*$";
        String lastNameRegex = "^[A-Z][a-zA-Z']*$";
        String usernameRegex = "^[a-zA-Z0-9]+$";
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        String addressRegex = "^[a-zA-Z0-9\\s]+$";

        // Validate first name
        if (!registerView.getTxtFirstName().getText().matches(firstNameRegex)) {
            showErrorMessage("Invalid first name. Please enter a valid first name.");
            return false;
        }

        // Validate last name
        else if (!registerView.getTxtLastName().getText().matches(lastNameRegex)) {
            showErrorMessage("Invalid last name. Please enter a valid last name.");
            return false;
        }

        // Validate username
        else if (!registerView.getTxtUsername().getText().matches(usernameRegex)) {
            showErrorMessage("Invalid username. Please use only letters and digits for the username.");
            return false;
        }

        // Validate username 2
        else if (registerView.getTxtUsername().getText().length() < 6) {
            showErrorMessage("Username must be at least 6 characters long.");
            return false;
        }

        // Validate email
        else if (!registerView.getTxtEmail().getText().matches(emailRegex)) {
            showErrorMessage("Invalid email. Please enter a valid email address (e.g., email@example.com).");
            return false;
        }

        // Validate shipping address
        else if (!registerView.getTxtShippingAddress().getText().matches(addressRegex)) {
            showErrorMessage("Invalid address. Please use only letters, numbers, and spaces for the address.");
            return false;
        }

        // Validate password length
        else if (registerView.getTxtPassword().getPassword().length < 6) {
            showErrorMessage("Password must be at least 6 characters long.");
            return false;
        }

        // Validate password matching
        else if (!registerView.arePasswordsMatching()) {
            showErrorMessage("Passwords do not match.");
            return false;
        }

        return true;
    }

    private void showInformationMessage(String message) {
        JOptionPane.showMessageDialog(
                registerView,
                message,
                "Fashion Frost",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                registerView,
                message,
                "Fashion Frost",
                JOptionPane.ERROR_MESSAGE);
    }
}
