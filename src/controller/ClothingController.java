package controller;

import manager.FormsManager;
import manager.SessionManager;
import model.Basket;
import model.User;
import repository.BasketRepository;
import repository.BrandRepository;
import repository.CategoryRepository;
import repository.ClothingRepository;
import view.*;

import javax.swing.*;

public class ClothingController {
    private final ClothingView clothingView;

    private BasketRepository basketRepository;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;
    private ClothingRepository clothingRepository;

    // Initialize the controller
    public ClothingController(ClothingView clothingView) {
        this.clothingView = clothingView;
        basketRepository = new BasketRepository();
        brandRepository = new BrandRepository();
        categoryRepository = new CategoryRepository();
        clothingRepository = new ClothingRepository();
    }

    public ClothingRepository getClothingRepository() {
        return clothingRepository;
    }

    public BasketRepository getBasketRepository() {
        return basketRepository;
    }

    public BrandRepository getBrandRepository() {
        return brandRepository;
    }

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    public void seeProducts() {
        FormsManager.getInstance().showForm(new ShopView());
    }

    public void seeBasket() {
        // Get the logged in user
        User loggedInUser = SessionManager.getLoggedInUser();

        // If the user is logged in, then let it see his basket
        if (loggedInUser != null) {
            FormsManager.getInstance().showForm(new BasketView());
        }

        // If the user is not logged in, prevent him from seeing the basket
        else {
            JOptionPane.showMessageDialog(
                    clothingView,
                    "You can't see your basket because you're not logged in!",
                    "Fashion Frost",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void seeOrders() {
        // Get the logged in user
        User loggedInUser = SessionManager.getLoggedInUser();

        // If the user is logged in, let him see his orders
        if (loggedInUser != null) {
            FormsManager.getInstance().showForm(new OrderView());
        }

        // If the user isn't logged in, prevent him from seeing his orders
        else {
            JOptionPane.showMessageDialog(
                    clothingView,
                    "You can't see your orders because you're not logged in.",
                    "Fashion Frost",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void seeAdmin() {
        FormsManager.getInstance().showForm(new AdminView());
    }

    public void addClothingToBasket(String clothingName) {
        // Check if the user is logged in, and if so, add a new item to his basket
        if (SessionManager.getLoggedInUser() != null) {
            JOptionPane.showMessageDialog(
                    clothingView,
                    "Sucessfully added a new product to your basket!",
                    "Fashion Frost",
                    JOptionPane.INFORMATION_MESSAGE);

            basketRepository.createBasket(new Basket(SessionManager.getLoggedInUser().getUserId(), clothingView.getClothing().getClothingId(), clothingView.getClothing().getPrice()));

            // Redirect the user to the general shop after he added a new clothing to his basket
            FormsManager.getInstance().showForm(new ShopView());
        }

        // If the user is not logged in, prevent him from seeing the basket
        else {
            JOptionPane.showMessageDialog(
                    clothingView,
                    "You can't add items to your basket because you're not logged in.",
                    "Fashion Frost",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
