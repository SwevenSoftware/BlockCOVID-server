package it.sweven.blockcovid.reservations.controllers;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.exceptions.StartingTooEarly;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class StartReservationController implements ReservationController {

  private final ReservationService reservationService;
  private final ReservationWithRoomAssembler reservationWithRoomAssembler;

  @Autowired
  public StartReservationController(
      ReservationService reservationService,
      ReservationWithRoomAssembler reservationWithRoomAssembler) {
    this.reservationService = reservationService;
    this.reservationWithRoomAssembler = reservationWithRoomAssembler;
  }

  @PutMapping("start/{reservationID}")
  public EntityModel<ReservationWithRoom> start(
      @AuthenticationPrincipal User submitter, @PathVariable String reservationID) {
    try {
      ReservationWithRoom reservation = reservationService.findById(reservationID);
      if (!reservation.getUsername().equals(submitter.getUsername()))
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "You must be the owner of a reservation in order to start it");
      return reservationWithRoomAssembler.toModel(
          reservationService.start(reservationID, LocalDateTime.now()));
    } catch (NoSuchReservation noSuchReservation) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such reservation");
    } catch (ReservationClash reservationClash) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "your reservation conflict with another one");
    } catch (StartingTooEarly startingTooEarly) {
      throw new ResponseStatusException(
          HttpStatus.TOO_EARLY, "you can start at maximum 30 minutes early");
    }
  }
}
