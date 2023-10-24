package com.jaky.ecomerce.eshop.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author : Jaiky Nguyen
 * @since : 10/24/2023, 9:20 PM
 **/
public enum Role implements GrantedAuthority {
    /**
     * Site customer role.
     */
    USER,

    /**
     * Site administrator role.
     *
     */
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}