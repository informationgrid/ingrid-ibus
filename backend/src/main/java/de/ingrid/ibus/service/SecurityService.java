/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
