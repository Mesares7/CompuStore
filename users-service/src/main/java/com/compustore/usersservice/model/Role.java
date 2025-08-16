package com.compustore.usersservice.model;

/**
 * Enumeration of application roles.  The names must match the values used in
 * security annotations and JWT claims.  Only two roles are required: ADMIN and
 * CLIENT.  See the dev.to article for examples of restricting controller
 * methods based on roles using @PreAuthorize【923295981449190†L1555-L1605】.
 */
public enum Role {
    ADMIN,
    CLIENT
}