package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    /**
     * Method called to fetch the payment entity given the payment id
     * @param paymentId
     * @return
     * @throws PaymentMethodNotFoundException
     */
    public PaymentEntity getPaymentByUUID(String paymentId) throws PaymentMethodNotFoundException {
        PaymentEntity paymentEntity = paymentDao.getPayment(paymentId);
        if(paymentEntity == null){
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }else {
            return paymentEntity;
        }
    }

    /**
     * Method called to get all the payment methods
     * @return
     */
    public List<PaymentEntity> getAllPaymentMethods() {
        List<PaymentEntity> paymentEntities = paymentDao.getPaymentMethods();
        return paymentEntities;

    }
}
