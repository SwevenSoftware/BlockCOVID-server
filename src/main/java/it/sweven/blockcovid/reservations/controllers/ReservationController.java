package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/reservations")
@Tag(name = "Reservations")
@SecurityRequirement(name = "bearer")
public interface ReservationController {}
