package it.sweven.blockcovid.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import it.sweven.blockcovid.controllers.admin.*;
import it.sweven.blockcovid.controllers.user.UserInfoController;
import it.sweven.blockcovid.controllers.user.UserModifyPasswordController;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class UserAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {
  private Set<Authority> authorities = Collections.emptySet();

  public UserAssembler setAuthorities(Set<Authority> authorities) {
    this.authorities = Objects.requireNonNullElse(authorities, Collections.emptySet());
    return this;
  }

  private void clearAuthorities() {
    setAuthorities(Collections.emptySet());
  }

  @Override
  public EntityModel<User> toModel(User entity) {
    EntityModel<User> userModel =
        EntityModel.of(
            entity,
            linkTo(methodOn(UserInfoController.class).info(null)).withSelfRel(),
            linkTo(methodOn(UserModifyPasswordController.class).modifyPassword(null, null))
                .withRel("change_password"));
    if (authorities.contains(Authority.ADMIN)) {
      userModel.add(
          linkTo(
                  methodOn(AdminModifyUserController.class)
                      .modifyUser(null, entity.getUsername(), null))
              .withRel("modify_user"));
      userModel.add(
          linkTo(methodOn(AdminListUsersController.class).listUsers(null)).withRel("list_users"));
      userModel.add(
          linkTo(methodOn(AdminDeleteUserController.class).delete("", null))
              .withRel("delete_user"));
      userModel.add(
          linkTo(methodOn(AdminRegistrationController.class).register(null, null))
              .withRel("register_user"));
    }
    clearAuthorities();
    return userModel;
  }
}
