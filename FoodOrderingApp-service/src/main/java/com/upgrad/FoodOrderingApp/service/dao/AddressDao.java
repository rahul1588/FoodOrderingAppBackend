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
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * method fetches address from database
     * @return address
     **/
    public AddressEntity getAddressById(String addressId) {
        try {
            AddressEntity addressEntity = entityManager.createNamedQuery("addressById", AddressEntity.class)
                    .setParameter("addressId", addressId)
                    .getSingleResult();
            return addressEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    public AddressEntity saveAddress(final AddressEntity address) {
        entityManager.persist(address);
        return address;
    }

    public List<CustomerAddressEntity> getAddressesByCustomer(CustomerEntity customer) {
        try {
            List<CustomerAddressEntity> customerAddressEntities = entityManager.createNamedQuery("getAddressesByCustomer", CustomerAddressEntity.class)
                    .setParameter("customer", customer).getResultList();
            return customerAddressEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AddressEntity getAddressByAddressId(final String addressId) {
        try {
            AddressEntity address = entityManager.createNamedQuery("addressById", AddressEntity.class)
                    .setParameter("addressId", addressId).getSingleResult();
            return address;
        } catch (NoResultException nre) {
            return null;
        }
    }
    public AddressEntity deleteAddress(final AddressEntity address) {
        entityManager.remove(address);
        return address;
    }
}
