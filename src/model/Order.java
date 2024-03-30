package model;

import java.sql.Timestamp;

public class Order {
    private int orderId;
    private int userId;
    private Timestamp orderDate;
    private int clothingId;

    private int quantity;

    private double totalAmount;
    private String status;

    public Order() {

    }

    public Order(int userId, Timestamp orderDate, int clothingId,
                 double totalAmount, String status, int quantity) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.clothingId = clothingId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Order = {" +
                "orderId = " + orderId +
                ", userId = " + userId +
                ", orderDate = " + orderDate +
                ", clothingId = " + clothingId +
                ", quantity = " + quantity +
                ", totalAmount = " + totalAmount +
                ", status = '" + status + '\'' +
                '}';
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
