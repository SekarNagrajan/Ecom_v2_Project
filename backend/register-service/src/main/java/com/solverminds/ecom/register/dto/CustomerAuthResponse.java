package com.solverminds.ecom.register.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerAuthResponse {
    
    private boolean authenticated;
    private String message;
    private String username;

    public CustomerAuthResponse() {
    }

    public CustomerAuthResponse(boolean authenticated, String message, String username) {
        this.authenticated = authenticated;
        this.message = message;
        this.username = username;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
