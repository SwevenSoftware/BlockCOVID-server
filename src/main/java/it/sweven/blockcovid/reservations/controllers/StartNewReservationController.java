package it.sweven.blockcovid.reservations.controllers;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.BadTimeIntervals;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.exceptions.StartingTooEarly;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import javax.management.BadAttributeValueExpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

public class StartNewReservationController implements ReservationController {
  private final ReservationService reservationService;
  private final ReservationWithRoomAssembler reservationWithRoomAssembler;

  @Autowired
  public StartNewReservationController(
      ReservationService reservationService,
      ReservationWithRoomAssembler reservationWithRoomAssembler) {
    this.reservationService = reservationService;
    this.reservationWithRoomAssembler = reservationWithRoomAssembler;
  }

  @PostMapping("start")
  public EntityModel<ReservationWithRoom> start(
      @AuthenticationPrincipal User submitter, @RequestBody ReservationInfo reservationInfo) {
    LocalDateTime now = LocalDateTime.now();
    if (reservationInfo.getEnd() == null || reservationInfo.getDeskId() == null)
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "you must supply deskId and reservation end");
    reservationInfo.setStart(now);
    if (reservationInfo.getStart().isAfter(reservationInfo.getEnd()))
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "You can not start a new reservation that ends in the past");
    try {
      ReservationWithRoom newReservation =
          reservationService.addReservation(reservationInfo, submitter.getUsername());
      return reservationWithRoomAssembler.toModel(
          reservationService.start(newReservation.getId(), now));
    } catch (ReservationClash reservationClash) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Your reservation clashes with some other");
    } catch (BadTimeIntervals badTimeIntervals) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "The desk is not available throughout the whole reservation");
    } catch (BadAttributeValueExpException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad input values");
    } catch (StartingTooEarly startingTooEarly) {
      throw new ResponseStatusException(
          HttpStatus.TOO_EARLY,
          "You can start your reservation maximum 30 minutes before the starting time");
    } catch (NoSuchReservation noSuchReservation) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation not found");
    } catch (Exception exception) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "You are trying to book a non existing desk in a non existing room");
    }
  }
}
