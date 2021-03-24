package it.sweven.blockcovid.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.dto.RoomWithDesks;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.routers.RoomRouter;
import it.sweven.blockcovid.routers.admin.AdminNewRoomRouter;
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
public class RoomWithDesksAssembler
    implements RepresentationModelAssembler<RoomWithDesks, EntityModel<RoomWithDesks>> {
  private Set<Authority> authorities = Collections.emptySet();

  public RoomWithDesksAssembler setAuthorities(Set<Authority> authorities) {
    this.authorities = Objects.requireNonNullElse(authorities, Collections.emptySet());
    return this;
  }

  private void clearAuthorities() {
    setAuthorities(Collections.emptySet());
  }

  @Override
  public EntityModel<RoomWithDesks> toModel(RoomWithDesks entity) {
    EntityModel<RoomWithDesks> roomModel =
        EntityModel.of(
            entity,
            linkTo(methodOn(RoomRouter.class).viewRoom(entity.getRoom().getName(), ""))
                .withSelfRel(),
            linkTo(methodOn(AdminNewRoomRouter.class).newRoom(null, null)).withRel("new_room"),
            linkTo(methodOn(RoomRouter.class).listRooms("")).withRel("list_rooms"));
    clearAuthorities();
    return roomModel;
  }

  @Override
  public CollectionModel<EntityModel<RoomWithDesks>> toCollectionModel(
      Iterable<? extends RoomWithDesks> entities) {
    List<EntityModel<RoomWithDesks>> entityModels =
        StreamSupport.stream(entities.spliterator(), true)
            .map(this::toModel)
            .collect(Collectors.toList());
    return CollectionModel.of(
        entityModels, linkTo(methodOn(RoomRouter.class).listRooms("")).withSelfRel());
  }
}
