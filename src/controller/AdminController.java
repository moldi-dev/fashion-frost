package controller;

import manager.FormsManager;
import model.Brand;
import model.Category;
import model.Clothing;
import repository.BrandRepository;
import repository.CategoryRepository;
import repository.ClothingRepository;
import view.AdminView;
import view.BasketView;
import view.OrderView;
import view.ShopView;

import javax.swing.*;
import java.io.File;
import java.sql.Timestamp;

public class AdminController {
    private final AdminView adminView;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ClothingRepository clothingRepository;

    // Initialize the admin controller
    public AdminController(AdminView adminView) {
        this.adminView = adminView;

        brandRepository = new BrandRepository();
        categoryRepository = new CategoryRepository();
        clothingRepository = new ClothingRepository();

    }

    public void seeBasket() {
        FormsManager.getInstance().showForm(new BasketView());
    }

    public void seeProducts() {
        FormsManager.getInstance().showForm(new ShopView());
    }

    public void seeOrders() {
        FormsManager.getInstance().showForm(new OrderView());
    }

    public void addBrand(String brandName, String brandDescription) {
        // Check if the given input is correct, and if so, add it to the database
        if (validateBrandInput(brandName, brandDescription)) {
            brandRepository.createBrand(new Brand(brandName, brandDescription));
            showInformationMessage("Successfully added a new brand!");
        }
    }

    // Check the given input for the brand
    private boolean validateBrandInput(String brandName, String brandDescription) {
        String brandNameRegex = "^[A-Z][a-zA-Z'\\-\\s]*$";
        String brandDescriptionRegex = "^[A-Z][a-zA-Z0-9'\\-\\s]*$";

        // Validate brand name
        if (!brandName.matches(brandNameRegex)) {
            showErrorMessage("Invalid brand name. Please enter a valid brand name.");
            return false;
        }

        // Validate brand name 2
        else if (brandRepository.getBrandByName(brandName) != null) {
            showErrorMessage("This brand name already exists.");
            return false;
        }

        // Validate brand description
        else if (!brandDescription.matches(brandDescriptionRegex)) {
            showErrorMessage("Invalid brand description. Please enter a valid brand description.");
            return false;
        }

        return true;
    }

    public void addCategory(String categoryName, String categoryDescription) {
        // Check if the given input is correct, and if so, add a new category to the database
        if (validateCategoryInput(categoryName, categoryDescription)) {
            categoryRepository.createCategory(new Category(categoryName, categoryDescription));
            showInformationMessage("Successfully added a new category!");
        }
    }

    // Check if the given input for the category is correct
    private boolean validateCategoryInput(String categoryName, String categoryDescription) {
        String categoryNameRegex = "^[A-Z][a-zA-Z'\\-\\s]*$";
        String categoryDescriptionRegex = "^[A-Z][a-zA-Z0-9'\\-\\s]*$";

        // Validate brand name
        if (!categoryName.matches(categoryNameRegex)) {
            showErrorMessage("Invalid category name. Please enter a valid category name.");
            return false;
        }

        // Validate brand name 2
        else if (categoryRepository.getCategoryByName(categoryName) != null) {
            showErrorMessage("This category name already exists.");
            return false;
        }

        // Validate brand description
        else if (!categoryDescription.matches(categoryDescriptionRegex)) {
            showErrorMessage("Invalid category description. Please enter a valid category description.");
            return false;
        }

        return true;
    }

