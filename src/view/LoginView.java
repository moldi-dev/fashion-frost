package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.LoginController;
import manager.FormsManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JButton cmdLogin;
    private final LoginController loginController;

    // Initialize the login page
    public LoginView() {
        loginController = new LoginController(this);
        init();
    }

    public JTextField getTxtUsername() {
        return this.txtUsername;
    }

    public JTextField getTxtPassword() {
        return this.txtPassword;
    }

    public JButton getCmdLogin() {
        return this.cmdLogin;
    }

    private void init() {
        // Use MigLayout to control how the login page looks like
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cmdLogin = new JButton("Login");

        cmdLogin.addActionListener(e -> loginController.login());

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "fill,250:280"));

        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");

        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        JLabel lbTitle = new JLabel("Welcome back!");
        JLabel description = new JLabel("Please sign in to access your account");

        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");

        description.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                        "[dark]foreground:darken(@foreground,30%)");

        panel.add(lbTitle);
        panel.add(description);
        panel.add(new JLabel("Username"), "gapy 8");
        panel.add(txtUsername);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(txtPassword);
        panel.add(cmdLogin, "gapy 10");

        panel.add(createSignUpLabel(), "gapy 10");

        add(panel);
    }

    // Creates the sign up label which when clicked redirects the user to the registration page
    private Component createSignUpLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        panel.putClientProperty(FlatClientProperties.STYLE,
                "background:null");

        JButton cmdSignUp = new JButton("<html><a href=\"#\">Sign up</a></html>");

        cmdSignUp.putClientProperty(FlatClientProperties.STYLE,
                "border:3,3,3,3");

        cmdSignUp.setContentAreaFilled(false);

        cmdSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cmdSignUp.addActionListener(e ->
        {
            FormsManager.getInstance().showForm(new RegisterView());
        });

        JLabel label = new JLabel("Don't have an account ?");
        label.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                        "[dark]foreground:darken(@foreground,30%)");

        panel.add(label);
        panel.add(cmdSignUp);

        return panel;
    }
}
