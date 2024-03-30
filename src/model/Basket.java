package model;

public class Basket {
    private int basketId;
    private int userId;
    private int clothingId;
    private double totalAmount;

    public Basket() {

    }

    public Basket(int userId, int clothingId, double totalAmount) {
        this.userId = userId;
        this.clothingId = clothingId;
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Basket = {" +
                "basketId = " + basketId +
                ", userId = " + userId +
                ", clothingId = " + clothingId +
                ", totalAmount = " + totalAmount +
                '}';
    }

    public int getBasketId() {
        return basketId;
    }

    public void setBasketId(int basketId) {
        this.basketId = basketId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClothingId() {
        return clothingId;
    }

    public void setClothingId(int clothingId) {
        this.clothingId = clothingId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
