package com.harshsetia.orderboard.controller;

import com.harshsetia.orderboard.dao.OrderDao;
import com.harshsetia.orderboard.model.Order;
import com.harshsetia.orderboard.model.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.util.*;
import java.util.logging.Logger;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by harshsetia on 11/02/2017.
 */
@Controller
public class OrderController {

    private static final Logger logger = Logger.getLogger(OrderController.class.getName());

    @Autowired
    OrderDao orderDao;

    @Autowired
    SummaryDisplayHelper summaryDisplayHelper;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getOrderSummary(Model model){
        logger.entering("OrderController", "getOrderSummary");
        List<Order> orders = orderDao.summary();

        //add the List<String> to model to be displayed by the UI layer
        model.addAttribute("orders", summaryDisplayHelper.transformOrderList(orders));
        return new ModelAndView("summary.jsp");            //I haven't created any jsp, this is just for indication purposes
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView registerOrder(@ModelAttribute("order") @Valid Order order, BindingResult bindingResult,Model model){
        ModelAndView modelAndView;
        if(bindingResult.hasErrors()){
            //I haven't created any jsp, this is just for indication purposes
            modelAndView = new ModelAndView("error.jsp", newHashMap(), HttpStatus.BAD_REQUEST);
        }
        else{
            boolean status = orderDao.registerOrder(order);
            model.addAttribute("registerStatus" , status);
            //I haven't created any jsp, this is just for indication purposes
            modelAndView = new ModelAndView("summary.jsp"); //This should be mapped to the "/" Get request
        }
        return modelAndView;
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public ModelAndView cancelOrder( @ModelAttribute("order") Order order, BindingResult bindingResult,Model model){
        model.addAttribute("cancelStatus", orderDao.cancelOrder(order.getId()));
        return new ModelAndView("summary.jsp");
    }

}
