package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="orders")
@NamedQueries({
        @NamedQuery(name = "pastOrdersByDate", query = "select o from OrderEntity o where o.customer = :customer order by o.date desc"),
        @NamedQuery(name = "ordersByCustomer", query = "select o from OrderEntity o where o.customer = :customer order by o.date desc "),
        @NamedQuery(name = "ordersByRestaurant", query = "select q from OrderEntity q where q.restaurant = :restaurant"),

})

public class OrderEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="uuid")
    @Size(max=200)
    @NotNull
    private String uuid;

    @Column(name="bill")
    @NotNull
    private BigDecimal bill;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="coupon_id")
    private CouponEntity coupon;

    @Column(name="discount")
    @NotNull
    private BigDecimal discount;

    @Column(name = "date")
    @NotNull
    private Date date;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="payment_id")
    private PaymentEntity payment;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="customer_id")
    private CustomerEntity customer;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="address_id")
    private AddressEntity address;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="restaurant_id")
    private RestaurantEntity restaurant;

    @OneToMany(mappedBy = "orderId", cascade= CascadeType.ALL, fetch= FetchType.LAZY)
    private List<OrderItemEntity> orderItem = new ArrayList<>();

    public OrderEntity() {}

    public OrderEntity(@NotNull @Size(max = 200) String uuid, @NotNull Double bill, @NotNull CouponEntity coupon, @NotNull Double discount, @NotNull Date date, @NotNull PaymentEntity payment, @NotNull CustomerEntity customer, @NotNull AddressEntity address, RestaurantEntity restaurant) {
        this.uuid = uuid;
        this.bill = new BigDecimal(bill);
        this.coupon = coupon;
        this.discount = new BigDecimal(discount);
        this.date = date;
        this.payment = payment;
        this.customer = customer;
        this.address = address;
        this.restaurant = restaurant;
    }

    public List<OrderItemEntity> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItemEntity> orderItem) {
        this.orderItem = orderItem;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Double getBill() {
        return bill.doubleValue();
    }

    public void setBill(Double bill) {
        this.bill = new BigDecimal(bill);
    }

    public CouponEntity getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponEntity coupon) {
        this.coupon = coupon;
    }

    public Double getDiscount() {
        return discount.doubleValue();
    }

    public void setDiscount(Double discount) {
        this.discount = new BigDecimal(discount);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }
}
