package com.svm.ecom.domain.user;

/**
 * API endpoint permission row from {@code ecomapi_user_api_permissions}.
 * A matched endpoint grants access for all HTTP methods.
 */
public class UserApiPermissionRecord {

    private final String apiEndpoint;

    public UserApiPermissionRecord(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }
}
