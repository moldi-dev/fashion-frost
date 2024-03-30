package model;

import java.sql.Timestamp;

public class Clothing {
    private int clothingId;
    private String name;
    private int category;
    private int brand;
    private String description;
    private String image_url;
    private double price;
    private int stockQuantity;
    private Timestamp createdAt;

    public Clothing() {

    }

    public Clothing(String name, int category, int brand,
                    String description, double price, int stockQuantity, Timestamp createdAt, String image_url) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return "Clothing = {" +
                "clothingId = " + clothingId +
                ", name = '" + name + '\'' +
                ", category = " + category +
                ", brand = " + brand +
                ", description = '" + description + '\'' +
                ", image_url = '" + image_url + '\'' +
                ", price = " + price +
                ", stockQuantity = " + stockQuantity +
                ", createdAt = " + createdAt +
                '}';
    }

    public int getClothingId() {
        return clothingId;
    }

    public void setClothingId(int clothingId) {
        this.clothingId = clothingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getBrand() {
        return brand;
    }

    public void setBrand(int brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return this.image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
