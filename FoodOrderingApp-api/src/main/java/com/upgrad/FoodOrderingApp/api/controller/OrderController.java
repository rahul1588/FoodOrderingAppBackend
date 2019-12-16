package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CustomerService customerService;

    /**
     * Method that implements the endpoint to get coupon name
     * @param couponName
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     * @throws CouponNotFoundException
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(
            @PathVariable(value = "coupon_name") final String couponName,
            @RequestHeader(value = "authorization") final String authorization)
            throws AuthorizationFailedException, CouponNotFoundException {

        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        CouponEntity couponEntity = orderService.getCouponByCouponName(couponName);

        CouponDetailsResponse couponDetails = new CouponDetailsResponse()
                .id(UUID.fromString(couponEntity.getUuid()))
                .couponName(couponEntity.getCouponName())
                .percent(couponEntity.getPercent());
        return new ResponseEntity<CouponDetailsResponse>(couponDetails, HttpStatus.OK);
    }

    /**
     * Method to implement the endpoint to get past orders of customer
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getCustomerOrders(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException
    {
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        List<OrderEntity> orderEntityList = orderService.getOrdersByCustomers(customerEntity.getUuid());

        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();

        for (OrderEntity orderEntity : orderEntityList) {

            OrderListCoupon orderListCoupon = new OrderListCoupon()
                    .id(UUID.fromString(orderEntity.getCoupon().getUuid()))
                    .couponName(orderEntity.getCoupon().getCouponName())
                    .percent(orderEntity.getCoupon().getPercent());

            OrderListPayment orderListPayment = new OrderListPayment()
                    .id(UUID.fromString(orderEntity.getPayment().getUuid()))
                    .paymentName(orderEntity.getPayment().getPaymentName());

            OrderListCustomer orderListCustomer = new OrderListCustomer()
                    .id(UUID.fromString(orderEntity.getCustomer().getUuid()))
                    .firstName(orderEntity.getCustomer().getFirstName())
                    .lastName(orderEntity.getCustomer().getLastName())
                    .emailAddress(orderEntity.getCustomer().getEmail())
                    .contactNumber(orderEntity.getCustomer().getContactNumber());

            OrderListAddressState orderListAddressState = new OrderListAddressState()
                    .id(UUID.fromString(orderEntity.getAddress().getState().getUuid()))
                    .stateName(orderEntity.getAddress().getState().getStateName());

            OrderListAddress orderListAddress = new OrderListAddress()
                    .id(UUID.fromString(orderEntity.getAddress().getUuid()))
                    .flatBuildingName(orderEntity.getAddress().getFlatBuilNo())
                    .locality(orderEntity.getAddress().getLocality())
                    .city(orderEntity.getAddress().getCity())
                    .pincode(orderEntity.getAddress().getPincode())
                    .state(orderListAddressState);

            OrderList orderList = new OrderList()
                    .id(UUID.fromString(orderEntity.getUuid()))
                    .bill(new BigDecimal(orderEntity.getBill()))
                    .coupon(orderListCoupon)
                    .discount(new BigDecimal(orderEntity.getDiscount()))
                    .date(orderEntity.getDate().toString())
                    .payment(orderListPayment)
                    .customer(orderListCustomer)
                    .address(orderListAddress);

            for (OrderItemEntity orderItemEntity : itemService.getItemsByOrder(orderEntity)) {

                ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem()
                        .id(UUID.fromString(orderItemEntity.getItemId().getUuid()))
                        .itemName(orderItemEntity.getItemId().getItemName())
                        .itemPrice(orderItemEntity.getItemId().getPrice())
                        .type(ItemQuantityResponseItem.TypeEnum.fromValue(orderItemEntity.getItemId().getType().getValue()));

                ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse()
                        .item(itemQuantityResponseItem)
                        .quantity(orderItemEntity.getQuantity())
                        .price(orderItemEntity.getPrice());

                orderList.addItemQuantitiesItem(itemQuantityResponse);
            }

            customerOrderResponse.addOrdersItem(orderList);
        }

        return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);
    }

    /**
     * Method to implement the endpoint to save customer orders
     * @param saveOrderRequest
     * @param authorization
     * @return
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/order", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(
            @RequestBody(required = false) final SaveOrderRequest saveOrderRequest,
            @RequestHeader("authorization") String authorization)
            throws AuthorizationFailedException, PaymentMethodNotFoundException, RestaurantNotFoundException,
            ItemNotFoundException, CouponNotFoundException, AddressNotFoundException {


        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUuid(UUID.randomUUID().toString());
        orderEntity.setCoupon(orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString()));
        orderEntity.setPayment(paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString()));
        orderEntity.setCustomer(customerEntity);
        orderEntity.setAddress(addressService.getAddressByUUID(saveOrderRequest.getAddressId(), customerEntity));
        orderEntity.setBill(saveOrderRequest.getBill().doubleValue());
        orderEntity.setDiscount(saveOrderRequest.getDiscount().doubleValue());
        orderEntity.setRestaurant(restaurantService.restaurantByUUID(saveOrderRequest.getRestaurantId().toString()));
        orderEntity.setDate(new Date());
        OrderEntity savedOrderEntity = orderService.saveOrder(orderEntity);


        for (ItemQuantity itemQuantity : saveOrderRequest.getItemQuantities()) {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderId(savedOrderEntity);
            orderItemEntity.setItemId(itemService.getItemByUUID(itemQuantity.getItemId().toString()));
            orderItemEntity.setQuantity(itemQuantity.getQuantity());
            orderItemEntity.setPrice(itemQuantity.getPrice());
            orderService.saveOrderItem(orderItemEntity);
        }

        SaveOrderResponse saveOrderResponse = new SaveOrderResponse()
                .id(savedOrderEntity.getUuid()).status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(saveOrderResponse, HttpStatus.CREATED);
    }

}
