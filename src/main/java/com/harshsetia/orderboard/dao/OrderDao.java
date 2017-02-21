package com.harshsetia.orderboard.dao;

import com.harshsetia.orderboard.model.Order;

import java.util.List;

/**
 * Created by harshsetia on 10/02/2017.
 */
public interface OrderDao {
    boolean registerOrder(Order order);
    boolean cancelOrder(long orderid);
    List<Order> summary();
}
