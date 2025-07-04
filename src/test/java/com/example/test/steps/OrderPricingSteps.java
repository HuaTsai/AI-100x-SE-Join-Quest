package com.example.test.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;
import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.Product;
import com.example.service.OrderService;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OrderPricingSteps {
    
    private OrderService orderService;
    private Order processedOrder;
    private List<OrderItem> deliveryItems;
    private int thresholdAmount;
    private int discountAmount;
    private boolean buyOneGetOneActive;

    @Given("no promotions are applied")
    public void noPromotionsAreApplied() {
        orderService = new OrderService();
    }

    @When("a customer places an order with:")
    public void aCustomerPlacesAnOrderWith(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<OrderItem> items = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            String productName = row.get("productName");
            int quantity = Integer.parseInt(row.get("quantity"));
            int unitPrice = Integer.parseInt(row.get("unitPrice"));
            String category = row.get("category");
            
            Product product = (category != null) ? 
                new Product(productName, category, unitPrice) : 
                new Product(productName, unitPrice);
            
            items.add(new OrderItem(product, quantity));
        }
        
        processedOrder = orderService.processOrder(items);
        deliveryItems = orderService.getDeliveryItems(processedOrder);
    }

    @Then("the order summary should be:")
    public void theOrderSummaryShouldBe(DataTable dataTable) {
        Map<String, String> expectedSummary = dataTable.asMaps(String.class, String.class).get(0);
        
        if (expectedSummary.containsKey("totalAmount")) {
            int expectedTotal = Integer.parseInt(expectedSummary.get("totalAmount"));
            assertEquals(expectedTotal, processedOrder.getTotalAmount());
        }
        
        if (expectedSummary.containsKey("originalAmount")) {
            int expectedOriginal = Integer.parseInt(expectedSummary.get("originalAmount"));
            assertEquals(expectedOriginal, processedOrder.getOriginalAmount());
        }
        
        if (expectedSummary.containsKey("discount")) {
            int expectedDiscount = Integer.parseInt(expectedSummary.get("discount"));
            assertEquals(expectedDiscount, processedOrder.getDiscount());
        }
    }

    @And("the customer should receive:")
    public void theCustomerShouldReceive(DataTable dataTable) {
        List<Map<String, String>> expectedItems = dataTable.asMaps(String.class, String.class);
        
        assertEquals(expectedItems.size(), deliveryItems.size());
        
        for (int i = 0; i < expectedItems.size(); i++) {
            Map<String, String> expectedItem = expectedItems.get(i);
            OrderItem actualItem = deliveryItems.get(i);
            
            assertEquals(expectedItem.get("productName"), actualItem.getProduct().getName());
            assertEquals(Integer.parseInt(expectedItem.get("quantity")), actualItem.getQuantity());
        }
    }

    @Given("the threshold discount promotion is configured:")
    public void theThresholdDiscountPromotionIsConfigured(DataTable dataTable) {
        Map<String, String> promotionConfig = dataTable.asMaps(String.class, String.class).get(0);
        thresholdAmount = Integer.parseInt(promotionConfig.get("threshold"));
        discountAmount = Integer.parseInt(promotionConfig.get("discount"));
        
        orderService = new OrderService();
        orderService.setThresholdPromotion(thresholdAmount, discountAmount);
    }

    @Given("the buy one get one promotion for cosmetics is active")
    public void theBuyOneGetOnePromotionForCosmeticsIsActive() {
        buyOneGetOneActive = true;
        if (orderService == null) {
            orderService = new OrderService();
        }
        orderService.setBuyOneGetOnePromotion(buyOneGetOneActive);
    }
}
