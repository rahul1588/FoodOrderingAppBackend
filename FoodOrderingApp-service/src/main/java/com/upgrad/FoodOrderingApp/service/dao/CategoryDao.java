package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * method returns the categories by uuid
     * @return uuid
     **/
    public CategoryEntity getCategoryByUuid(final String uuid){
        try{
            return entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class).setParameter("uuid",uuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    /**
     * method returns categories from DB and returns the list of categories
     * @return list
     **/
    public List<CategoryEntity> getAllCategories() {
        try {
            return entityManager.createNamedQuery("allCategories", CategoryEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
