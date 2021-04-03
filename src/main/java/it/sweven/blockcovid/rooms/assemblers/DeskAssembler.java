package it.sweven.blockcovid.rooms.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.rooms.controllers.AdminNewDeskController;
import it.sweven.blockcovid.rooms.dto.DeskWithRoomName;
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
public class DeskAssembler
    implements RepresentationModelAssembler<DeskWithRoomName, EntityModel<DeskWithRoomName>> {
  private Set<Authority> authorities = Collections.emptySet();

  public DeskAssembler setAuthorities(Set<Authority> authorities) {
    this.authorities = Objects.requireNonNullElse(authorities, Collections.emptySet());
    return this;
  }

  private void clearAuthorities() {
    setAuthorities(Collections.emptySet());
  }

  @Override
  public EntityModel<DeskWithRoomName> toModel(DeskWithRoomName entity) {
    EntityModel<DeskWithRoomName> deskModel = EntityModel.of(entity);
    if (authorities.contains(Authority.ADMIN))
      deskModel.add(
          linkTo(methodOn(AdminNewDeskController.class).addDesk("", null, null))
              .withRel("add_desk"));
    clearAuthorities();
    return deskModel;
  }

  @Override
  public CollectionModel<EntityModel<DeskWithRoomName>> toCollectionModel(
      Iterable<? extends DeskWithRoomName> entities) {
    List<EntityModel<DeskWithRoomName>> entityModels =
        StreamSupport.stream(entities.spliterator(), true)
            .map(this::toModel)
            .collect(Collectors.toList());
    return CollectionModel.of(entityModels);
  }
}
