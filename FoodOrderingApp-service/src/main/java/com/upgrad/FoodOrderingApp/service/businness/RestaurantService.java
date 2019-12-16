package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    /**
     *  returns all the restaurants according to the customer ratings
     *  @return List
     **/
    public List<RestaurantEntity> restaurantsByRating() {
        List<RestaurantEntity> restaurantEntities = restaurantDao.getAllRestaurantsByRating();

        return restaurantEntities;
    }

    /**
     *  returns the restaurants even if there is partial match in the restaurant
     *  @param=restaurantName
     *  @return matching restaurant details
     **/
    public List<RestaurantEntity> restaurantsByName(final String restaurantName) throws RestaurantNotFoundException {
        if (restaurantName.isEmpty()) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }

        List<RestaurantEntity> restaurantListByRating = restaurantDao.getAllRestaurantsByRating();
        List<RestaurantEntity> matchingRestaurantList = new ArrayList<>();

        for (RestaurantEntity restaurantEntity : restaurantListByRating) {
            if (restaurantEntity.getRestaurantName().toLowerCase().contains(restaurantName.toLowerCase())) {
                matchingRestaurantList.add(restaurantEntity);
            }
        }

        return matchingRestaurantList;
    }

    /**
     *  returns all the restaurants based on the input category Id
     *  @param=category Id
     *  @return updated restaurant details
     **/
    public List<RestaurantEntity> restaurantByCategory(final String categoryId) throws CategoryNotFoundException {

        if (categoryId.equals("")) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryId);

        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No Category By this id");
        }

        List<RestaurantEntity> restaurantListByCategoryId = categoryEntity.getRestaurants();
        restaurantListByCategoryId.sort(Comparator.comparing(RestaurantEntity::getRestaurantName));

        return restaurantListByCategoryId;
    }

    /**
     *  updates the restaurant ratings if input is between 1&5
     *  @param=restaurant Id
     *  @return updated restaurant details
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity, Double newRating)
            throws InvalidRatingException {
        if (newRating < 1.0 || newRating > 5.0) {
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        Double newAvgRating =
                ((restaurantEntity.getCustomerRating().doubleValue()) *
                        ((double) restaurantEntity.getNumberCustomersRated()) + newRating) /
                        ((double) restaurantEntity.getNumberCustomersRated() + 1);

        restaurantEntity.setCustomerRating(newAvgRating);
        restaurantEntity.setNumberCustomersRated(restaurantEntity.getNumberCustomersRated() + 1);

        return restaurantDao.updateRestaurantEntity(restaurantEntity);
    }

    /**
     *  returns the restaurant based on input restaurant ID
     *  @param=restaurant Id
     *  @return Resturant
     **/
    public RestaurantEntity restaurantByUUID(String uuid) throws RestaurantNotFoundException {
        if (uuid.equals("")) {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }

        RestaurantEntity restaurantByRestaurantId = restaurantDao.restaurantByUUID(uuid);

        if (restaurantByRestaurantId == null) {
            throw new RestaurantNotFoundException("RNF-001", "No Restaurant By this Id");
        }

        return restaurantByRestaurantId;
    }
}
