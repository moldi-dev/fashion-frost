package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.RegisterController;
import manager.FormsManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JPanel {
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JTextField txtShippingAddress;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton cmdRegister;
    private final RegisterController registerController;

    // Initialize the register page
    public RegisterView() {
        registerController = new RegisterController(this);
        init();
    }

    public JTextField getTxtFirstName() {
        return this.txtFirstName;
    }

    public JTextField getTxtLastName() {
        return this.txtLastName;
    }

    public JTextField getTxtUsername() {
        return this.txtUsername;
    }

    public JTextField getTxtEmail() {
        return this.txtEmail;
    }

    public JTextField getTxtShippingAddress() {
        return this.txtShippingAddress;
    }

    public JPasswordField getTxtPassword() {
        return this.txtPassword;
    }

    public JPasswordField getTxtConfirmPassword() {
        return this.txtConfirmPassword;
    }

    public JButton getCmdRegister() {
        return this.cmdRegister;
    }

    private void init() {
        // Use MigLayout to control how the register page looks like
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));

        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtUsername = new JTextField();
        txtEmail = new JTextField();
        txtShippingAddress = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();
        cmdRegister = new JButton("Sign Up");

        cmdRegister.addActionListener(e -> registerController.register());

        // Create the panel which will contain all the fields
        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "[fill,360]"));
        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        txtFirstName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your first name");
        txtLastName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your last name");
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
        txtShippingAddress.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your address");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        txtConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirm your password");

        txtPassword.putClientProperty(FlatClientProperties.STYLE,
                "showRevealButton:true");

        txtConfirmPassword.putClientProperty(FlatClientProperties.STYLE,
                "showRevealButton:true");

        cmdRegister.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,10%);" +
                        "[dark]background:lighten(@background,10%);" +
                        "borderWidth:0;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0");

        JLabel lbTitle = new JLabel("Welcome to Fashion Frost!");
        JLabel description = new JLabel("Join us to buy the best clothes on the market at the best prices. Sign up now and start shopping!");

        lbTitle.putClientProperty(FlatClientProperties.STYLE,
                "font:bold +10");

        description.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                        "[dark]foreground:darken(@foreground,30%)");

        panel.add(lbTitle);
        panel.add(description);

        panel.add(new JLabel("Full name"), "gapy 10");
        panel.add(txtFirstName, "split 2");
        panel.add(txtLastName);

        panel.add(new JLabel("Username"), "gapy 8");
        panel.add(txtUsername);

        panel.add(new JLabel("Email"), "gapy 8");
        panel.add(txtEmail);

        panel.add(new JLabel("Address"), "gapy 8");
        panel.add(txtShippingAddress);

        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(txtPassword);

        panel.add(new JLabel("Confirm password"), "gapy 0");
        panel.add(txtConfirmPassword);

        panel.add(cmdRegister, "gapy 20");
        panel.add(createLoginLabel(), "gapy 10");
        add(panel);
    }

    // Create the login label, which when clicked, will redirect the user to the log in page
    private Component createLoginLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        panel.putClientProperty(FlatClientProperties.STYLE,
                "background:null");

        JButton cmdLogin = new JButton("<html><a href=\"#\">Sign in here</a></html>");

        cmdLogin.putClientProperty(FlatClientProperties.STYLE,
                "border:3,3,3,3");
        cmdLogin.setContentAreaFilled(false);

        cmdLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cmdLogin.addActionListener(e ->
        {
            FormsManager.getInstance().showForm(new LoginView());
        });

        JLabel label = new JLabel("Already have an account ?");

        label.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                        "[dark]foreground:darken(@foreground,30%)");

        panel.add(label);
        panel.add(cmdLogin);

        return panel;
    }

    public boolean arePasswordsMatching() {
        String password = String.valueOf(txtPassword.getPassword());
        String confirmPassword = String.valueOf(txtConfirmPassword.getPassword());

        return password.equals(confirmPassword);
    }
}
