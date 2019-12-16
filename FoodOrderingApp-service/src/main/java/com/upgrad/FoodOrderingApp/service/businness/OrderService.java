package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CustomerAuthDao customerAuthDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CouponDao couponDao;

    /**
     * Service method to get an instance of the Coupon Entity when Coupon name is provided
     * @param couponName
     * @return
     * @throws CouponNotFoundException
     */
    public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {

        if (couponName.equals("")) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }

        CouponEntity couponEntity = couponDao.getCouponByCouponName(couponName);

        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }

        return couponEntity;
    }

    /**
     * Service method to get an instance of the Coupon Entity when Coupon UUID is provided
     * @param uuid
     * @return
     * @throws CouponNotFoundException
     */
    public CouponEntity getCouponByCouponId(String uuid) throws CouponNotFoundException {

        CouponEntity coupon = couponDao.getCouponByUUID(uuid);
        if(coupon == null){
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }

        return coupon;
    }

    /**
     * Service Method to save the order
     * @param order
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrder(OrderEntity order) {
        OrderEntity newOrder = orderDao.createNewOrder(order);
        return newOrder;
    }

    /**
     * Service method to save individual order items
     * @param orderItemEntity
     * @return
     */
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        OrderItemEntity newOrderItemEntity = orderDao.createNewOrderItem(orderItemEntity);
        return newOrderItemEntity;
    }

    /**
     * Service method to get the customer orders given customer UUID
     * @param customerUUID
     * @return
     */
    public List<OrderEntity> getOrdersByCustomers(String customerUUID) {
        return orderDao.getOrdersByCustomers(customerDao.getCustomerByUUID(customerUUID));
    }
}
