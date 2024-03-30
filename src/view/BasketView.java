package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.BasketController;
import manager.SessionManager;
import model.Clothing;
import model.User;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class BasketView extends JPanel {
    private final BasketController basketController;
    private List<Clothing> clothingList;
    private JPanel clothingPanelContainer;
    private JLabel totalPriceToPay;
    private JButton placeOrderButton;

    // Initialize the basket page
    public BasketView() {
        basketController = new BasketController(this);

        init();
    }

    public JLabel getTotalPriceToPay() {
        return totalPriceToPay;
    }

    public void setTotalPriceToPay(JLabel totalPriceToPay) {
        this.totalPriceToPay = totalPriceToPay;
    }

    public JButton getPlaceOrderButton() {
        return placeOrderButton;
    }

    public void setPlaceOrderButton(JButton placeOrderButton) {
        this.placeOrderButton = placeOrderButton;
    }

    public List<Clothing> getClothingList() {
        return clothingList;
    }

    private void init() {
        // Use the MigLayout to control how the basket page looks like
        setLayout(new MigLayout("fill,insets 20", "[grow,fill]", "[]"));

        // Get the currently logged in user
        User loggedInUser = SessionManager.getLoggedInUser();

        // Check if the user is logged in
        if (loggedInUser != null) {
            // Create the navbar with its labels
            JPanel navbar = new JPanel(new MigLayout("fillx,insets 20", "[left][right]", "[top]"));

            JLabel productsLabel = new JLabel("Products");
            JLabel ordersLabel = new JLabel("Your orders");
            JLabel adminLabel = new JLabel("Admin Panel");

            productsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            ordersLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            adminLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            productsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    basketController.seeProducts();
                }
            });

            ordersLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    basketController.seeOrders();
                }
            });

            adminLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    basketController.seeAdmin();
                }
            });

            navbar.add(productsLabel, "cell 0 0");

            // If the user is an administrator, add the admin label to the navbar
            if (loggedInUser.getRole().equals("ADMINISTRATOR")) {
                navbar.add(adminLabel, "cell 1 0");
            }

            navbar.add(ordersLabel, "cell 1 0");

            navbar.putClientProperty(FlatClientProperties.STYLE,
                    "arc:20;" +
                            "[light]background:darken(@background,3%);" +
                            "[dark]background:lighten(@background,3%)");

            add(navbar, "growx, wrap");

            JLabel headerLabel = new JLabel("All the products currently in your basket:");
            headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 20));

            totalPriceToPay = new JLabel("Total price to pay: $" + basketController.getBasketRepository().getTotalPriceToPayForUser(loggedInUser.getUserId()));
            totalPriceToPay.setFont(totalPriceToPay.getFont().deriveFont(Font.BOLD, 20));

            placeOrderButton = new JButton("Place order");
            placeOrderButton.addActionListener(e -> basketController.placeOrder());
            placeOrderButton.setEnabled(basketController.getBasketRepository().getTotalPriceToPayForUser(loggedInUser.getUserId()) > 0);

            // Create the clothing panel
            clothingPanelContainer = new JPanel(new MigLayout("wrap 3, fillx", "[fill,120]", "[grow,fill]"));

            // Get the items in the currently logged in user's basket
            clothingList = basketController.getBasketRepository().getClothingsInBasketForUser(SessionManager.getLoggedInUser().getUserId());

            // Add each clothing to the clothing panel
            for (Clothing clothing : clothingList) {
                JPanel clothingPanel = createClothingPanel(clothing);
                clothingPanelContainer.add(clothingPanel);
            }

            // Add the scroll pane
            JScrollPane scrollPane = new JScrollPane(clothingPanelContainer);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getVerticalScrollBar().setBlockIncrement(32);

            // Add the panel which will contain all the necessary information
            JPanel panel = new JPanel(new MigLayout("wrap 3, fillx, insets 35 45 30 45", "[fill,120]", "[grow,fill]"));
            panel.putClientProperty(FlatClientProperties.STYLE,
                    "arc:20;" +
                            "[light]background:darken(@background,3%);" +
                            "[dark]background:lighten(@background,3%)");

            panel.add(totalPriceToPay, "span 5, center");
            panel.add(headerLabel, "span 5, center, wrap");
            panel.add(scrollPane, "span 5, grow, push");
            panel.add(placeOrderButton, "span 5, center");

            add(panel, "growx");
        }

        // If the user isn't logged in, redirect him to the products page
        else {
            basketController.seeProducts();
        }
    }

    private JPanel createClothingPanel(Clothing clothing) {
        JPanel clothingPanel = new JPanel(new MigLayout("fill", "[center]", "[center]"));

        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(clothing.getImageUrl())));
        Image resizedImage = originalIcon.getImage().getScaledInstance(200, 325, Image.SCALE_AREA_AVERAGING);
        JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));

        JLabel nameLabel = new JLabel(clothing.getName());
        JLabel priceLabel = new JLabel("Price: $" + clothing.getPrice());

        JButton deleteProduct = new JButton("Remove from basket");
        deleteProduct.addActionListener(e -> basketController.deleteProduct(clothing.getName()));

        clothingPanel.add(imageLabel, "wrap");
        clothingPanel.add(nameLabel, "wrap");
        clothingPanel.add(priceLabel, "wrap");
        clothingPanel.add(deleteProduct);

        clothingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        clothingPanel.setPreferredSize(new Dimension(300, 450));

        return clothingPanel;
    }

    public void updateClothingPanel() {
        clothingPanelContainer.removeAll();

        for (Clothing clothing : clothingList) {
            JPanel clothingPanel = createClothingPanel(clothing);
            clothingPanelContainer.add(clothingPanel);
        }

        clothingPanelContainer.revalidate();
        clothingPanelContainer.repaint();
    }
}
