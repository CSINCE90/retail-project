package com.retailsports.payment_service.enums;

public enum OrderStatus {
    PENDING, // initial status
    CONFIRMED, // after payment is confirmed
    PROCESSING, // order is being prepared
    SHIPPED, // order is on the way
    DELIVERED, // order has been delivered
    CANCELLED, // order has been cancelled
    REFUNDED // order has been refunded
}
