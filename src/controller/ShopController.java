package controller;

import manager.FormsManager;
import manager.SessionManager;
import model.Clothing;
import model.User;
import repository.BrandRepository;
import repository.CategoryRepository;
import repository.ClothingRepository;
import view.*;

import javax.swing.*;
import java.util.List;

public class ShopController {
    private final ShopView shopView;
    private final ClothingRepository clothingRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    // Initialize the controller
    public ShopController(ShopView shopView) {
        this.shopView = shopView;
        clothingRepository = new ClothingRepository();
        categoryRepository = new CategoryRepository();
        brandRepository = new BrandRepository();
    }

    public ClothingRepository getClothingRepository() {
        return clothingRepository;
    }

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    public BrandRepository getBrandRepository() {
        return brandRepository;
    }

    public void seeClothing(String clothingName) {
        FormsManager.getInstance().showForm(new ClothingView(clothingName));
    }

    public void seeAdmin() {
        FormsManager.getInstance().showForm(new AdminView());
    }

    public void seeOrders() {
        // Get the currently logged in user
        User loggedInUser = SessionManager.getLoggedInUser();

        // If the user is logged in, let him see his order page
        if (loggedInUser != null) {
            FormsManager.getInstance().showForm(new OrderView());
        }

        // If the user isn't logged in, prevent him from seeing the order page
        else {
            JOptionPane.showMessageDialog(
                    shopView,
                    "You can't see your orders because you're not logged in.",
                    "Fashion Frost",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void seeBasket() {
        // Get the currently logged in user
        User loggedInUser = SessionManager.getLoggedInUser();

        // If the user is logged in, let him see his basket page
        if (loggedInUser != null) {
            FormsManager.getInstance().showForm(new BasketView());
        }

        // If the user isn't logged in, prevent him from seeing his basket
        else {
            JOptionPane.showMessageDialog(
                    shopView,
                    "You can't see your basket because you're not logged in.",
                    "Fashion Frost",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Applies the filter search (searchbar + checkboxes + radios)
    public void applyFilters(JTextField searchBar, List<JCheckBox> categoryCheckBoxList, List<JCheckBox> brandCheckBoxList, List<JRadioButton> orderByRadioButtons) {
        // Get the filtered list from the repo
        List<Clothing> filteredList = clothingRepository.getFilteredClothingsInStock(searchBar.getText(), categoryCheckBoxList, brandCheckBoxList, orderByRadioButtons);

        // Update the displaying list in the shop page
        shopView.setClothingList(filteredList);

        // Repaint and update the panel displaying the products
        shopView.updateClothingPanel();
    }
}
