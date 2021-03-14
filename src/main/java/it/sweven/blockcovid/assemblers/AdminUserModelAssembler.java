package it.sweven.blockcovid.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.routers.AdminRouter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class AdminUserModelAssembler
    implements RepresentationModelAssembler<User, EntityModel<User>> {

  /**
   * TODO add all admin-specific methods applicable on the entity User
   *
   * @param user the user to represent as model
   * @return An entity model representing the given user
   */
  @Override
  public EntityModel<User> toModel(User user) {

    try {
      return EntityModel.of(
          user,
          linkTo(
                  methodOn(AdminRouter.class)
                      .register(
                          new Credentials(
                              user.getUsername(), user.getPassword(), user.getAuthorities()),
                          ""))
              .withSelfRel());
    } catch (Exception e) {
      return EntityModel.of(user);
    }
  }
}
