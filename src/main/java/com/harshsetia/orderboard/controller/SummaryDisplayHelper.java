package com.harshsetia.orderboard.controller;

import com.harshsetia.orderboard.model.Order;
import com.harshsetia.orderboard.model.OrderType;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by harshsetia on 11/02/2017.
 */
@Component
public class SummaryDisplayHelper {

    private static final String DENOMINATION = "kg";
    private static final String FOR = "for";
    private static final String SEPARATOR = " ";
    private static final String CURRENCY_SYMBOL = "£";


    public List<String> transformOrderList(List<Order> orders){
        List<Order> sellOnlyOrders = newArrayList();
        List<Order> buyOnlyOrders = newArrayList();

        for(Order order:orders) {
            if (OrderType.SELL.equals(order.getOrderType())) {
                sellOnlyOrders.add(order);
            } else
                buyOnlyOrders.add(order);
        }

        List<String> buyBoard = getOrderBoard(mergeCommonPriceItems(buyOnlyOrders, OrderType.BUY));
        List<String> sellBoard = getOrderBoard(mergeCommonPriceItems(sellOnlyOrders, OrderType.SELL));

        sellBoard.addAll(buyBoard);
        return sellBoard;
    }

    private Map<Double,Double> mergeCommonPriceItems(List<Order> orders, OrderType orderType){
        TreeMap<Double, Double> priceQuantityMap;

        if(OrderType.SELL.equals(orderType)){
            priceQuantityMap = new TreeMap<Double,Double>(new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    return o1.compareTo(o2);
                }
            });
        }
        else{
            priceQuantityMap = new TreeMap<Double,Double>(new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    return o2.compareTo(o1);
                }
            });
        }

        for(Order order: orders){
            double price = order.getPricePerKilo();
            if(priceQuantityMap.containsKey(price)){
                //existing quantity
                double quantity = priceQuantityMap.get(price);
                priceQuantityMap.put(price,quantity+order.getQuantity());
            }
            else
                priceQuantityMap.put(price,order.getQuantity());

        }
        return priceQuantityMap;
    }

    private List<String> getOrderBoard(Map<Double, Double> priceQuantityMap){
        List<String> orderBoard = newArrayList();
        Iterator itr = priceQuantityMap.keySet().iterator();
        while(itr.hasNext()){
            double price = (Double)itr.next();
            double quantity = priceQuantityMap.get(price);

            StringBuilder sbr = new StringBuilder(formatDecimal(quantity));
            sbr.append(SEPARATOR)
                    .append(DENOMINATION).append(SEPARATOR)
                    .append(FOR).append(SEPARATOR)
                    .append(CURRENCY_SYMBOL).append(formatDecimal(price));
            orderBoard.add(sbr.toString());
        }
        return orderBoard;
    }
    private String formatDecimal(double value){
        if(value == (long) value)
            return String.format("%d",(long)value);
        else
            return String.format("%s",value);
    }
}
