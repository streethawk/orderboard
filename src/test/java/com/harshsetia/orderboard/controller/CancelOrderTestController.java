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
import org.springframework.web.servlet.View;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by harshsetia on 11/02/2017.
 */
public class CancelOrderTestController {

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
    public void cancelOrderTestForCheckingRequestResponse() throws Exception{
        Order order = new Order();
        order.setId(1);
        order.setUserid("user1");
        order.setOrderType(OrderType.BUY);
        order.setQuantity(10);
        order.setPricePerKilo(100);

        when(orderDao.cancelOrder(order.getId())).thenReturn(true);
        mockMvc.perform(post("/cancel").flashAttr("order",order))
                .andExpect(status().isOk());
    }
}
