package it.sweven.blockcovid.reservations.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.reservations.controllers.NewReservationController;
import it.sweven.blockcovid.reservations.entities.Reservation;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class ReservationAssembler
    implements RepresentationModelAssembler<Reservation, EntityModel<Reservation>> {
  @Override
  public EntityModel<Reservation> toModel(Reservation entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(NewReservationController.class).book(null, null))
            .withRel("new_reservation"));
  }
}
