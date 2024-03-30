package controller;

import manager.FormsManager;
import manager.SessionManager;
import model.User;
import repository.UserRepository;
import view.LoginView;
import view.ShopView;

import javax.swing.*;

public class LoginController {
    private final LoginView loginView;

    // Initialize the controller
    public LoginController(LoginView loginView) {
        this.loginView = loginView;
    }

    public void login() {
        // Get the username and password provided in the login form
        String username = loginView.getTxtUsername().getText();
        String password = String.valueOf(loginView.getTxtPassword().getText());

        UserRepository userRepository = new UserRepository();

        // Get the data from the database for the user by the given username in the login form
        User user = userRepository.getUserByUsername(username);

        // Check if the username and password correspond to the result extracted above from the database
        if (user != null && user.getPassword().equals(password) && user.getUsername().equals(username)) {
            // If they correspond, set the result user as logged in
            SessionManager.setLoggedInUser(user);

            // Redirect the user to the shop
            FormsManager.getInstance().showForm(new ShopView());
        }

        // If the username or password is incorrect, prevent the user from logging in
        else {
            showErrorMessage("Invalid username or password.");
        }
    }

    private void showInformationMessage(String message) {
        JOptionPane.showMessageDialog(
                loginView,
                message,
                "Fashion Frost",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                loginView,
                message,
                "Fashion Frost",
                JOptionPane.ERROR_MESSAGE);
    }
}
