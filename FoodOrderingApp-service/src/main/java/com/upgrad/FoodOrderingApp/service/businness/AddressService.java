package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private StateDao stateDao;


    /**
     * to get the Address entity when the Address UUID is passed
     * @return StateEntity
     **/
    public StateEntity getStateByUUID(final String stateUUID) throws AddressNotFoundException {

        StateEntity state = stateDao.findStateByUUID(stateUUID);
        if (state == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return state;
    }

    /**
     * checks if address is empty and gives exception if fields contain invalid data
     * @return AddressEntity
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity address, StateEntity state) throws AddressNotFoundException, SaveAddressException {
        if (addressFieldsEmpty(address))
            throw new SaveAddressException("SAR-001", "No field can be empty");
        if (!validPincode(address.getPincode())) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }
        address.setState(state);

        return addressDao.saveAddress(address);
    }

    /**
     * saves the provided address of the customer
     * @return CustomerAddressEntity
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity saveCustomerAddress(CustomerEntity customer, AddressEntity address) {
        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setCustomer(customer);
        customerAddressEntity.setAddress(address);
        CustomerAddressEntity createdCustomerAddress = customerAddressDao.saveCustomerAddress(customerAddressEntity);
        return createdCustomerAddress;
    }

    /**
     * Gets list of addresses based on customer entity
     * @return List<AddressEntity>
     **/
    public List<AddressEntity> getAllAddress(CustomerEntity customer) {
        List<AddressEntity> addressEntities = new LinkedList<>();
        List<CustomerAddressEntity> customerAddressEntities = addressDao.getAddressesByCustomer(customer);
        if (customerAddressEntities != null) {
            customerAddressEntities.forEach(customerAddressEntity ->
                    addressEntities.add(customerAddressEntity.getAddress()));
        }
        return addressEntities;
    }

    /**
     * Queries for Customer address based on address Entity
     * @return AddressEntity
     **/
    public AddressEntity getAddressByUUID(final String addressId, final CustomerEntity customer) throws AddressNotFoundException, AuthorizationFailedException {

        if (addressId == null) {
            throw new AddressNotFoundException("ANF-005","Address id can not be empty");
        }
        AddressEntity address = addressDao.getAddressByAddressId(addressId);
        if (address == null) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }

        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustomerAddressByAddress(address);
        if (!customerAddressEntity.getCustomer().getUuid().equals(customer.getUuid())) {
            throw new AuthorizationFailedException("ATHR-004","You are not authorized to view/update/delete any one else's address");
        }
        return address;
    }

    /**
     * Method to delete the address from the database
     * @return boolean
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(final AddressEntity addressEntity) {
        AddressEntity deletedAddress = addressDao.deleteAddress(addressEntity);
        return deletedAddress;
    }


    /**
     * fetches all the states from the DB
     * @return List
     **/
    public List<StateEntity> getAllStates() {
        List<StateEntity> states = stateDao.getAllStates();
        return states;
    }

    /**
     * Check if required address fields are empty
     * @return boolean
     **/
    private boolean addressFieldsEmpty(AddressEntity address) {
        if (address.getFlatBuilNo().isEmpty() ||
                address.getLocality().isEmpty() ||
                address.getCity().isEmpty() ||
                address.getPincode().isEmpty() )
            return true;
        return false;
    }

    /**
     * Verifies if pincode is valid
     * @return boolean
     **/
    private boolean validPincode(String pincode) throws SaveAddressException {
        Pattern p = Pattern.compile("\\d{6}\\b");
        Matcher m = p.matcher(pincode);
        return (m.find() && m.group().equals(pincode));

    }
}
