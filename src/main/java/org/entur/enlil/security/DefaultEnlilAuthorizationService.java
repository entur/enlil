package org.entur.enlil.security;

import java.util.List;
import java.util.function.Function;
import org.entur.enlil.security.spi.EnlilAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.helper.organisation.authorization.DefaultAuthorizationService;

public class DefaultEnlilAuthorizationService<T>
  extends DefaultAuthorizationService<T>
  implements EnlilAuthorizationService<T> {

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
  public List<String> getAllowedCodespacesForEditSX() {
    return roleAssignmentExtractor
      .getRoleAssignmentsForUser()
      .stream()
      .filter(roleAssignment -> "editSX".equals(roleAssignment.getRole()))
      .map(RoleAssignment::getOrganisation)
      .toList();
  }

  @Override
  public boolean canEditSX(T providerId) {
    String providerOrganisation = getProviderOrganisationById.apply(providerId);
    if (providerOrganisation == null) {
      return false;
    }
    return roleAssignmentExtractor
      .getRoleAssignmentsForUser()
      .stream()
      .anyMatch(roleAssignment ->
        matchProviderRole(roleAssignment, "editSX", providerOrganisation)
      );
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
