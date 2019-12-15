package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerAuthDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerAuthEntity createCustomerAuth(CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    public CustomerAuthEntity findCustAuthByAccessToken(final String accessToken) {
        final CustomerAuthEntity loggedInCustomerAuth;
        try {
            loggedInCustomerAuth = entityManager.createNamedQuery("customerAuthByAccessToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
        return loggedInCustomerAuth;
    }

    public CustomerAuthEntity update(CustomerAuthEntity customerAuthEntity) {
        entityManager.merge(customerAuthEntity);
        return customerAuthEntity;
    }
}
