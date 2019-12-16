package com.upgrad.FoodOrderingApp.api.provider;

import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;

public class BearerAuthDecoder {

    public final String BEARER_AUTH_PREFIX = "Bearer ";
    private final String accessToken;

    public BearerAuthDecoder(final String bearerToken) throws AuthorizationFailedException {
        final String[] bearerTokens = bearerToken.split(BEARER_AUTH_PREFIX);
        if(bearerTokens.length == 2) {
            this.accessToken = bearerTokens[1];
        }
        else {
            this.accessToken = bearerToken;
        }

    }

    public String getAccessToken() {
        return accessToken;
    }
}
