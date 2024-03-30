package controller;

import manager.FormsManager;
import manager.SessionManager;
import model.User;
import repository.BrandRepository;
import repository.CategoryRepository;
import repository.OrderRepository;
import view.AdminView;
import view.BasketView;
import view.OrderView;
import view.ShopView;

import javax.swing.*;


public class OrderController {
    private final OrderView orderView;
    private final OrderRepository orderRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    // Initialize the controller
    public OrderController(OrderView orderView) {
        this.orderView = orderView;
        orderRepository = new OrderRepository();
        brandRepository = new BrandRepository();
        categoryRepository = new CategoryRepository();
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
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

        // If the user is logged in, let him see his basket
        if (loggedInUser != null) {
            FormsManager.getInstance().showForm(new BasketView());
        }

        // If the user isn't logged in, prevent him from seeing his basket
        else {
            JOptionPane.showMessageDialog(
                    orderView,
                    "You can't see your basket because you're not logged in.",
                    "Fashion Frost",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void seeAdmin() {
        FormsManager.getInstance().showForm(new AdminView());
    }
}
