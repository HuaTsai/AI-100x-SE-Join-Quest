package com.example.model;

public class Product {
    private String name;
    private String category;
    private int unitPrice;
    
    public Product(String name, int unitPrice) {
        this.name = name;
        this.unitPrice = unitPrice;
    }
    
    public Product(String name, String category, int unitPrice) {
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public int getUnitPrice() {
        return unitPrice;
    }
}
