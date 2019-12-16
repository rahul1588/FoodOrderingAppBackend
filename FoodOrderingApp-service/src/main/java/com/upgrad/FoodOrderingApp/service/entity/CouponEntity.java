package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="coupon")
@NamedQueries({
        @NamedQuery(name = "couponByCouponName", query = "select c from CouponEntity c where c.couponName = :couponName"),
        @NamedQuery(name = "couponByUUID", query = "select c from CouponEntity c where c.uuid = :couponId" )
})

public class CouponEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="uuid")
    @Size(max=200)
    @NotNull
    private String uuid;

    @Column(name="coupon_name")
    @Size(max=255)
    @NotNull
    private String couponName;

    @NotNull
    @Column(name="percent")
    private Integer percent;

    @OneToMany(mappedBy = "coupon", cascade= CascadeType.ALL, fetch= FetchType.LAZY)
    private List<OrderEntity> orders = new ArrayList<>();

    public CouponEntity() {
    }

    public CouponEntity(String uuid, String couponName, Integer percent) {
        this.uuid = uuid;
        this.couponName = couponName;
        this.percent = percent;
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

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }
}
