package com.harshsetia.orderboard.dao;

import com.harshsetia.orderboard.model.Order;
import com.harshsetia.orderboard.model.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harshsetia on 10/02/2017.
 */
@Repository
public class OrderDaoImpl implements OrderDao {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public boolean registerOrder(Order order) {
        Map<String, Object> params = getStringObjectMap(order);

        String query = "INSERT INTO orders (userid, quantity, pricePerKilo, orderType) VALUES (:userid, :quantity, :pricePerKilo, :orderType)";

        int inserted =  namedParameterJdbcTemplate.update(query, params);
        return inserted==1? true: false;
    }

    private Map<String, Object> getStringObjectMap(Order order) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userid", order.getUserid());
        params.put("quantity", order.getQuantity());
        params.put("pricePerKilo", order.getPricePerKilo());
        params.put("orderType", order.getOrderType().getValue());
        return params;
    }

    public boolean cancelOrder(long orderid) {
        String query = "delete from orders where id=:orderid";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderid", orderid);
        int deleted =  namedParameterJdbcTemplate.update(query, params);
        return deleted==1? true: false;
    }

    @Override
    public List<Order> summary() {
        Map<String, Object> params = new HashMap<String, Object>();
        String query = "Select * from orders";
        List<Order> result = namedParameterJdbcTemplate.query(query, params, new OrderMapper());
        return result;

    }

    private static final class OrderMapper implements RowMapper<Order> {

        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getInt("id"));
            order.setUserid(rs.getString("userid"));
            order.setQuantity(rs.getDouble("quantity"));
            order.setPricePerKilo(rs.getDouble("pricePerKilo"));
            order.setOrderType(OrderType.valueOf(rs.getString("orderType")));
            return order;
        }
    }
}
