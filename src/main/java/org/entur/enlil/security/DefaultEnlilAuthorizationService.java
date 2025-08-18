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
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.helper.organisation.authorization.DefaultAuthorizationService;

public class DefaultEnlilAuthorizationService<T>
  extends DefaultAuthorizationService<T>
  implements EnlilAuthorizationService<T> {

  private static final String ROLE_ADMIN_DEVIATIONS = "adminDeviations";
  private static final String ROLE_EDIT_DEVIATIONS = "editDeviations";
  private static final String TYPE_OF_ENTITY_DEVIATION_TYPE = "DeviationType";
  private static final String TYPE_OF_ENTITY_DEVIATION_MESSAGE = "DeviationMessage";
  private static final String TYPE_OF_ENTITY_DEVIATION_CANCELLATION = "Cancellation";
  private static final String TYPE_OF_ENTITY_DEVIATION_EXTRA_JOURNEY = "ExtraJourney";

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
    Map<String, Set<Map<String, List<String>>>> codespaceMap = new HashMap<>();

    roleAssignmentExtractor
      .getRoleAssignmentsForUser()
      .stream()
      .filter(roleAssignment -> ROLE_EDIT_DEVIATIONS.equals(roleAssignment.getRole()))
      .forEach(roleAssignment -> {
        codespaceMap.putIfAbsent(roleAssignment.getOrganisation(), new HashSet<>());
        codespaceMap
          .get(roleAssignment.getOrganisation())
          .add(roleAssignment.getEntityClassifications());
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

  private List<Permission> getPermissions(
    Map<String, List<String>> entityClassifications
  ) {
    var permissions = new ArrayList<Permission>();
    if (entityClassifications.containsKey(TYPE_OF_ENTITY_DEVIATION_TYPE)) {
      if (
        entityClassifications
          .get(TYPE_OF_ENTITY_DEVIATION_TYPE)
          .contains(TYPE_OF_ENTITY_DEVIATION_MESSAGE)
      ) {
        permissions.add(Permission.MESSAGES);
      }
      if (
        entityClassifications
          .get(TYPE_OF_ENTITY_DEVIATION_TYPE)
          .contains(TYPE_OF_ENTITY_DEVIATION_CANCELLATION)
      ) {
        permissions.add(Permission.CANCELLATIONS);
      }
      if (
        entityClassifications
          .get(TYPE_OF_ENTITY_DEVIATION_TYPE)
          .contains(TYPE_OF_ENTITY_DEVIATION_EXTRA_JOURNEY)
      ) {
        permissions.add(Permission.EXTRAJOURNEYS);
      }
    }
    return permissions;
  }

  @Override
  public boolean hasAccessToCodespaceForPermission(T providerId, Permission permission) {
    if (isAdministrator()) {
      return true;
    }

    String providerOrganisation = getProviderOrganisationById.apply(providerId);
    if (providerOrganisation == null) {
      return false;
    }

    return roleAssignmentExtractor
      .getRoleAssignmentsForUser()
      .stream()
      .anyMatch(roleAssignment -> {
        if (
          roleAssignment.getRole().equals(ROLE_EDIT_DEVIATIONS) &&
          roleAssignment
            .getEntityClassifications()
            .containsKey(TYPE_OF_ENTITY_DEVIATION_TYPE)
        ) {
          var entityTypes = roleAssignment
            .getEntityClassifications()
            .get(TYPE_OF_ENTITY_DEVIATION_TYPE);
          if (
            Permission.MESSAGES.equals(permission) &&
            entityTypes.contains(TYPE_OF_ENTITY_DEVIATION_MESSAGE)
          ) {
            return true;
          }
          if (
            Permission.CANCELLATIONS.equals(permission) &&
            entityTypes.contains(TYPE_OF_ENTITY_DEVIATION_CANCELLATION)
          ) {
            return true;
          }
          if (
            Permission.EXTRAJOURNEYS.equals(permission) &&
            entityTypes.contains(TYPE_OF_ENTITY_DEVIATION_EXTRA_JOURNEY)
          ) {
            return true;
          }
        }
        return false;
      });
  }

  public boolean isAdministrator() {
    return roleAssignmentExtractor
      .getRoleAssignmentsForUser()
      .stream()
      .anyMatch(roleAssignment -> ROLE_ADMIN_DEVIATIONS.equals(roleAssignment.getRole()));
  }
}
