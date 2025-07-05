package com.example.service;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.Product;
import java.util.List;
import java.util.ArrayList;

public class OrderService {

    private static final int SINGLES_DAY_THRESHOLD = 10;
    private static final int SINGLES_DAY_DISCOUNT_MULTIPLIER = 2;
    private static final String COSMETICS_CATEGORY = "cosmetics";

    private int thresholdAmount;
    private int discountAmount;
    private boolean buyOneGetOneActive;
    private boolean singlesDayPromotionActive;

    public void setThresholdPromotion(int thresholdAmount, int discountAmount) {
        this.thresholdAmount = thresholdAmount;
        this.discountAmount = discountAmount;
    }

    public void setBuyOneGetOnePromotion(boolean active) {
        this.buyOneGetOneActive = active;
    }

    public void setSinglesDayPromotionActive(boolean active) {
        this.singlesDayPromotionActive = active;
    }

    public Order processOrder(List<OrderItem> items) {
        Order order = new Order(items);

        int originalAmount = calculateOriginalAmount(items);
        order.setOriginalAmount(originalAmount);

        int discount = calculateTotalDiscount(items, originalAmount);
        order.setDiscount(discount);
        order.setTotalAmount(originalAmount - discount);

        return order;
    }

    private int calculateOriginalAmount(List<OrderItem> items) {
        int totalAmount = 0;
        for (OrderItem item : items) {
            totalAmount += item.getSubtotal();
        }
        return totalAmount;
    }

    private int calculateTotalDiscount(List<OrderItem> items, int originalAmount) {
        int totalDiscount = 0;

        totalDiscount += calculateThresholdDiscount(originalAmount);
        totalDiscount += calculateSinglesDayDiscount(items);

        return totalDiscount;
    }

    private int calculateThresholdDiscount(int originalAmount) {
        if (thresholdAmount > 0 && originalAmount >= thresholdAmount) {
            return discountAmount;
        }
        return 0;
    }

    private int calculateSinglesDayDiscount(List<OrderItem> items) {
        if (!singlesDayPromotionActive) {
            return 0;
        }

        int discount = 0;
        for (OrderItem item : items) {
            if (item.getQuantity() >= SINGLES_DAY_THRESHOLD) {
                int discountMultiplier = item.getQuantity() / SINGLES_DAY_THRESHOLD;
                // For Singles' Day promotion: discount = multiplier *
                // SINGLES_DAY_DISCOUNT_MULTIPLIER * unit_price
                discount += discountMultiplier * SINGLES_DAY_DISCOUNT_MULTIPLIER * item.getProduct().getUnitPrice();
            }
        }
        return discount;
    }

    public List<OrderItem> getDeliveryItems(Order order) {
        List<OrderItem> deliveryItems = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            int deliveryQuantity = calculateDeliveryQuantity(item);
            deliveryItems.add(new OrderItem(item.getProduct(), deliveryQuantity));
        }

        return deliveryItems;
    }

    private int calculateDeliveryQuantity(OrderItem item) {
        int deliveryQuantity = item.getQuantity();

        if (buyOneGetOneActive && COSMETICS_CATEGORY.equals(item.getProduct().getCategory())) {
            deliveryQuantity += calculateBuyOneGetOneBonusItems(item.getQuantity());
        }

        return deliveryQuantity;
    }

    private int calculateBuyOneGetOneBonusItems(int quantity) {
        // For cosmetics with buy-one-get-one promotion:
        // - You get the original quantity plus bonus items
        // - Bonus items = floor(quantity / 2) for pairs, plus 1 if quantity is odd
        int bonusItems = quantity / 2;
        if (quantity % 2 == 1) {
            bonusItems += 1;
        }
        return bonusItems;
    }
}
