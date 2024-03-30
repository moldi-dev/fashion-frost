package view;

import com.formdev.flatlaf.FlatClientProperties;
import controller.OrderController;
import manager.SessionManager;
import model.Clothing;
import model.User;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class OrderView extends JPanel {
    private final OrderController orderController;
    private JPanel clothingPanelContainer;
    private List<Clothing> clothingList;

    // Initialize the repo
    public OrderView() {
        orderController = new OrderController(this);

        init();
    }

    private void init() {
        // Use MigLayout to control how the orders page looks like
        setLayout(new MigLayout("fill,insets 20", "[grow,fill]", "[]"));

        // Get the currently logged in user
        User loggedInUser = SessionManager.getLoggedInUser();

        // Check if the user is logged in
        if (loggedInUser != null) {
            // Add the navbar and its labels
            JPanel navbar = new JPanel(new MigLayout("fillx,insets 20", "[left][right]", "[top]"));

            JLabel productsLabel = new JLabel("Products");
            JLabel basketLabel = new JLabel("Basket");
            JLabel adminLabel = new JLabel("Admin Panel");

            adminLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            adminLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    orderController.seeAdmin();
                }
            });

            productsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            basketLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            productsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    orderController.seeProducts();
                }
            });

            basketLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    orderController.seeBasket();
                }
            });

            navbar.add(productsLabel, "cell 0 0");

            // If the logged in user is an administrator, add the admin label
            if (loggedInUser.getRole().equals("ADMINISTRATOR")) {
                navbar.add(adminLabel, "cell 1 0");
            }

            navbar.add(basketLabel, "cell 0 0");

            navbar.putClientProperty(FlatClientProperties.STYLE,
                    "arc:20;" +
                            "[light]background:darken(@background,3%);" +
                            "[dark]background:lighten(@background,3%)");

            add(navbar, "growx, wrap");

            JLabel headerLabel = new JLabel("All your ordered products:");
            headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 20));

            // Create the clothing panel container
            clothingPanelContainer = new JPanel(new MigLayout("wrap 3, fillx", "[fill,120]"));

            // Retrieve every clothing ordered by the logged in user
            clothingList = orderController.getOrderRepository().getClothingsInOrdersForUser(SessionManager.getLoggedInUser().getUserId());

            // Add to the clothing panel each clothing in the list retrieved above
            for (Clothing clothing : clothingList) {
                JPanel clothingPanel = createClothingPanel(clothing);
                clothingPanelContainer.add(clothingPanel);
            }

            // Add the scroll pane
            JScrollPane scrollPane = new JScrollPane(clothingPanelContainer);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getVerticalScrollBar().setBlockIncrement(32);

            // Add the main panel which will contain the scrolling and each product ordered
            JPanel panel = new JPanel(new MigLayout("wrap 3, fillx, insets 35 45 30 45", "[fill,120]", "[grow,fill]"));
            panel.putClientProperty(FlatClientProperties.STYLE,
                    "arc:20;" +
                            "[light]background:darken(@background,3%);" +
                            "[dark]background:lighten(@background,3%)");

            panel.add(headerLabel, "span 5, center, wrap");
            panel.add(scrollPane, "span 5, grow, push");

            add(panel, "growx");
        }

        // If the user isn't logged in, redirect him to the products page
        else {
            orderController.seeProducts();
        }
    }

    private JPanel createClothingPanel(Clothing clothing) {
        JPanel clothingPanel = new JPanel(new MigLayout("fill", "[center]", "[center]"));

        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(clothing.getImageUrl())));
        Image resizedImage = originalIcon.getImage().getScaledInstance(200, 325, Image.SCALE_AREA_AVERAGING);
        JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));

        JLabel nameLabel = new JLabel(clothing.getName());
        JLabel categoryLabel = new JLabel("Category: " + orderController.getCategoryRepository().getCategoryByClothingName(clothing.getName()).getName());
        JLabel brandLabel = new JLabel("Brand: " + orderController.getBrandRepository().getBrandByClothingName(clothing.getName()).getName());
        JLabel orderLabel = new JLabel("Ordered on: " + orderController.getOrderRepository().getOrderDateForUserOrder(SessionManager.getLoggedInUser().getUserId(), clothing.getClothingId()));
        JLabel statusLabel = new JLabel("Status: " + orderController.getOrderRepository().getOrderStatusForUserOrder(SessionManager.getLoggedInUser().getUserId(), clothing.getClothingId()));

        clothingPanel.add(imageLabel, "wrap");
        clothingPanel.add(nameLabel, "wrap");
        clothingPanel.add(categoryLabel, "wrap");
        clothingPanel.add(brandLabel, "wrap");
        clothingPanel.add(orderLabel, "wrap");
        clothingPanel.add(statusLabel);

        clothingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        clothingPanel.setPreferredSize(new Dimension(300, 450));

        return clothingPanel;
    }
}
