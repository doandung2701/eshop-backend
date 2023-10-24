package com.jaky.ecomerce.eshop.controller;

import com.jaky.ecomerce.eshop.dto.AuthenticationRequestDTO;
import com.jaky.ecomerce.eshop.dto.PasswordResetDto;
import com.jaky.ecomerce.eshop.model.User;
import com.jaky.ecomerce.eshop.security.JwtProvider;
import com.jaky.ecomerce.eshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Jaiky Nguyen
 * @since : 10/24/2023, 9:43 PM
 **/
@RestController
@RequestMapping("/api/v1/rest")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;


    private final UserService userService;

    private final JwtProvider jwtProvider;

    /**
     * Authenticate user in system.
     * URL request {"/login"}, method POST.
     *
     * @param request data transfer object with user email and password.
     * @return ResponseEntity with HTTP response: status code, headers, and body.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = userService.findByEmail(request.getEmail());
            String userRole = user.getRoles().iterator().next().name();
            String token = jwtProvider.createToken(request.getEmail(), userRole);
            Map<Object, Object> response = new HashMap<>();
            response.put("email", request.getEmail());
            response.put("token", token);
            response.put("userRole", userRole);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Incorrect password or email", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Send password reset code to user email.
     * URL request {"/forgot"}, method POST.
     *
     * @param passwordReset data transfer object with user email.
     * @return ResponseEntity with HTTP response: status code, headers, and body.
     */
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetDto passwordReset) {
        boolean forgotPassword = userService.sendPasswordResetCode(passwordReset.getEmail());

        if (!forgotPassword) {
            return new ResponseEntity<>("Email not found", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Reset password code is send to your E-mail", HttpStatus.OK);
    }

    /**
     * Get password reset code from email.
     * URL request {"/reset/{code}"}, method GET.
     *
     * @param code code from email.
     * @return ResponseEntity with HTTP response: status code, headers, and body.
     */
    @GetMapping("/reset/{code}")
    public ResponseEntity<?> getPasswordResetCode(@PathVariable String code) {
        User user = userService.findByPasswordResetCode(code);

        if (user == null) {
            return new ResponseEntity<>("Password reset code is invalid!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Reset user password.
     * URL request {"/reset"}, method POST.
     *
     * @param passwordReset data transfer object with user email and password.
     * @return ResponseEntity with HTTP response: status code, headers, and body.
     */
    @PostMapping("/reset")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordResetDto passwordReset) {
        Map<String, String> errors = new HashMap<>();
        boolean isConfirmEmpty = StringUtils.isEmpty(passwordReset.getPassword2());
        boolean isPasswordDifferent = passwordReset.getPassword() != null &&
                !passwordReset.getPassword().equals(passwordReset.getPassword2());

        if (isConfirmEmpty) {
            errors.put("password2Error", "Password confirmation cannot be empty");

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        if (isPasswordDifferent) {
            errors.put("passwordError", "Passwords do not match");

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        userService.passwordReset(passwordReset);
        return new ResponseEntity<>("Password successfully changed!", HttpStatus.OK);
    }
}
