package model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String shippingAddress;
    private String role;
    private Timestamp createdAt;

    public User() {

    }

    public User(String firstName, String lastName, String username, String email,
                String password, String shippingAddress, Timestamp createdAt, String role) {
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User = {" +
                "userId = " + userId +
                ", firstName = '" + firstName + '\'' +
                ", lastName = '" + lastName + '\'' +
                ", username = '" + username + '\'' +
                ", email = '" + email + '\'' +
                ", password = '" + password + '\'' +
                ", shippingAddress = '" + shippingAddress + '\'' +
                ", role = '" + role + '\'' +
                ", createdAt = " + createdAt +
                '}';
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
