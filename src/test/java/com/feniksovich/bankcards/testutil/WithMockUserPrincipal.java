package com.feniksovich.bankcards.testutil;

import com.feniksovich.bankcards.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.*;
import java.util.Collections;
import java.util.UUID;

/**
 * Test support annotation that populates Spring Security's SecurityContext with a minimal
 * {@link UserPrincipal} without running security filters.
 * <p>
 * Apply to a test method or class to simulate an authenticated user in MockMvc/WebMvc tests
 * where filters are disabled (e.g., {@code @AutoConfigureMockMvc(addFilters = false)}).
 * <p>
 * The factory creates a {@link SecurityContext} that contains a {@link UserPrincipal} with
 * a random user id, phone number "9990000000", and no authorities.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithSecurityContext(factory = WithMockUserPrincipal.Factory.class)
public @interface WithMockUserPrincipal {
    final class Factory implements WithSecurityContextFactory<WithMockUserPrincipal> {
        @Override
        public SecurityContext createSecurityContext(WithMockUserPrincipal annotation) {
            final SecurityContext context = SecurityContextHolder.createEmptyContext();
            final UserPrincipal principal = new UserPrincipal(
                    UUID.randomUUID(),
                    "9990000000",
                    "password",
                    Collections.emptySet()
            );
            final Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            context.setAuthentication(authentication);
            return context;
        }
    }
}


