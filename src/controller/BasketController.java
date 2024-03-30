package controller;

import manager.FormsManager;
import manager.SessionManager;
import model.Clothing;
import model.User;
import repository.BasketRepository;
import view.AdminView;
import view.BasketView;
import view.OrderView;
import view.ShopView;

import javax.swing.*;

public class BasketController {
    private final BasketView basketView;
    private final BasketRepository basketRepository;

    // Initialize the basket controller
    public BasketController(BasketView basketView) {
        this.basketView = basketView;

        basketRepository = new BasketRepository();
    }

    public BasketRepository getBasketRepository() {
        return this.basketRepository;
    }

    public void seeProducts() {
        FormsManager.getInstance().showForm(new ShopView());
    }

    public void seeOrders() {
        // Get the currently logged in user
        User loggedInUser = SessionManager.getLoggedInUser();

        // If the user is logged in, let him see his orders
        if (loggedInUser != null) {
            FormsManager.getInstance().showForm(new OrderView());
        }

        // If the user isn't logged in, show an error message
        else {
            JOptionPane.showMessageDialog(
                    basketView,
                    "You can't see your orders because you're not logged in.",
                    "Fashion Frost",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void seeAdmin() {
        FormsManager.getInstance().showForm(new AdminView());
    }

    public void placeOrder() {
        basketRepository.placeOrder(SessionManager.getLoggedInUser().getUserId());

        JOptionPane.showMessageDialog(
                basketView,
                "You have successfully placed your order!",
                "Fashion Frost",
                JOptionPane.INFORMATION_MESSAGE);

        FormsManager.getInstance().showForm(new OrderView());
    }

    public void deleteProduct(String productName) {
        // Create a new clothing which will be the one needed to be removed from the user's basket
        Clothing clothingToDelete = new Clothing();

        // Iterate through each clothing in current user's basket
        for (int i = 0; i < basketView.getClothingList().size(); i++) {
            // Check if the current clothing is the one needed to be removed from the user's basket
            if (basketView.getClothingList().get(i).getName().equalsIgnoreCase(productName)) {
                // If so, mark it and stop the loop
                clothingToDelete = basketView.getClothingList().get(i);
                break;
            }
        }

        // Proceed to remove the selected clothing from the logged in user's basket
        basketRepository.deleteClothingInBasketForUser(SessionManager.getLoggedInUser().getUserId(), clothingToDelete.getClothingId());
        basketView.getClothingList().remove(clothingToDelete);

        // Update the panel which shows the clothings in the basket for the current user
        basketView.updateClothingPanel();

        // Update the total price to pay after the user removed a clothing from his basket
        basketView.getTotalPriceToPay().setText("Total price to pay: $" + basketRepository.getTotalPriceToPayForUser(SessionManager.getLoggedInUser().getUserId()));

        // If the total price to pay is 0, disable the place order button
        basketView.getPlaceOrderButton().setEnabled(basketRepository.getTotalPriceToPayForUser(SessionManager.getLoggedInUser().getUserId()) > 0);
    }
}
