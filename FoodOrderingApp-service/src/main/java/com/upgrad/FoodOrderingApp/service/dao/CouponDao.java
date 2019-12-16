package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CouponDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to get the Coupon from the database given coupon name
     * @param couponName
     * @return
     */
    public CouponEntity getCouponByCouponName(String couponName){
        try{
            CouponEntity couponEntity = entityManager.createNamedQuery("couponByCouponName",CouponEntity.class).setParameter("couponName",couponName).getSingleResult();
            return couponEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    /**
     * Get the Coupon from DB given Coupon UUID
     * @param couponId
     * @return
     */
    public CouponEntity getCouponByUUID(String couponId) {
        final CouponEntity couponEntity;
        try {
            couponEntity = entityManager.createNamedQuery("couponByUUID", CouponEntity.class)
                    .setParameter("couponId", couponId)
                    .getSingleResult();
            return couponEntity;
        }catch (NoResultException nre){
            return null;
        }
    }
}
