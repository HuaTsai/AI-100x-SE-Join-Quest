@order_pricing
Feature: E-commerce Order Pricing Promotions
  As a shopper
  I want the system to calculate my order total with applicable promotions
  So that I can understand how much to pay and what items I will receive

  Scenario: Single product without promotions
    Given no promotions are applied
    When a customer places an order with:
      | productName | quantity | unitPrice |
      | T-shirt     | 1        | 500       |
    Then the order summary should be:
      | totalAmount |
      | 500         |
    And the customer should receive:
      | productName | quantity |
      | T-shirt     | 1        |

  Scenario: Threshold discount applies when subtotal reaches 1000
    Given the threshold discount promotion is configured:
      | threshold | discount |
      | 1000      | 100      |
    When a customer places an order with:
      | productName | quantity | unitPrice |
      | T-shirt     | 2        | 500       |
      | 褲子        | 1        | 600       |
    Then the order summary should be:
      | originalAmount | discount | totalAmount |
      | 1600           | 100      | 1500        |
    And the customer should receive:
      | productName | quantity |
      | T-shirt     | 2        |
      | 褲子        | 1        |

  Scenario: Buy-one-get-one for cosmetics - multiple products
    Given the buy one get one promotion for cosmetics is active
    When a customer places an order with:
      | productName | category  | quantity | unitPrice |
      | 口紅        | cosmetics | 1        | 300       |
      | 粉底液      | cosmetics | 1        | 400       |
    Then the order summary should be:
      | totalAmount |
      | 700         |
    And the customer should receive:
      | productName | quantity |
      | 口紅        | 2        |
      | 粉底液      | 2        |

  Scenario: Buy-one-get-one for cosmetics - same product twice
    Given the buy one get one promotion for cosmetics is active
    When a customer places an order with:
      | productName | category  | quantity | unitPrice |
      | 口紅        | cosmetics | 2        | 300       |
    Then the order summary should be:
      | totalAmount |
      | 600         |
    And the customer should receive:
      | productName | quantity |
      | 口紅        | 3        |

  Scenario: Buy-one-get-one for cosmetics - mixed categories
    Given the buy one get one promotion for cosmetics is active
    When a customer places an order with:
      | productName | category  | quantity | unitPrice |
      | 襪子        | apparel   | 1        | 100       |
      | 口紅        | cosmetics | 1        | 300       |
    Then the order summary should be:
      | totalAmount |
      | 400         |
    And the customer should receive:
      | productName | quantity |
      | 襪子        | 1        |
      | 口紅        | 2        |

  Scenario: Multiple promotions stacked
    Given the threshold discount promotion is configured:
      | threshold | discount |
      | 1000      | 100      |
    And the buy one get one promotion for cosmetics is active
    When a customer places an order with:
      | productName | category  | quantity | unitPrice |
      | T-shirt     | apparel   | 3        | 500       |
      | 口紅        | cosmetics | 1        | 300       |
    Then the order summary should be:
      | originalAmount | discount | totalAmount |
      | 1800           | 100      | 1700        |
    And the customer should receive:
      | productName | quantity |
      | T-shirt     | 3        |
      | 口紅        | 2        |

  Scenario: Singles' Day promotion: 20% off when reach 10 items - items in one category
    Given the Singles' Day promotion is active
    When a customer places an order with:
      | productName | quantity | unitPrice |
      | 襪子        | 12       | 100       |
    Then the order summary should be:
      | originalAmount | discount | totalAmount |
      | 1200           | 200      | 1000        |
    And the customer should receive:
      | productName | quantity |
      | 襪子        | 12       |

  Scenario: Singles' Day promotion: 20% off when reach 10 items - more items in one category
    Given the Singles' Day promotion is active
    When a customer places an order with:
      | productName | quantity | unitPrice |
      | 襪子        | 27       | 200       |
    Then the order summary should be:
      | originalAmount | discount | totalAmount |
      | 5400           | 800      | 4600        |
    And the customer should receive:
      | productName | quantity |
      | 襪子        | 27       |

  Scenario: Singles' Day promotion: 20% off when reach 10 items - mixed categories
    Given the Singles' Day promotion is active
    When a customer places an order with:
      | productName | quantity | unitPrice |
      | 襪子        | 1        | 100       |
      | 口紅        | 1        | 200       |
      | 褲子        | 1        | 300       |
      | 襯衫        | 1        | 400       |
      | 褲子        | 1        | 500       |
      | 帽子        | 1        | 600       |
      | 眼鏡        | 1        | 700       |
      | 鞋子        | 1        | 800       |
      | 內衣        | 1        | 900       |
      | 口罩        | 1        | 1000      |
    Then the order summary should be:
      | originalAmount | discount | totalAmount |
      | 5500           | 0        | 5500        |
    And the customer should receive:
      | productName | quantity |
      | 襪子        | 1        |
      | 口紅        | 1        |
      | 褲子        | 1        |
      | 襯衫        | 1        |
      | 褲子        | 1        |
      | 帽子        | 1        |
      | 眼鏡        | 1        |
      | 鞋子        | 1        |
      | 內衣        | 1        |
      | 口罩        | 1        |