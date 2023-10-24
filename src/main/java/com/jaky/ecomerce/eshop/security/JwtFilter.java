package com.jaky.ecomerce.eshop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * @author : Jaiky Nguyen
 * @since : 10/24/2023, 8:48 PM
 **/
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    /**
     * Object for generating and verifying JWT.
     */
    private final JwtProvider jwtProvider;

    /**
     * Constructor for initializing the main variables of the JWT filter.
     * The @Autowired annotation will allow Spring to automatically initialize objects.
     *
     * @param jwtProvider object for generating and verifying JWT.
     */


    /**
     * Method of the Filter is called by the container each time a request/response
     * pair is passed through the chain due to a client request for a resource
     * at the end of the chain. The FilterChain passed in to this method allows
     * the Filter to pass on the request and response to the next entity in the chain.
     *
     * @param servletRequest  the request to process.
     * @param servletResponse the response associated with the request.
     * @param filterChain     provides access to the next filter in the chain for this
     *                        filter to pass the request and response to for further
     *                        processing.
     *
     * @throws IOException      if an I/O error occurs during this filter's
     *                          processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtProvider.resolveToken((HttpServletRequest) servletRequest);

        try {
            if (token != null && jwtProvider.validateToken(token)) {
                Authentication authentication = jwtProvider.getAuthentication(token);

                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) servletResponse).sendError(e.getHttpStatus().value());
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
