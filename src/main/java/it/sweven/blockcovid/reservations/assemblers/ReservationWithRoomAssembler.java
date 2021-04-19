package it.sweven.blockcovid.reservations.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.reservations.controllers.*;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ReservationWithRoomAssembler
    implements RepresentationModelAssembler<ReservationWithRoom, EntityModel<ReservationWithRoom>> {
  @Override
  public EntityModel<ReservationWithRoom> toModel(ReservationWithRoom entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(NewReservationController.class).book(null, null))
            .withRel("new_reservation"),
        linkTo(methodOn(ModifyReservationController.class).modifyReservation(null, null, null))
            .withRel("modify_reservation"),
        linkTo(methodOn(GetDeskStateAtTimeController.class).getDeskState(null, null, null))
            .withRel("desk_status_reservation"),
        linkTo(methodOn(DeleteReservationController.class).deleteReservation(null, null))
            .withRel("delete_reservation"));
  }

  @Override
  public CollectionModel<EntityModel<ReservationWithRoom>> toCollectionModel(
      Iterable<? extends ReservationWithRoom> entities) {
    List<EntityModel<ReservationWithRoom>> entityModels =
        StreamSupport.stream(entities.spliterator(), true)
            .map(this::toModel)
            .collect(Collectors.toList());
    return CollectionModel.of(
        entityModels,
        linkTo(methodOn(ViewPersonalReservationsController.class).viewAll(null, null))
            .withSelfRel());
  }
}
