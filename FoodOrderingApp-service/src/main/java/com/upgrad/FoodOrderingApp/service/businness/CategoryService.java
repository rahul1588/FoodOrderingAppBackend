package com.upgrad.FoodOrderingApp.service.businness;
import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private RestaurantDao restaurantDao;

    /**
     * returns categories of restaurant in alphabetical order.
     * @return takes restaurant uuid as input param and returns sorted categories alphabetically
     **/
    public List<CategoryEntity> getCategoriesByRestaurant(String RestaurantUuid){
        RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(RestaurantUuid);
        return restaurantEntity.getCategories().stream().sorted(Comparator.comparing(CategoryEntity::getCategoryName)).collect(Collectors.toList());
    }

    /**
     * method returns all the categories list ordered by their names.
     * @return List
     **/
    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        return categoryDao.getAllCategories().stream()
                .sorted(Comparator.comparing(CategoryEntity::getCategoryName))
                .collect(Collectors.toList());
    }

    /**
     *  implements the business logic for 'getCategoryById' endpoint
     *  @param=category Id
     *  @return List
     **/
    public CategoryEntity getCategoryById(String categoryId) throws CategoryNotFoundException {

        if(categoryId.equals("")){
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryId);

        if(categoryEntity==null){
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        return categoryEntity;
    }
}
