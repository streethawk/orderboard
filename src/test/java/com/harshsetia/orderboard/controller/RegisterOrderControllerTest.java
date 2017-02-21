package com.harshsetia.orderboard.controller;

import com.harshsetia.orderboard.dao.OrderDaoImpl;
import com.harshsetia.orderboard.model.Order;
import com.harshsetia.orderboard.model.OrderType;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by harshsetia on 11/02/2017.
 */
public class RegisterOrderControllerTest {

    @InjectMocks
    OrderController orderController;

    @Mock
    private OrderDaoImpl orderDao;

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
    public void registerOrderTestForCheckingRequestResponse() throws Exception{
        Order order = new Order();
        order.setId(1);
        order.setUserid("user1");
        order.setOrderType(OrderType.BUY);
        order.setQuantity(10);
        order.setPricePerKilo(100);

        when(orderDao.registerOrder(order)).thenReturn(true);
        mockMvc.perform(post("/register").flashAttr("order",order))
                .andExpect(status().isOk());
    }

    @Test
    public void registerOrderTestForEmptyUserId() throws Exception{
        Order order = new Order();
        order.setOrderType(OrderType.BUY);
        order.setQuantity(10);
        order.setPricePerKilo(100);

        when(orderDao.registerOrder(order)).thenReturn(true);
        mockMvc.perform(post("/register").flashAttr("order",order))
                .andExpect(status().is(400))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(model().attributeHasFieldErrors("order", "userid"));
    }

    @Test
    public void registerOrderTestForZeroPriceAndEmptyUser() throws Exception{
        Order order = new Order();
        order.setOrderType(OrderType.BUY);
        order.setQuantity(10);
        order.setPricePerKilo(0);

        when(orderDao.registerOrder(order)).thenReturn(true);
        mockMvc.perform(post("/register").flashAttr("order",order))
                .andExpect(status().is(400))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(model().attributeHasFieldErrors("order", "userid", "pricePerKilo"));
    }

    @Test
    public void registerOrderTestForNegativeQuantity() throws Exception{
        Order order = new Order();
        order.setUserid("user1");
        order.setOrderType(OrderType.BUY);
        order.setQuantity(-10);
        order.setPricePerKilo(300.00);

        when(orderDao.registerOrder(order)).thenReturn(true);
        mockMvc.perform(post("/register").flashAttr("order",order))
                .andExpect(status().is(400))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(model().attributeHasFieldErrors("order", "quantity"));
    }

}
