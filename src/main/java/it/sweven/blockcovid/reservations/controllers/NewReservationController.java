package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.BadTimeIntervals;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import javax.management.BadAttributeValueExpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class NewReservationController implements ReservationController {
  private final ReservationService reservationService;
  private final ReservationWithRoomAssembler reservationWithRoomAssembler;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  public NewReservationController(
      ReservationService reservationService,
      ReservationWithRoomAssembler reservationWithRoomAssembler) {
    this.reservationService = reservationService;
    this.reservationWithRoomAssembler = reservationWithRoomAssembler;
  }

  @PostMapping("reservation")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "reservation successfully made"),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Another reservation clashes with yours",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @ResponseBody
  @PreAuthorize("#submitter.isUser()")
  public EntityModel<ReservationWithRoom> book(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @RequestBody ReservationInfo reservationInfo) {
    if (reservationInfo.getStart() != null
        && reservationInfo.getEnd() != null
        && reservationInfo.getDeskId() != null
        && !reservationInfo.getStart().isBefore(LocalDateTime.now().minusMinutes(2))
        && reservationInfo.getStart().isBefore(reservationInfo.getEnd())) {
      try {
        return reservationWithRoomAssembler.toModel(
            reservationService.addReservation(reservationInfo, submitter.getUsername()));
      } catch (ReservationClash reservationClash) {
        logger.warn(
            "A reservation clash was detected, therefore the reservation "
                + reservationInfo
                + " was not made for the user "
                + submitter.getUsername());
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "Another reservation for the same desk clashes with yours");
      } catch (BadTimeIntervals badTimeIntervals) {
        logger.warn(
            "The reservation " + reservationInfo + "was not made due to bad time intervals");
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Your reservation must be inside the room opening time interval");
      } catch (DeskNotFoundException deskNotFoundException) {
        logger.warn("The reservation " + reservationInfo + "was not made due an invalid deskId");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid desk id");
      } catch (RoomNotFoundException roomNotFoundException) {
        logger.error(
            "The reservation "
                + reservationInfo
                + "was not made due to a deskID associated with no room. "
                + "An inconsistent state of the server is hence detected");
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "desk associated with no room");
      } catch (BadAttributeValueExpException e) {
        logger.warn(
            "The reservation "
                + reservationInfo
                + " was not made  due to bad fields in the request");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid fields in your request");
      }
    } else {
      logger.warn(
          "The reservation "
              + reservationInfo
              + " was not made  due to bad fields in the request caught inside the controller");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid fields in your request");
    }
  }
}
