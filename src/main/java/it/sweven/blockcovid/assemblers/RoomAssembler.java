package it.sweven.blockcovid.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.routers.AdminRouter;
import it.sweven.blockcovid.routers.RoomRouter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.CollectionModel;
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
            linkTo(methodOn(AdminRouter.class).newRoom("", null)).withRel("new_room"),
            linkTo(methodOn(RoomRouter.class).listRooms("")).withRel("list_rooms"));
    clearAuthorities();
    return roomModel;
  }

  @Override
  public CollectionModel<EntityModel<Room>> toCollectionModel(Iterable<? extends Room> entities) {
    List<EntityModel<Room>> entityModels =
        StreamSupport.stream(entities.spliterator(), true)
            .map(this::toModel)
            .collect(Collectors.toList());
    return CollectionModel.of(
        entityModels, linkTo(methodOn(RoomRouter.class).listRooms("")).withSelfRel());
  }
}
