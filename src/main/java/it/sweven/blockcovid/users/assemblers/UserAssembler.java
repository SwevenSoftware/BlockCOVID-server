package it.sweven.blockcovid.users.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import it.sweven.blockcovid.users.controllers.*;
import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
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
            linkTo(methodOn(AccountInfoController.class).info(null)).withSelfRel(),
            linkTo(methodOn(ModifyPasswordController.class).modifyPassword(null, null))
                .withRel("change_password"));
    if (authorities.contains(Authority.ADMIN)) {
      userModel.add(
          linkTo(methodOn(ModifyUserController.class).modifyUser(null, entity.getUsername(), null))
              .withRel("modify_user"));
      userModel.add(
          linkTo(methodOn(ListUsersController.class).listUsers(null)).withRel("list_users"));
      userModel.add(
          linkTo(methodOn(DeleteUserController.class).delete("", null)).withRel("delete_user"));
      userModel.add(
          linkTo(methodOn(RegistrationController.class).register(null, null))
              .withRel("register_user"));
    }
    clearAuthorities();
    return userModel;
  }
}
