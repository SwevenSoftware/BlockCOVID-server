package it.sweven.blockcovid.rooms.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.rooms.controllers.ListRoomsController;
import it.sweven.blockcovid.rooms.controllers.NewRoomController;
import it.sweven.blockcovid.rooms.controllers.ViewRoomController;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.users.entities.Authority;
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
            linkTo(methodOn(ViewRoomController.class).viewRoom(null, entity.getName(), null, null))
                .withSelfRel(),
            linkTo(methodOn(NewRoomController.class).newRoom(null, null)).withRel("new_room"),
            linkTo(methodOn(ListRoomsController.class).listRooms(null, null, null))
                .withRel("list_rooms"));
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
        entityModels,
        linkTo(methodOn(ListRoomsController.class).listRooms(null, null, null)).withSelfRel());
  }
}
