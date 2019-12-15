package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerAuthDao customerAuthDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;


    /**
     * Checks if the email address is valid or not
     * @return Customer id object
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity newCustomer) throws SignUpRestrictedException {

        CustomerEntity existingCustomer = customerDao.findByContactNumber(newCustomer.getContactNumber());

        if (existingCustomer != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number");
        }

        if (!fieldsComplete(newCustomer)) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

        if (!validEmailAddress(newCustomer.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        if (!validContactNumber(newCustomer.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }

        if (!validPassword(newCustomer.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }

        encryptPassword(newCustomer);
        return customerDao.createCustomer(newCustomer);

    }

    /**
     * Check if the given username exists in the database
     * @return Customer id object
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        CustomerEntity registeredCustomer = customerDao.findByContactNumber(username);
        if (registeredCustomer == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, registeredCustomer.getSalt());
        if (registeredCustomer.getPassword().equals(encryptedPassword)) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setUuid(UUID.randomUUID().toString());
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(registeredCustomer.getUuid(), now, expiresAt));
            customerAuthEntity.setExpiresAt(expiresAt);
            customerAuthEntity.setCustomer(registeredCustomer);
            customerAuthEntity.setLoginAt(now);
            CustomerAuthEntity authCustomer = customerAuthDao.createCustomerAuth(customerAuthEntity);
            return authCustomer;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
    }

    /**
     * logout customer location
     * @return Customer id object
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        final ZonedDateTime now;
        CustomerAuthEntity loggedInCustomerAuth = customerAuthDao.findCustAuthByAccessToken(accessToken);
        if (loggedInCustomerAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if (loggedInCustomerAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        now = ZonedDateTime.now(ZoneId.systemDefault());
        if (loggedInCustomerAuth.getExpiresAt().isBefore(now) || loggedInCustomerAuth.getExpiresAt().isEqual(now)) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        loggedInCustomerAuth.setLogoutAt(ZonedDateTime.now(ZoneId.systemDefault()));
        CustomerAuthEntity loggedOutCustomerAuth = customerAuthDao.update(loggedInCustomerAuth);
        return loggedOutCustomerAuth;
    }

    /**
     * update customer details in the database
     * @return Customer updated id
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final CustomerEntity customer) throws AuthorizationFailedException, UpdateCustomerException {
        CustomerEntity updatedCustomer = customerDao.updateCustomer(customer);
        return updatedCustomer;
    }

    /**
     * update customer password in the database
     * @return Customer updated id
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(final String oldPassword, final String newPassword, final CustomerEntity customer) throws AuthorizationFailedException, UpdateCustomerException {
        if (!validPassword(newPassword)) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }

        String encryptedOldPassword = passwordCryptographyProvider.encrypt(oldPassword, customer.getSalt());
        if (!encryptedOldPassword.equals(customer.getPassword())) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }

        customer.setPassword(newPassword);
        encryptPassword(customer);
        CustomerEntity updatedCustomer = customerDao.updatePassword(customer);
        return updatedCustomer;
    }

    /**
     * gets customer details from the database
     * @return Customer updated id
     **/
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity getCustomer(final String accessToken) throws AuthorizationFailedException {

        CustomerAuthEntity customerAuth = customerAuthDao.findCustAuthByAccessToken(accessToken);
        final ZonedDateTime now;
        if (customerAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if (customerAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        now = ZonedDateTime.now(ZoneId.systemDefault());
        if (customerAuth.getExpiresAt().isBefore(now) ||  customerAuth.getExpiresAt().isEqual(now)) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        return customerAuth.getCustomer();
    }

    /**
     * Checks if customer fields are empty or not
     * @return boolean
     **/
    private boolean fieldsComplete(CustomerEntity customer) throws SignUpRestrictedException {
        if ( customer.getFirstName().isEmpty()||
                customer.getContactNumber().isEmpty()||
                customer.getEmail().isEmpty() ||
                customer.getPassword().isEmpty() )
            return false;
        else
            return true;
    }

    /**
     * Checks if the email address is valid or not
     * @return boolean
     **/
    private boolean validEmailAddress(String email) {
        String regex = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher= pattern.matcher(email);
        return  matcher.matches();
    }

    /**
     * Checks if the contact number is valid or not
     * @return boolean
     **/
    private boolean validContactNumber(String contact) {
        String regex = "[0-9]{10}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contact);
        if (matcher.find() && matcher.group().equals(contact))
            return true;
        return false;
    }

    /**
     * Checks if the password is valid or not
     * @return boolean
     **/
    private boolean validPassword(String password) {
        String regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#@$%&*!^]).{8,}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * Method to encrypt the password
     * @return void
     **/
    private void encryptPassword(final CustomerEntity newCustomer) {
        String password = newCustomer.getPassword();
        final String[] encryptedData = passwordCryptographyProvider.encrypt(password);
        newCustomer.setSalt(encryptedData[0]);
        newCustomer.setPassword(encryptedData[1]);
    }
}
