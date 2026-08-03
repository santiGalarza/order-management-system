package com.santiGalarza.order_management.order.status;

public class StatusCodes {
    private StatusCodes() {}

    public static final String PENDING = "PENDING";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String SHIPPED = "SHIPPED";
    public static final String DELIVERED = "DELIVERED";
    public static final String RETURN_REQUESTED = "RETURN_REQUESTED";
    public static final String RETURN_CONFIRMED = "RETURN_CONFIRMED";
    public static final String DELIVERY_FAILED = "DELIVERY_FAILED";
    public static final String REATTEMPTING_DELIVERY = "REATTEMPTING_DELIVERY";
    public static final String CANCELLED = "CANCELLED";

}
