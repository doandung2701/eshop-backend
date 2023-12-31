package com.jaky.ecomerce.eshop.service;

import com.jaky.ecomerce.eshop.dto.PasswordResetDto;
import com.jaky.ecomerce.eshop.model.Role;
import com.jaky.ecomerce.eshop.model.User;
import com.jaky.ecomerce.eshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : Jaiky Nguyen
 * @since : 10/24/2023, 9:32 PM
 **/
@Service("userDetailsServiceImpl")
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    /**
     * Implementation of the {@link UserRepository} interface
     * for working with users with a database.
     */
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    /**
     * Retrieves an User by its id.
     *
     * @param id must not be null.
     * @return User with the given id.
     */
    @Override
    public User getOne(Long id) {
        return userRepository.getOne(id);
    }

    /**
     * Returns the user with the same email as the value of the input parameter.
     *
     * @param email user name to return.
     * @return The {@link User} class object.
     */
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Return list of all registered users.
     *
     * @return list of {@link User}.
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Returns the user with the same name as the value of the input parameter.
     *
     * @param username user name to return.
     * @return The {@link User} class object.
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Returns the user that has the same activation code as the value of the input parameter.
     *
     * @param code activation code to return.
     * @return The {@link User} class object.
     */
    @Override
    public User findByActivationCode(String code) {
        return userRepository.findByActivationCode(code);
    }

    /**
     * Returns the user that has the same password reset code as the value of the input parameter.
     *
     * @param code password reset code.
     * @return The {@link User} class object.
     */
    @Override
    public User findByPasswordResetCode(String code) {
        return userRepository.findByPasswordResetCode(code);
    }

    /**
     * Save user info.
     *
     * @param user user object to return.
     * @return The {@link User} class object which will be saved in the database.
     */
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, LockedException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (user.getActivationCode() != null) {
            throw new LockedException("email not activated");
        }

        return user;
    }

    /**
     * Save user in database and send activation code to user email.
     *
     * @param user user who has registered.
     * @return true if the user is not exists.
     */
    @Override
    public boolean addUser(User user) {
        User userFromDb = userRepository.findByEmail(user.getEmail());

        if (userFromDb != null) {
            return false;
        }
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    /**
     * Send password reset code to user email.
     *
     * @param email users email.
     * @return true if user email is exists.
     */
    @Override
    public boolean sendPasswordResetCode(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return false;
        }
        user.setPasswordResetCode(UUID.randomUUID().toString());
        userRepository.save(user);
        return true;
    }


    /**
     * Reset user password.
     *
     * @param passwordReset data transfer object with user email and password.
     */
    @Override
    public void passwordReset(PasswordResetDto passwordReset) {
        User user = userRepository.findByEmail(passwordReset.getEmail());
        user.setPassword(passwordEncoder.encode(passwordReset.getPassword()));
        user.setPasswordResetCode(null);

        userRepository.save(user);
    }

    /**
     * Return true if activation code is exists.
     *
     * @param code activation code from the database.
     * @return true if activation code is exists.
     */
    @Override
    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            return false;
        }

        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);

        return true;
    }

    /**
     * Save updated user with set of roles.
     *
     * @param username user name of registered user.
     * @param form     form of user roles.
     * @param user     user from the database.
     */
    @Override
    public void userSave(String username, Map<String, String> form, User user) {
        user.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepository.save(user);
    }

    /**
     * Save updated user profile with new password or email.
     *
     * @param user     user from the database.
     * @param password the user's password to be changed.
     * @param email    the user's email to be changed.
     */
    @Override
    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();

        boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));

        if (isEmailChanged) {
            user.setEmail(email);

            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (!StringUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);

    }

}