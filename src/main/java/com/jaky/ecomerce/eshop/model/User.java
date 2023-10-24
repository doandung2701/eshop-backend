package com.jaky.ecomerce.eshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Set;
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "username", "activationCode"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {
    /**
     * The unique code of the object.
     * The @Id annotation says that the field is the key for the current object.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * User name.
     * The @NotBlank annotation says the field should not be empty.
     */
    @NotBlank(message = "Username cannot be empty")
    private String username;

    /**
     * User password for logging into account on the site.
     * The @NotBlank annotation says the field should not be empty.
     */
    @NotBlank(message = "Password cannot be empty")
    private String password;

    /**
     * User email.
     * The @Email annotation says the string has to be a well-formed email address.
     * The @NotBlank annotation says the field should not be empty.
     */
    @Email(message = "Incorrect email")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    /**
     * Provides access to the site if the user has confirmed the activation code on his email.
     */
    private boolean active;

    /**
     * Activation code that is sent to the user's email.
     */
    private String activationCode;

    /**
     * Password reset code that is sent to the user's email.
     */
    private String passwordResetCode;

    /**
     * User role. User can have multiple roles.
     * Sampling on first access to the current object.
     * The value of the field (id of the {@link User}) is stored in the "user_id" column.
     */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    /**
     * Returns a boolean value depending on the account expiration date.
     * Indicates whether the user's account has expired. An expired account cannot be authenticated.
     * Implemented interface method {@link UserDetails}.
     *
     * @return {@code true} - if the user's account is valid (ie non-expired).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
     * Implemented interface method {@link UserDetails}.
     *
     * @return {@code true} - if the user is not locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired credentials prevent authentication.
     * Implemented interface method {@link UserDetails}.
     *
     * @return {@code true} if the user's credentials are valid (ie non-expired).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
     * Implemented interface method {@link UserDetails}.
     *
     * @return {@code true} if the user is enabled.
     */
    @Override
    public boolean isEnabled() {
        return isActive();
    }

    /**
     * Returns the authorities granted to the user.
     * Implemented interface method {@link UserDetails}.
     *
     * @return object of type {@link Set} is a list of user roles.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }
}