package com.jaky.ecomerce.eshop.dto;

/**
 * @author : Jaiky Nguyen
 * @since : 10/24/2023, 9:31 PM
 **/
import lombok.Data;

@Data
public class PasswordResetDto {
    private String email;
    private String password;
    private String password2;
}
