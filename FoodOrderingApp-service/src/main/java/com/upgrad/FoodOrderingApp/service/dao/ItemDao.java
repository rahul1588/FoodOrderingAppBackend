package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemEntity getItemById(String itemId) {
        try {
            ItemEntity itemEntity = entityManager.createNamedQuery("itemById", ItemEntity.class)
                    .setParameter("itemId", itemId)
                    .getSingleResult();
            return itemEntity;
        } catch (NoResultException nre) {
            return null;
          }
    }

    public ItemEntity getItemByUUID(String uuid) {
        try {
            return entityManager.createNamedQuery("itemByUUID", ItemEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
