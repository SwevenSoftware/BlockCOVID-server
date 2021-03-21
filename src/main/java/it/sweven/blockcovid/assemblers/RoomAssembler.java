package it.sweven.blockcovid.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.routers.AdminRouter;
import it.sweven.blockcovid.routers.RoomRouter;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class RoomAssembler implements RepresentationModelAssembler<Room, EntityModel<Room>> {
  private Set<Authority> authorities = Collections.emptySet();

  public RoomAssembler setAuthorities(Set<Authority> authorities) {
    this.authorities = Objects.requireNonNullElse(authorities, Collections.emptySet());
    return this;
  }

  private void clearAuthorities() {
    setAuthorities(Collections.emptySet());
  }

  @Override
  public EntityModel<Room> toModel(Room entity) {
    EntityModel<Room> roomModel =
        EntityModel.of(
            entity,
            linkTo(methodOn(RoomRouter.class).viewRoom(entity.getName(), "")).withSelfRel(),
            linkTo(methodOn(AdminRouter.class).newRoom("", null)).withRel("new_room"));
    clearAuthorities();
    return roomModel;
  }
}
