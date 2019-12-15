package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CustomerAddressDao {

    @PersistenceContext
    private EntityManager entityManager;


    public List<CustomerAddressEntity> getAllCustomerAddressByCustomer(CustomerEntity customerEntity){
        try{
            List <CustomerAddressEntity> customerAddressEntities = entityManager.createNamedQuery("getAllCustomerAddressByCustomer",CustomerAddressEntity.class).setParameter("customer_entity",customerEntity).getResultList();
            return customerAddressEntities;
        }catch (NoResultException nre){
            return null;
        }
    }


    public CustomerAddressEntity saveCustomerAddress(final CustomerAddressEntity cutomerAddress) {
        entityManager.persist(cutomerAddress);
        return cutomerAddress;
    }

    public CustomerAddressEntity getCustomerAddressByAddress(final AddressEntity address) {
        try {
            CustomerAddressEntity customerAddressEntity = entityManager.createNamedQuery("customerAddressByAddress", CustomerAddressEntity.class)
                    .setParameter("address", address).getSingleResult();
            return customerAddressEntity;
        } catch (NoResultException nre) {
            return null;
        }

    }
}