package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.ClothingController;
import manager.SessionManager;
import model.Clothing;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ClothingView extends JPanel {
    private Clothing clothing;
    private ClothingController clothingController;
    private JLabel lblImage;
    private JLabel lblName;
    private JLabel lblPrice;
    private JLabel lblCategory;
    private JLabel lblBrand;
    private JLabel lblDescription;
    private JButton addToBasketButton;

    // Initialize the clothing page for the given clothing name
    public ClothingView(String clothingName) {
        clothingController = new ClothingController(this);

        // Get the clothing we're generating this page for
        clothing = clothingController.getClothingRepository().getClothingByExactName(clothingName);

        init();
    }

    private void init() {
        // Use MigLayout to control how the clothing page looks like
        setLayout(new MigLayout("fill,insets 20", "[grow,fill]", "[grow,fill]"));

        // Add the navbar with its labels
        JPanel navbar = new JPanel(new MigLayout("fillx,insets 20", "[left][right]", "[top]"));

        JLabel productsLabel = new JLabel("Products");
        JLabel basketLabel = new JLabel("Basket");
        JLabel ordersLabel = new JLabel("Your orders");
        JLabel adminLabel = new JLabel("Admin Panel");

        productsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        basketLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ordersLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        adminLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        productsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clothingController.seeProducts();
            }
        });

        basketLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clothingController.seeBasket();
            }
        });

        ordersLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clothingController.seeOrders();
            }
        });

        adminLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clothingController.seeAdmin();
            }
        });

        navbar.add(productsLabel, "cell 0 0");
        navbar.add(basketLabel, "cell 0 0");

        // Check if the user is logged in and if he's an administrator
        // If so, add the admin panel label
        if (SessionManager.getLoggedInUser() != null && SessionManager.getLoggedInUser().getRole().equals("ADMINISTRATOR")) {
            navbar.add(adminLabel, "cell 1 0");
        }

        navbar.add(ordersLabel, "cell 1 0");

        navbar.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        add(navbar, "wrap");

        // Check if the clothing returned by the repo isn't null
        if (clothing != null) {
            // Design how the clothing panel looks like
            JPanel productDetailsPanel = new JPanel(new MigLayout("wrap, insets 35 45 30 45", "[fill, 120]"));
            lblImage = new JLabel(new ImageIcon(getResizedImage(clothing.getImageUrl(), 200, 325)));
            lblName = new JLabel("Name: " + clothing.getName());
            lblCategory = new JLabel("Category: " + clothingController.getCategoryRepository().getCategoryByClothingName(clothing.getName()).getName());
            lblBrand = new JLabel("Brand: " + clothingController.getBrandRepository().getBrandByClothingName(clothing.getName()).getName());
            lblDescription = new JLabel("Description: " + clothing.getDescription());
            lblPrice = new JLabel("Price: " + clothing.getPrice() + "$");
            addToBasketButton = new JButton("Add to Basket");

            addToBasketButton.setEnabled(clothing.getStockQuantity() > 0);

            addToBasketButton.addActionListener(e ->
            {
                clothingController.addClothingToBasket(clothing.getName());
            });

            productDetailsPanel.add(lblImage, "cell 5 0, span 5");
            productDetailsPanel.add(lblPrice, "cell 5 5");
            productDetailsPanel.add(lblName, "cell 5 6");
            productDetailsPanel.add(lblCategory, "cell 5 7");
            productDetailsPanel.add(lblBrand, "cell 5 8");
            productDetailsPanel.add(lblDescription, "cell 5 9");
            productDetailsPanel.add(addToBasketButton, "cell 5 10");

            productDetailsPanel.putClientProperty(FlatClientProperties.STYLE,
                    "arc:20;" +
                            "[light]background:darken(@background,3%);" +
                            "[dark]background:lighten(@background,3%)");

            add(productDetailsPanel, "center");
        }
    }

    public Clothing getClothing() {
        return clothing;
    }

    public void setClothing(Clothing clothing) {
        this.clothing = clothing;
    }

    // Retrieves a resized image of a given image
    private Image getResizedImage(String imageUrl, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(imageUrl)));
        return originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
    }
}
