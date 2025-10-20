package com.feniksovich.bankcards.entity;

import lombok.Getter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum Role {

    USER(1, "ROLE_USER"),
    ADMIN(2, "ROLE_ADMIN");

    private final int priority;
    private final Set<SimpleGrantedAuthority> authorities;

    Role(int priority, String... authorities) {
        this.priority = priority;
        this.authorities = Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public static RoleHierarchy hierarchy() {
        final Role[] roles = values();
        if (roles.length < 2) {
            return RoleHierarchyImpl.fromHierarchy("");
        }

        final Role[] sortedByPriorityDesc = Arrays.stream(roles)
                .sorted(Comparator.comparingInt(Role::getPriority).reversed())
                .toArray(Role[]::new);

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sortedByPriorityDesc.length - 1; i++) {
            builder
                    .append("ROLE_")
                    .append(sortedByPriorityDesc[i].name())
                    .append(" > ")
                    .append("ROLE_")
                    .append(sortedByPriorityDesc[i + 1].name());
            if (i < sortedByPriorityDesc.length - 2) {
                builder.append('\n');
            }
        }

        return RoleHierarchyImpl.fromHierarchy(builder.toString());
    }
}