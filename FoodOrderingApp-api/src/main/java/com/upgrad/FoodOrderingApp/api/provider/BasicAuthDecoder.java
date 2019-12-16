package com.upgrad.FoodOrderingApp.api.provider;

import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;

import java.util.Base64;

public class BasicAuthDecoder {

    private final String username;
    private final String password;

    public BasicAuthDecoder(final String base64EncodedCredential) throws AuthenticationFailedException {
        final String[] decodedCredentials;
        try {
            decodedCredentials = new String(Base64.getDecoder().decode(base64EncodedCredential.split("Basic ")[1])).split(":");
            username = decodedCredentials[0];
            password = decodedCredentials[1];
        }
        catch (Exception exc) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }

    }

    public String getUsername() {
        return this.username;
    }

    public  String getPassword() {
        return this.password;
    }
}
