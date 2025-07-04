package com.example.service;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.Product;
import java.util.List;
import java.util.ArrayList;

public class OrderService {
    
    private int thresholdAmount;
    private int discountAmount;
    private boolean buyOneGetOneActive;
    
    public void setThresholdPromotion(int thresholdAmount, int discountAmount) {
        this.thresholdAmount = thresholdAmount;
        this.discountAmount = discountAmount;
    }
    
    public void setBuyOneGetOnePromotion(boolean active) {
        this.buyOneGetOneActive = active;
    }
    
    public Order processOrder(List<OrderItem> items) {
        Order order = new Order(items);
        
        // Calculate original amount
        int originalAmount = 0;
        for (OrderItem item : items) {
            originalAmount += item.getSubtotal();
        }
        
        order.setOriginalAmount(originalAmount);
        
        // Apply threshold discount if applicable
        int discount = 0;
        if (thresholdAmount > 0 && originalAmount >= thresholdAmount) {
            discount = discountAmount;
        }
        
        order.setDiscount(discount);
        order.setTotalAmount(originalAmount - discount);
        
        return order;
    }
    
    public List<OrderItem> getDeliveryItems(Order order) {
        List<OrderItem> deliveryItems = new ArrayList<>();
        
        for (OrderItem item : order.getItems()) {
            int deliveryQuantity = item.getQuantity();
            
            // Apply buy-one-get-one promotion for cosmetics
            if (buyOneGetOneActive && "cosmetics".equals(item.getProduct().getCategory())) {
                // For cosmetics with buy-one-get-one promotion:
                // - You get the original quantity plus bonus items
                // - Bonus items = floor(quantity / 2) for pairs, plus 1 if quantity is odd
                int bonusItems = item.getQuantity() / 2;
                if (item.getQuantity() % 2 == 1) {
                    bonusItems += 1;
                }
                deliveryQuantity = item.getQuantity() + bonusItems;
            }
            
            deliveryItems.add(new OrderItem(item.getProduct(), deliveryQuantity));
        }
        
        return deliveryItems;
    }
}
