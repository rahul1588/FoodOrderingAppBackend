package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;


    public CustomerAuthEntity getCustomerAuthByAccessToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthByAccessToken", CustomerAuthEntity.class)
                    .setParameter("access_token", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity findByContactNumber(String contactNumber) {
        try {
            CustomerEntity customer = entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class)
                    .setParameter("contactNumber", contactNumber)
                    .getSingleResult();
            return customer;
        } catch (NoResultException nre) {
            return null;
        }
    }


    public CustomerEntity createCustomer(CustomerEntity customer) {
        entityManager.persist(customer);
        return customer;
    }

    public CustomerEntity updateCustomer(CustomerEntity customer) {
        entityManager.merge(customer);
        return customer;
    }

    public CustomerEntity updatePassword(CustomerEntity customer) {
        entityManager.merge(customer);
        return customer;
    }
}