    public void addClothing(String clothingName, String clothingCategory, String clothingBrand, String clothingDescription, String clothingImageURL, String clothingPrice, String clothingStockQuantity) {
        // Check if the given input is correct, and if so, add a new clothing to the database
        if (validateClothingInput(clothingName, clothingCategory, clothingBrand, clothingDescription, clothingImageURL, clothingPrice, clothingStockQuantity)) {
            // Check if the given clothing category and brand already exists in the database
            // If one of them doesn't exist, insert it into the database
            if (categoryRepository.getCategoryByNameForAdminPanel(clothingCategory) == null) {
                categoryRepository.createCategory(new Category(clothingCategory, null));
            }

            if (brandRepository.getBrandByNameForAdminPanel(clothingBrand) == null) {
                brandRepository.createBrand(new Brand(clothingBrand, null));
            }

            // Add the given clothing to the database
            clothingRepository.createClothing(new Clothing(clothingName,
                    categoryRepository.getCategoryByNameForAdminPanel(clothingCategory).getCategoryId(),
                    brandRepository.getBrandByNameForAdminPanel(clothingBrand).getBrandId(),
                    clothingDescription,
                    Double.parseDouble(clothingPrice),
                    Integer.parseInt(clothingStockQuantity),
                    new Timestamp(System.currentTimeMillis()),
                    clothingImageURL
            ));

            showInformationMessage("Successfully added a new clothing!");
        }
    }

    // Check if the given input is correct
    private boolean validateClothingInput(String clothingName, String clothingCategory, String clothingBrand, String clothingDescription, String clothingImageURL, String clothingPrice, String clothingStockQuantity) {
        String clothingNameRegex = "^[A-Z][a-zA-Z'\\-\\s]*$";
        String clothingCategoryRegex = "^[A-Z][a-zA-Z'\\-\\s]*$";
        String clothingBrandRegex = "^[A-Z][a-zA-Z'\\-\\s]*$";
        String clothingDescriptionRegex = "^[A-Z][a-zA-Z0-9'\\-\\s]*$";
        String clothingPriceRegex = "^[1-9]\\d*(\\.\\d{1,2})?$";
        String clothingStockQuantityRegex = "^[1-9]\\d*$";

        // Validate clothing name
        if (!clothingName.matches(clothingNameRegex)) {
            showErrorMessage("Invalid clothing name. Please enter a valid clothing name.");
            return false;
        }

        // Validate clothing name 2
        else if (clothingRepository.getClothingByNameForAdminPanel(clothingName) != null) {
            showErrorMessage("This clothing name already exists.");
            return false;
        }

        // Validate clothing category
        else if (!clothingCategory.matches(clothingCategoryRegex)) {
            showErrorMessage("Invalid category name. Please enter a valid category name.");
            return false;
        }

        // Validate clothing brand
        else if (!clothingBrand.matches(clothingBrandRegex)) {
            showErrorMessage("Invalid brand name. Please enter a valid brand name.");
            return false;
        }

        // Validate clothing description
        else if (!clothingDescription.matches(clothingDescriptionRegex)) {
            showErrorMessage("Invalid description. Please enter a valid description.");
            return false;
        }

        // Validate clothing image url 2
        else if (!isImgURLValid(clothingImageURL) || clothingImageURL.isEmpty()) {
            showErrorMessage("Invalid image URL. Please enter a valid URL.");
            return false;
        }

        // Validate clothing description
        else if (!clothingPrice.matches(clothingPriceRegex)) {
            showErrorMessage("Invalid price. Please enter a valid price.");
            return false;
        }

        // Validate clothing stock quantity
        else if (!clothingStockQuantity.matches(clothingStockQuantityRegex)) {
            showErrorMessage("Invalid stock quantity. Please enter a valid stock quantity");
            return false;
        }

        return true;
    }

    // Check if there actually exists the given image url in the project files
    private boolean isImgURLValid(String imageURL) {
        String projectBaseDirectory = System.getProperty("user.dir") + "/src";

        // Construct the absolute path
        String absolutePath = projectBaseDirectory + imageURL;

        // Create a File object for the specified path
        File file = new File(absolutePath);

        // Check if the file exists
        return file.exists();
    }

    private void showInformationMessage(String message) {
        JOptionPane.showMessageDialog(
                adminView,
                message,
                "Fashion Frost",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                adminView,
                message,
                "Fashion Frost",
                JOptionPane.ERROR_MESSAGE);
    }
}
