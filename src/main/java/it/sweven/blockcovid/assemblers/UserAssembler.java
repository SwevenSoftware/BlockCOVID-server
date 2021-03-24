package it.sweven.blockcovid.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.routers.admin.*;
import it.sweven.blockcovid.routers.user.UserInfoRouter;
import it.sweven.blockcovid.routers.user.UserModifyPasswordRouter;
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
            linkTo(methodOn(UserInfoRouter.class).info(null)).withSelfRel(),
            linkTo(methodOn(UserModifyPasswordRouter.class).modifyPassword(null, null))
                .withRel("change_password"));
    if (authorities.contains(Authority.ADMIN)) {
      userModel.add(
          linkTo(methodOn(AdminModifyUserRouter.class).modifyUser(null, entity.getUsername(), null))
              .withRel("modify_user"));
      userModel.add(
          linkTo(methodOn(AdminListUsersRouter.class).listUsers(null)).withRel("list_users"));
      userModel.add(
          linkTo(methodOn(AdminDeleteUserRouter.class).delete("", null)).withRel("delete_user"));
      userModel.add(
          linkTo(methodOn(AdminRegistrationRouter.class).register(null, null))
              .withRel("register_user"));
    }
    clearAuthorities();
    return userModel;
  }
}
