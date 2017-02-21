package com.harshsetia.orderboard.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

/**
 * Created by harshsetia on 10/02/2017.
 */
public class Order {

    @Range(min = 1, max=Long.MAX_VALUE)
    private long id;

    @NotEmpty(message = "The User Id cannot be empty")
    private String userid;

    //Max value is indicative and can be adjusted as per business requirements
    //Min value is chosen as 1, as any order for 0 quantity is invalid
    @Range(message = "Quantity should be between 1 and 1000", min = 1, max = 1000)
    private double quantity;

    //Min value is chosen as 1, as any price of 0 is invalid
    //Max value is indicative and can be adjusted as per business requirements
    @Range(message = "Price should be between 1 and 1000", min = 1, max = 1000)
    private double pricePerKilo;

    //By virtue of being enum, the validation is automatically applied
    public OrderType orderType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPricePerKilo() {
        return pricePerKilo;
    }

    public void setPricePerKilo(double pricePerKilo) {
        this.pricePerKilo = pricePerKilo;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userid=" + userid +
                ", quantity=" + quantity +
                ", pricePerKilo=" + pricePerKilo +
                ", orderType='" + orderType + '\'' +
                '}';
    }
}
