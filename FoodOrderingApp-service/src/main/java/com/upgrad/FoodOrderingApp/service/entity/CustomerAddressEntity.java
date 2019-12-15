package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name="customer_address")
@NamedQueries({
        @NamedQuery(name = "getAllCustomerAddressByCustomer",query = "SELECT c from CustomerAddressEntity c where c.customer = :customer_entity"),
        @NamedQuery(name = "getCustomerAddressByAddress",query = "SELECT c from CustomerAddressEntity c where c.address = :address_entity"),
        @NamedQuery(name = "getAddressesByCustomer", query = "SELECT cae FROM CustomerAddressEntity cae WHERE cae.customer = :customer"),
        @NamedQuery(name = "customerAddressByAddress", query = "SELECT cae FROM CustomerAddressEntity cae WHERE cae.address = :address")
})

public class CustomerAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="customer_id")
    private CustomerEntity customer;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="address_id")
    private AddressEntity address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public CustomerAddressEntity() {
    }
}
