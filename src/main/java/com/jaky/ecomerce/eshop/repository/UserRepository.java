package com.jaky.ecomerce.eshop.repository;

import com.jaky.ecomerce.eshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Jaiky Nguyen
 * @since : 10/24/2023, 9:33 PM
 **/
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Returns the user from the database that has the same name as the value of the input parameter.
     *
     * @param username user name to return.
     * @return The {@link User} class object.
     */
    User findByUsername(String username);

    /**
     * Returns the user from the database that has the same activation code as the value of the input parameter.
     *
     * @param code activation code to return.
     * @return The {@link User} class object.
     */
    User findByActivationCode(String code);

    /**
     * Returns the user from the database that has the same email as the value of the input parameter.
     *
     * @param email user email to return.
     * @return The {@link User} class object.
     */
    User findByEmail(String email);

    /**
     * Returns the user from the database that has the same password reset code as the value of the input parameter.
     *
     * @param code password reset code.
     * @return The {@link User} class object.
     */
    User findByPasswordResetCode(String code);
}
