package com.harshsetia.orderboard.dao.integration;

import com.harshsetia.orderboard.dao.OrderDaoImpl;
import com.harshsetia.orderboard.model.Order;
import com.harshsetia.orderboard.model.OrderType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * Created by harshsetia on 10/02/2017.
 */
public class OrderDaoTest {

    private EmbeddedDatabase H2DB;
    private OrderDaoImpl orderDao;

    @Before
    public void setup(){
        H2DB = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("db/sql/create-db.sql")
                .addScript("db/sql/insert-data.sql")
                .build();

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(H2DB);
        orderDao = new OrderDaoImpl();
        orderDao.setNamedParameterJdbcTemplate(namedParameterJdbcTemplate);


    }


    //Integration test to test the H2 DB connectivity and initial data size availability in the DB.
    @Test
    public void summaryTestWhenDBStartsWithDefaultInsertedData(){
        List<Order> orders = orderDao.summary();
        assertEquals(5, orders.size());
    }

    @Test
    public void registerTwoNewOrders(){
        Order order1 = new Order();
        Order order2 = new Order();

        //first order (after the initial 5 pre-loaded orders from insert-data.sql
        order1.setUserid("user6");
        order1.setQuantity(22);
        order1.setPricePerKilo(300.23);
        order1.setOrderType(OrderType.BUY);

        order2.setUserid("user7");
        order2.setQuantity(22);
        order2.setPricePerKilo(300.23);
        order2.setOrderType(OrderType.SELL);


        boolean status1 = orderDao.registerOrder(order1);
        assertEquals(true, status1);
        assertEquals(6, orderDao.summary().size());


        boolean status2 = orderDao.registerOrder(order2);
        assertEquals(true, status2);
        assertEquals(7, orderDao.summary().size());
    }

    @Test
    public void cancelOrder(){
        boolean status = orderDao.cancelOrder(5);
        assertEquals(true, status);
        assertEquals(4, orderDao.summary().size());
    }

    @Test
    public void summaryTestForMixOfRegisterAndCencellation(){
        Order order1 = new Order();
        Order order2 = new Order();

        //first order (after the initial 5 pre-loaded orders from insert-data.sql
        order1.setUserid("user6");
        order1.setQuantity(22);
        order1.setPricePerKilo(300.23);
        order1.setOrderType(OrderType.BUY);

        order2.setUserid("user7");
        order2.setQuantity(22);
        order2.setPricePerKilo(300.23);
        order2.setOrderType(OrderType.SELL);

        orderDao.cancelOrder(1);
        orderDao.cancelOrder(3);
        orderDao.registerOrder(order1);
        orderDao.registerOrder(order2);

        List<Order> orders = orderDao.summary();
        assertEquals(5, orders.size());

        List<Long> expectedOrdersInDB = newArrayList(2l,4l,5l,6l,7l);

        List<Long> actualOrdersInDB = newArrayList();
        for(Order order: orders){
            actualOrdersInDB.add(order.getId());
        }

        assertThat(expectedOrdersInDB, is(actualOrdersInDB));

    }

    /*
    Multi-threaded test to verify the Summary operation
    Existing orders in DB - 5
    ThreadPool size - 2
    New Buy orders - 4, Total 5+4=9
    New Sell orders - 4, Total 9+4=13
    Remove orders 1 and 3, Total 13-2 = 11
     */

    @Test
    public void summaryTestForMultiThreadedExecution(){

        ExecutorService executorServiceForRegisterOrderWorker = Executors.newFixedThreadPool(2);
        for(int i=0;i<4;i++){
            OrderRegisterWorker orderRegisterWorkerBuy = new OrderRegisterWorker(OrderType.BUY);
            OrderRegisterWorker orderRegisterWorkerSell = new OrderRegisterWorker(OrderType.SELL);
            if(i%2==0){
                OrderCancellationWorker orderCancellationWorker = new OrderCancellationWorker(i+1);
                executorServiceForRegisterOrderWorker.submit(orderCancellationWorker);
            }
            executorServiceForRegisterOrderWorker.submit(orderRegisterWorkerBuy);
            executorServiceForRegisterOrderWorker.submit(orderRegisterWorkerSell);
        }
        executorServiceForRegisterOrderWorker.shutdown();

        try {
            executorServiceForRegisterOrderWorker.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interrupted exception");
        }

        List<Order> orders = orderDao.summary();
        assertEquals(11, orderDao.summary().size());

        List<Long> expectedOrderIdsInDB = newArrayList(2l,4l,5l,6l,7l,8l, 9l,10l,11l,12l,13l);

        List<Long> actualOrdersInDB = newArrayList();
        for(Order order: orders){
            actualOrdersInDB.add(order.getId());
        }

        assertThat(expectedOrderIdsInDB, is(actualOrdersInDB));
    }

    /*
    Inner Worker Class for Adding new Orders
     */
    public class OrderRegisterWorker implements Runnable{

        private OrderType orderType;

        public OrderRegisterWorker(OrderType orderType){
            this.orderType = orderType;
        }

        public void run() {
            Random random = new Random();
            Order order = new Order();
            order.setQuantity(random.nextDouble());
            order.setUserid("User"+random.nextInt());
            order.setPricePerKilo(random.nextDouble());
            order.setOrderType(orderType);
            orderDao.registerOrder(order);
        }
    }

    /*
    Inner Worker Class for deleting Orders
     */
    public class OrderCancellationWorker implements Runnable{

        private long id;
        public OrderCancellationWorker(long id){
            this.id = id;
        }

        public void run() {
            orderDao.cancelOrder(id);
        }
    }

    @After
    public void tearDown(){
        H2DB.shutdown();
    }

}