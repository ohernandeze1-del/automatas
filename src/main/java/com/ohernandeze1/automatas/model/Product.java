package com.ohernandeze1.automatas.model;

public class Product {
    private final String code;
    private final String name;
    private final int price;
    private int stock;

    public Product(String code, String name, int price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getStock() { return stock; }
    public void addStock(int quantity) { this.stock += quantity; }
    public void removeOne() { this.stock -= 1; }
}
