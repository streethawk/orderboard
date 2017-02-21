package com.harshsetia.orderboard.controller;

import com.harshsetia.orderboard.dao.OrderDaoImpl;
import com.harshsetia.orderboard.model.Order;
import com.harshsetia.orderboard.model.OrderType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;

import org.springframework.web.servlet.View;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

/**
 * Created by harshsetia on 11/02/2017.
 */
public class OrderControllerTest {

    @InjectMocks
    OrderController orderController;

    @Mock
    private OrderDaoImpl orderDao;

    @Mock
    private SummaryDisplayHelper summaryDisplayHelper;

    @Mock
    View mockView;

    private MockMvc mockMvc;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setSingleView(mockView)
                .build();
    }

    @Test
    public void getOrderSummaryTestForCheckingRequestResponse() throws Exception{

        when(orderDao.summary()).thenReturn(newArrayList());
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void getOrderSummaryTestForSingleBuy() throws Exception{
        Order order = new Order();
        order.setUserid("user1");
        order.setOrderType(OrderType.BUY);
        order.setQuantity(10);
        order.setPricePerKilo(100);
        List<Order> orders = newArrayList(order);
        List<String> orderBoard = newArrayList("10 kg for £100");

        when(orderDao.summary()).thenReturn(orders);
        when(summaryDisplayHelper.transformOrderList(orders)).thenReturn(orderBoard);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("orders", orderBoard));
    }

    @Test
    public void getOrderSummaryTestForSingleSell() throws Exception{
        Order order = new Order();
        order.setUserid("user1");
        order.setOrderType(OrderType.SELL);
        order.setQuantity(10);
        order.setPricePerKilo(100);
        List<Order> orders = newArrayList(order);
        List<String> orderBoard = newArrayList("10 kg for £100");

        when(orderDao.summary()).thenReturn(orders);
        when(summaryDisplayHelper.transformOrderList(orders)).thenReturn(orderBoard);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("orders", orderBoard));
    }

    @Test
    public void getOrderSummarySortedForSell() throws Exception{
        /* a) SELL: 3.5 kg for £306 [user1]
           b) SELL: 1.2 kg for £310 [user2]
           c) SELL: 1.5 kg for £307 [user3]
           d) SELL: 2.0 kg for £306 [user4] */
        List<Order> orders = getOrders(OrderType.SELL);
        List<String> expectedOrderBoard = newArrayList("5.5 kg for £306", "1.5 kg for £307", "1.2 kg for £310");

        when(orderDao.summary()).thenReturn(orders);
        when(summaryDisplayHelper.transformOrderList(orders)).thenReturn(expectedOrderBoard);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("orders", expectedOrderBoard));

    }

    @Test
    public void getOrderSummarySortedForBuy() throws Exception{
        /* a) BUY: 3.5 kg for £306 [user1]
           b) BUY: 1.2 kg for £310 [user2]
           c) BUY: 1.5 kg for £307 [user3]
           d) BUY: 2.0 kg for £306 [user4] */
        List<Order> orders = getOrders(OrderType.BUY);
        List<String> expectedOrderBoard = newArrayList("1.2 kg for £310", "1.5 kg for £307", "5.5 kg for £306");

        when(orderDao.summary()).thenReturn(orders);
        when(summaryDisplayHelper.transformOrderList(orders)).thenReturn(expectedOrderBoard);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("orders", expectedOrderBoard));

    }

    @Test
    public void getOrderSummaryForMixedOrders() throws Exception{
        /* a) SELL: 3.5 kg for £306 [user1]
           b) SELL: 1.2 kg for £310 [user2]
           c) SELL: 1.5 kg for £307 [user3]
           d) SELL: 2.0 kg for £306 [user4]
           a) BUY: 3.5 kg for £306 [user1]
           b) BUY: 1.2 kg for £310 [user2]
           c) BUY: 1.5 kg for £307 [user3]
           d) BUY: 2.0 kg for £306 [user4]
           */

        List<Order> sellOrders = getOrders(OrderType.SELL);
        List<Order> buyOrders = getOrders(OrderType.BUY);
        sellOrders.addAll(buyOrders); //merge the two lists
        List<String> expectedOrderBoard = newArrayList("5.5 kg for £306", "1.5 kg for £307", "1.2 kg for £310",
                "1.2 kg for £310", "1.5 kg for £307", "5.5 kg for £306");

        when(orderDao.summary()).thenReturn(sellOrders);
        when(summaryDisplayHelper.transformOrderList(sellOrders)).thenReturn(expectedOrderBoard);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("orders", expectedOrderBoard));


    }

    private List<Order> getOrders(OrderType orderType) {
        Order order1 = new Order();
        order1.setOrderType(orderType);
        order1.setPricePerKilo(306);
        order1.setQuantity(3.5);
        order1.setUserid("user1");

        Order order2 = new Order();
        order2.setOrderType(orderType);
        order2.setPricePerKilo(310);
        order2.setQuantity(1.2);
        order2.setUserid("user2");

        Order order3 = new Order();
        order3.setOrderType(orderType);
        order3.setPricePerKilo(307);
        order3.setQuantity(1.5);
        order3.setUserid("user3");

        Order order4 = new Order();
        order4.setOrderType(orderType);
        order4.setPricePerKilo(306);
        order4.setQuantity(2.0);
        order4.setUserid("user4");

        return newArrayList(order1, order2, order3, order4);
    }


}