package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    public ItemEntity getItemByUUID(String itemId) throws ItemNotFoundException {
        ItemEntity itemEntity = itemDao.getItemById(itemId);
        if(itemEntity == null){
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        }else {
            return itemEntity;
        }
    }

    public List<OrderItemEntity> getItemsByOrder(OrderEntity orderEntity) {
        return orderItemDao.getItemsByOrder(orderEntity);
    }

    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantId, String categoryId) {
        RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(restaurantId);
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryId);
        List<ItemEntity> restaurantItemList = new ArrayList<>();

        for (ItemEntity restaurantItem : restaurantEntity.getItems()) {
            for (ItemEntity categoryItem : categoryEntity.getItems()) {
                if (restaurantItem.getUuid().equals(categoryItem.getUuid())) {
                    restaurantItemList.add(restaurantItem);
                }
            }
        }
        restaurantItemList.sort(Comparator.comparing(ItemEntity::getItemName));
        return restaurantItemList;
    }

    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        List<ItemEntity> itemEntityList = new ArrayList<ItemEntity>();
        for (OrderEntity orderEntity : orderDao.getOrdersByRestaurant(restaurantEntity)) {
            for (OrderItemEntity orderItemEntity : orderItemDao.getItemsByOrder(orderEntity)) {
                itemEntityList.add(orderItemEntity.getItemId());
            }
        }

        Map<String, Integer> map = new HashMap<String, Integer>();
        for (ItemEntity itemEntity : itemEntityList) {
            Integer count = map.get(itemEntity.getUuid());
            map.put(itemEntity.getUuid(), (count == null) ? 1 : count + 1);
        }

        Map<String, Integer> map1 = new TreeMap<String, Integer>(map);
        List<ItemEntity> sortedItemEntityList = new ArrayList<ItemEntity>();
        for (Map.Entry<String, Integer> entry : map1.entrySet()) {
            sortedItemEntityList.add(itemDao.getItemByUUID(entry.getKey()));
        }
        Collections.reverse(sortedItemEntityList);

        return sortedItemEntityList;
    }
}
