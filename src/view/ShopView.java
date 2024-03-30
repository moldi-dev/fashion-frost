package view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import controller.ShopController;
import manager.SessionManager;
import model.Brand;
import model.Category;
import model.Clothing;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShopView extends JPanel {
    private List<Clothing> clothingList;
    private final ShopController shopController;
    private JPanel clothingPanelContainer;
    private final List<Brand> brandList;
    private final List<Category> categoryList;
    private List<JCheckBox> categoryCheckBoxes;
    private final List<JRadioButton> orderByRadioButtons;
    private List<JCheckBox> brandCheckBoxes;

    // Initialize the view
    public ShopView() {
        clothingList = new ArrayList<>();
        orderByRadioButtons = new ArrayList<>();
        shopController = new ShopController(this);

        // Get every category and every brand from the database
        categoryList = shopController.getCategoryRepository().getAllCategories();
        brandList = shopController.getBrandRepository().getAllBrands();

        init();
    }

    public List<Clothing> getClothingList() {
        return this.clothingList;
    }

    public void setClothingList(List<Clothing> clothingList) {
        this.clothingList = clothingList;
    }

    private void init() {
        // Use MigLayout to control how the shop page looks like
        setLayout(new MigLayout("fill,insets 20", "[grow,fill]", "[]"));

        // Add the navbar and its labels
        JPanel navbar = new JPanel(new MigLayout("fillx,insets 20", "[left][right]", "[top]"));

        JLabel basketLabel = new JLabel("Basket");
        JLabel orderLabel = new JLabel("Your orders");
        JLabel adminLabel = new JLabel("Admin Panel");

        adminLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        adminLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                shopController.seeAdmin();
            }
        });

        basketLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        basketLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                shopController.seeBasket();
            }
        });

        orderLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        orderLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                shopController.seeOrders();
            }
        });

        navbar.add(basketLabel, "cell 0 0");

        // If the user is logged in and he's an administrator, add the admin label to the navbar
        if (SessionManager.getLoggedInUser() != null && SessionManager.getLoggedInUser().getRole().equals("ADMINISTRATOR")) {
            navbar.add(adminLabel, "cell 1 0");
        }

        navbar.add(orderLabel, "cell 1 0");

        navbar.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        add(navbar, "growx, wrap");

        JLabel headerLabel = new JLabel("All our products:");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 20));

        // Create the searchbar
        JTextField searchBar = new JTextField();
        searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Type in to search products by name");
        searchBar.setColumns(15);
        searchBar.setBorder(new CompoundBorder(searchBar.getBorder(), new FlatEmptyBorder(0, 0, 5, 0)));

        // Create the order by layout
        JLabel orderByLabel = new JLabel("Order by:");
        JPanel orderByPanel = new JPanel(new MigLayout("fillx"));

        orderByPanel.add(orderByLabel);

        // Add the sorting types to the sorting list
        ButtonGroup orderByButtonGroup = new ButtonGroup();
        JRadioButton noneRadioButton = new JRadioButton("None");
        noneRadioButton.setSelected(true); // Select "None" by default

        // Add the sorting types to the sorting list and group them
        orderByRadioButtons.add(noneRadioButton);
        orderByRadioButtons.add(new JRadioButton("A - Z"));
        orderByRadioButtons.add(new JRadioButton("Z - A"));
        orderByRadioButtons.add(new JRadioButton("Newest"));
        orderByRadioButtons.add(new JRadioButton("Oldest"));
        orderByRadioButtons.add(new JRadioButton("Price ascending"));
        orderByRadioButtons.add(new JRadioButton("Price descending"));

        for (JRadioButton radioButton : orderByRadioButtons) {
            orderByButtonGroup.add(radioButton);
            radioButton.addActionListener(e -> shopController.applyFilters(searchBar, categoryCheckBoxes, brandCheckBoxes, orderByRadioButtons));
            orderByPanel.add(radioButton);
        }

        // Create the filter by layout
        JLabel filterByCategory = new JLabel("Filter by category:");
        JLabel filterByBrand = new JLabel("Filter by brand:");
        categoryCheckBoxes = new ArrayList<>();
        brandCheckBoxes = new ArrayList<>();

        for (Category category : categoryList) {
            JCheckBox categoryCheckBoxButton = new JCheckBox(category.getName());
            categoryCheckBoxButton.addActionListener(e -> shopController.applyFilters(searchBar, categoryCheckBoxes, brandCheckBoxes, orderByRadioButtons));
            categoryCheckBoxes.add(categoryCheckBoxButton);
        }

        for (Brand brand : brandList) {
            JCheckBox brandCheckBoxButton = new JCheckBox(brand.getName());
            brandCheckBoxButton.addActionListener(e -> shopController.applyFilters(searchBar, categoryCheckBoxes, brandCheckBoxes, orderByRadioButtons));
            brandCheckBoxes.add(brandCheckBoxButton);
        }

        JPanel filterByCategoryPanel = new JPanel(new MigLayout("fillx"));
        JPanel filterByBrandPanel = new JPanel(new MigLayout("fillx"));
        filterByCategoryPanel.add(filterByCategory);
        categoryCheckBoxes.forEach(filterByCategoryPanel::add);
        filterByBrandPanel.add(filterByBrand);
        brandCheckBoxes.forEach(filterByBrandPanel::add);

        // Create a timer for the filter search
        // The timer starts counting down from 350 ms when the user stops inputting characters
        // in the search bar
        // I designed it this way because I was tired of pressing the ENTER key whenever I was
        // done typing in names for the search bar
        Timer timer = new Timer(350, e -> shopController.applyFilters(searchBar, categoryCheckBoxes, brandCheckBoxes, orderByRadioButtons));
        timer.setRepeats(false);

        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        // Create the main clothing panel
        clothingPanelContainer = new JPanel(new MigLayout("wrap 3, fillx", "[fill,120]", "[grow,fill]"));

        // Retrieves all clothings which are in stock (stock quantity > 0)
        clothingList = shopController.getClothingRepository().getAllClothingInStock();

        // Add each clothing to the main clothing panel
        for (Clothing clothing : clothingList) {
            JPanel clothingPanel = createClothingPanel(clothing);
            clothingPanelContainer.add(clothingPanel);
        }

        // Create the scroll pane
        JScrollPane scrollPane = new JScrollPane(clothingPanelContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(32);

        // Add the main panel which will contain the clothing panel and the scroll pane
        JPanel panel = new JPanel(new MigLayout("wrap 3, fillx, insets 35 45 30 45", "[fill,120]"));
        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        panel.add(headerLabel, "span 5, center, wrap");
        panel.add(searchBar, "span 5, center, wrap");
        panel.add(filterByCategoryPanel, "span 5, center, wrap");
        panel.add(filterByBrandPanel, "span 5, center, wrap");
        panel.add(orderByPanel, "span 5, center, wrap");
        panel.add(scrollPane, "span 5, grow, push");

        add(panel, "growx");
    }

    private JPanel createClothingPanel(Clothing clothing) {
        JPanel clothingPanel = new JPanel(new MigLayout("fill", "[center]", "[center]"));

        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(clothing.getImageUrl())));
        Image resizedImage = originalIcon.getImage().getScaledInstance(200, 325, Image.SCALE_AREA_AVERAGING);
        JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));

        JLabel nameLabel = new JLabel(clothing.getName());
        JLabel priceLabel = new JLabel("$" + clothing.getPrice());

        JButton seeClothingProduct = new JButton("See product");
        seeClothingProduct.addActionListener(e -> shopController.seeClothing(clothing.getName()));

        clothingPanel.add(imageLabel, "wrap");
        clothingPanel.add(nameLabel, "wrap");
        clothingPanel.add(priceLabel, "wrap");
        clothingPanel.add(seeClothingProduct);

        clothingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        clothingPanel.setPreferredSize(new Dimension(300, 450));

        return clothingPanel;
    }

    // Updates the clothing panel
    // Used for the search bar
    // If an user types in say "Sweater", a query is run which retrieves
    // every product which contains the name "Sweater", and then the displayed
    // list of products is updated to that one and shown on the main content panel
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
