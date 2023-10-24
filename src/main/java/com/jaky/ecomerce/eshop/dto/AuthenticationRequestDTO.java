package com.jaky.ecomerce.eshop.dto;

import lombok.Data;

/**
 * @author : Jaiky Nguyen
 * @since : 10/24/2023, 9:44 PM
 **/
@Data
public class AuthenticationRequestDTO {
    private String email;
    private String password;
}
