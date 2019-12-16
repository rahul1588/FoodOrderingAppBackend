package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="category")
@NamedQueries({
        @NamedQuery(name="getCategoryByUuid", query = "select q from CategoryEntity q where q.uuid = :uuid"),
        @NamedQuery(name = "allCategories", query = "select q from CategoryEntity q"),
})

public class CategoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="uuid")
    @Size(max=200)
    @NotNull
    private String uuid;

    @Column(name="category_name")
    @Size(max=255)
    @NotNull
    private String categoryName;

    @ManyToMany
    @JoinTable(name = "restaurant_category", joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "restaurant_id"))
    private List<RestaurantEntity> restaurants = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "category_item", joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<ItemEntity> items = new ArrayList<>();

    public List<RestaurantEntity> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<RestaurantEntity> restaurants) {
        this.restaurants = restaurants;
    }

    @OneToMany(mappedBy = "categoryId", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<RestaurantCategoryEntity> restaurantCategory = new ArrayList<>();

    @OneToMany(mappedBy = "categoryId", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<CategoryItemEntity> categoryItem = new ArrayList<>();


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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    public List<RestaurantCategoryEntity> getRestaurantCategory() {
        return restaurantCategory;
    }

    public void setRestaurantCategory(List<RestaurantCategoryEntity> restaurantCategory) {
        this.restaurantCategory = restaurantCategory;
    }

    public List<CategoryItemEntity> getCategoryItem() {
        return categoryItem;
    }

    public void setCategoryItem(List<CategoryItemEntity> categoryItem) {
        this.categoryItem = categoryItem;
    }

    public CategoryEntity() {
    }
}
