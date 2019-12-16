package com.upgrad.FoodOrderingApp.service.entity;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="restaurant_item")
public class RestaurantItemEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="item_id")
    private ItemEntity itemId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="restaurant_id")
    private RestaurantEntity restaurantId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ItemEntity getItemId() {
        return itemId;
    }

    public void setItemId(ItemEntity itemId) {
        this.itemId = this.itemId;
    }

    public RestaurantEntity getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(RestaurantEntity restaurantId) {
        this.restaurantId = restaurantId;
    }

    public RestaurantItemEntity() {
    }
}
