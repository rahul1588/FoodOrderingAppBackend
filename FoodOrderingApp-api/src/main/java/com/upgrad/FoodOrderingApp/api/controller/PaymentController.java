package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> getPaymentMethods() {
        List<PaymentEntity> paymentList = paymentService.getAllPaymentMethods();
        PaymentListResponse paymentListResponse;
        List<PaymentResponse> paymentMethods = new LinkedList<PaymentResponse>();
        for(PaymentEntity paymentEntity: paymentList){
            PaymentResponse paymentResponse = new PaymentResponse().id(UUID.fromString(paymentEntity.getUuid())).paymentName(paymentEntity.getPaymentName());
            paymentMethods.add(paymentResponse);
        }
        paymentListResponse = new PaymentListResponse().paymentMethods(paymentMethods);
        return new ResponseEntity<PaymentListResponse>(paymentListResponse, HttpStatus.OK);
    }
}
