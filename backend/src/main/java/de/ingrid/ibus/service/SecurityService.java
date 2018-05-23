package de.ingrid.ibus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * This class handles access to the webapp.
 * If a user has not been configured with a password, then the user is allowed to access the webapp to set the password.
 * Once a password is set, we only allow authenticated users.
 */
@Component("security")
public class SecurityService {

    public boolean isPasswordDefined = false;

    @Value("${spring.security.user.password:}")
    private String password;

    @PostConstruct
    private void init() {
        if (!password.isEmpty()) {
            this.isPasswordDefined = true;
        }
    }

    public boolean hasPermission(Authentication authentication) {
        if (!this.isPasswordDefined || !authentication.getPrincipal().equals("anonymousUser")) {
            return true;
        }
        return false;
    }

}
