package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.AdminController;
import manager.SessionManager;
import model.User;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class AdminView extends JPanel {
    private final AdminController adminController;

    // Initialize the Admin Panel page
    public AdminView() {
        adminController = new AdminController(this);

        init();
    }

    private void init() {
        // Use the MigLayout to control how the page looks like
        setLayout(new MigLayout("fill,insets 20", "[grow,fill]", "[]"));

        // Get the logged in user
        User loggedInUser = SessionManager.getLoggedInUser();

        // Check if the logged in user is an administrator
        if (loggedInUser != null && loggedInUser.getRole().equals("ADMINISTRATOR")) {
            // Create the navbar and its label
            JPanel navbar = new JPanel(new MigLayout("fillx,insets 20", "[left][right]", "[top]"));

            JLabel productsLabel = new JLabel("Products");
            JLabel basketLabel = new JLabel("Basket");
            JLabel ordersLabel = new JLabel("Your orders");

            productsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            basketLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            ordersLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            productsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    adminController.seeProducts();
                }
            });

            basketLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    adminController.seeBasket();
                }
            });

            ordersLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    adminController.seeOrders();
                }
            });

            navbar.add(productsLabel, "cell 0 0");
            navbar.add(basketLabel, "cell 0 0");
            navbar.add(ordersLabel, "cell 1 0");

            navbar.putClientProperty(FlatClientProperties.STYLE,
                    "arc:20;" +
                            "[light]background:darken(@background,3%);" +
                            "[dark]background:lighten(@background,3%)");

            add(navbar, "growx, wrap");

            // Create the scroll content panel
            JPanel scrollContentPanel = new JPanel();
            scrollContentPanel.setLayout(new MigLayout("fillx, insets 20", "[grow,fill]", "[]"));

            // Create the "add new clothing", "add new brand", "add new category" panels
            JPanel clothingPanel = createClothingPanel();
            JPanel brandPanel = createBrandPanel();
            JPanel categoryPanel = createCategoryPanel();

            // Add the created panels to the scroll content panel
            scrollContentPanel.add(clothingPanel, "growx, wrap");
            scrollContentPanel.add(brandPanel, "growx, wrap");
            scrollContentPanel.add(categoryPanel, "growx, wrap");

            // Create the scroll
            JScrollPane scrollPane = new JScrollPane(scrollContentPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getVerticalScrollBar().setBlockIncrement(32);

            // Add the scroll to the view
            add(scrollPane, "grow, push");
        }

        // If the logged in user isn't an administrator, redirect him to the products page
        else {
            adminController.seeProducts();
        }
    }

    private JPanel createClothingPanel() {
        JTextField clothingName = new JTextField();
        JTextField clothingCategory = new JTextField();
        JTextField clothingBrand = new JTextField();
        JTextField clothingDescription = new JTextField();
        JTextField clothingImageURL = new JTextField();
        JTextField clothingPrice = new JTextField();
        JTextField clothingStockQuantity = new JTextField();

        JButton cmdAddClothing = new JButton("Add a new clothing");

        cmdAddClothing.addActionListener(e -> adminController.addClothing(clothingName.getText(),
                clothingCategory.getText(),
                clothingBrand.getText(),
                clothingDescription.getText(),
                clothingImageURL.getText(),
                clothingPrice.getText(),
                clothingStockQuantity.getText()));

        clothingName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter clothing's name");
        clothingCategory.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter clothing's category name");
        clothingBrand.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter clothing's brand name");
        clothingDescription.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter clothing's description");
        clothingImageURL.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter clothing's image URL | Input example: /image/your_image.png");
        clothingPrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter clothing's price | Input example: 29.99");
        clothingStockQuantity.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter clothing's stock quantity | Input example: 5");

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "fill,250:280"));

        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        JLabel lbTitle = new JLabel("Add a new clothing");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");

        panel.add(lbTitle);
        panel.add(new JLabel("Clothing name"), "gapy 8");
        panel.add(clothingName);
        panel.add(new JLabel("Clothing category"), "gapy 8");
        panel.add(clothingCategory);
        panel.add(new JLabel("Clothing brand"), "gapy 8");
        panel.add(clothingBrand);
        panel.add(new JLabel("Clothing description"), "gapy 8");
        panel.add(clothingDescription);
        panel.add(new JLabel("Clothing image URL"), "gapy 8");
        panel.add(clothingImageURL);
        panel.add(new JLabel("Clothing price"), "gapy 8");
        panel.add(clothingPrice);
        panel.add(new JLabel("Clothing stock quantity"), "gapy 8");
        panel.add(clothingStockQuantity);
        panel.add(cmdAddClothing);

        return panel;
    }

    private JPanel createBrandPanel() {
        JTextField txtBrandName = new JTextField();
        JTextField txtBrandDescription = new JTextField();
        JButton cmdAddBrand = new JButton("Add a new clothing brand");

        cmdAddBrand.addActionListener(e -> adminController.addBrand(txtBrandName.getText(), txtBrandDescription.getText()));

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "fill,250:280"));

        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        txtBrandName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter brand's name");
        txtBrandDescription.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter brand's description");

        JLabel lbTitle = new JLabel("Add a new brand");

        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");

        panel.add(lbTitle);
        panel.add(new JLabel("Brand name"), "gapy 8");
        panel.add(txtBrandName);
        panel.add(new JLabel("Brand description"), "gapy 8");
        panel.add(txtBrandDescription);
        panel.add(cmdAddBrand, "gapy 10");

        return panel;
    }

    private JPanel createCategoryPanel() {
        JTextField txtCategoryName = new JTextField();
        JTextField txtCategoryDescription = new JTextField();
        JButton cmdAddBrand = new JButton("Add a new clothing category");

        cmdAddBrand.addActionListener(e -> adminController.addCategory(txtCategoryName.getText(), txtCategoryDescription.getText()));

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "fill,250:280"));

        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        txtCategoryName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter category's name");
        txtCategoryDescription.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter category's description");

        JLabel lbTitle = new JLabel("Add a new category");

        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");

        panel.add(lbTitle);
        panel.add(new JLabel("Category name"), "gapy 8");
        panel.add(txtCategoryName);
        panel.add(new JLabel("Category description"), "gapy 8");
        panel.add(txtCategoryDescription);
        panel.add(cmdAddBrand, "gapy 10");

        return panel;
    }
}
