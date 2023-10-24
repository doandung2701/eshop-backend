package com.jaky.ecomerce.eshop.security;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
/**
 * @author : Jaiky Nguyen
 * @since : 10/24/2023, 9:07 PM
 **/
@Getter
public class JwtAuthenticationException extends AuthenticationException {
    /**
     * HTTP status codes.
     */
    private HttpStatus httpStatus;

    /**
     * Constructs an JwtAuthenticationException with the specified message.
     *
     * @param msg the detail message.
     */
    public JwtAuthenticationException(String msg) {
        super(msg);
    }

    /**
     * Constructs an JwtAuthenticationException with the specified message and HTTP status code.
     *
     * @param msg        the detail message.
     * @param httpStatus HTTP status.
     */
    public JwtAuthenticationException(String msg, HttpStatus httpStatus) {
        super(msg);
        this.httpStatus = httpStatus;
    }
}
