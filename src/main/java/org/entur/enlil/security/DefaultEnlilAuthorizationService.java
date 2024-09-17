package org.entur.enlil.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.entur.enlil.security.model.Codespace;
import org.entur.enlil.security.model.Permission;
import org.entur.enlil.security.spi.EnlilAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.helper.organisation.authorization.DefaultAuthorizationService;

public class DefaultEnlilAuthorizationService<T>
  extends DefaultAuthorizationService<T>
  implements EnlilAuthorizationService<T> {

  public static final String ROLE_EDIT_SX = "editSX";
  public static final String ROLE_EDIT_EXTRAJOURNEYS = "editExtraJourneys";
  private final Function<T, String> getProviderOrganisationById;
  private final RoleAssignmentExtractor roleAssignmentExtractor;

  public DefaultEnlilAuthorizationService(
    RoleAssignmentExtractor roleAssignmentExtractor
  ) {
    super(roleAssignmentExtractor);
    this.getProviderOrganisationById = t -> null;
    this.roleAssignmentExtractor = roleAssignmentExtractor;
  }

  public DefaultEnlilAuthorizationService(
    Function<T, String> getProviderOrganisationById,
    RoleAssignmentExtractor roleAssignmentExtractor
  ) {
    super(getProviderOrganisationById, roleAssignmentExtractor);
    this.getProviderOrganisationById = getProviderOrganisationById;
    this.roleAssignmentExtractor = roleAssignmentExtractor;
  }

  @Override
  public List<Codespace> getAllowedCodespaces() {
    Map<String, Set<String>> codespaceMap = new HashMap<>();

    roleAssignmentExtractor
      .getRoleAssignmentsForUser()
      .stream()
      .filter(roleAssignment ->
        List.of(ROLE_EDIT_SX, ROLE_EDIT_EXTRAJOURNEYS).contains(roleAssignment.getRole())
      )
      .forEach(roleAssignment -> {
        codespaceMap.putIfAbsent(roleAssignment.getOrganisation(), new HashSet<>());
        codespaceMap.get(roleAssignment.getOrganisation()).add(roleAssignment.getRole());
      });

    return codespaceMap
      .entrySet()
      .stream()
      .map(entry ->
        new Codespace(
          entry.getKey(),
          entry
            .getValue()
            .stream()
            .map(this::getPermissions)
            .flatMap(Collection::stream)
            .toList()
        )
      )
      .toList();
  }

  private List<Permission> getPermissions(String role) {
    if (ROLE_EDIT_SX.equals(role)) {
      return List.of(Permission.MESSAGES, Permission.CANCELLATIONS);
    } else if (ROLE_EDIT_EXTRAJOURNEYS.equals(role)) {
      return List.of(Permission.EXTRAJOURNEYS);
    } else {
      throw new IllegalArgumentException("Unsupported role: " + role);
    }
  }

  @Override
  public boolean hasAccessToCodespaceForPermission(T providerId, Permission permission) {
    String providerOrganisation = getProviderOrganisationById.apply(providerId);
    if (providerOrganisation == null) {
      return false;
    }

    if (
      Permission.MESSAGES.equals(permission) ||
      Permission.CANCELLATIONS.equals(permission)
    ) {
      return roleAssignmentExtractor
        .getRoleAssignmentsForUser()
        .stream()
        .anyMatch(roleAssignment ->
          matchProviderRole(roleAssignment, ROLE_EDIT_SX, providerOrganisation)
        );
    } else if (Permission.EXTRAJOURNEYS.equals(permission)) {
      return roleAssignmentExtractor
        .getRoleAssignmentsForUser()
        .stream()
        .anyMatch(roleAssignment ->
          matchProviderRole(roleAssignment, ROLE_EDIT_EXTRAJOURNEYS, providerOrganisation)
        );
    } else {
      throw new IllegalArgumentException("Unsupported permission: " + permission);
    }
  }

  /**
   * Return true if the role assignment gives access to the given role for the given provider.
   */
  private static boolean matchProviderRole(
    RoleAssignment roleAssignment,
    String role,
    String providerOrganisation
  ) {
    return (
      role.equals(roleAssignment.getRole()) &&
      providerOrganisation.equals(roleAssignment.getOrganisation())
    );
  }
}
