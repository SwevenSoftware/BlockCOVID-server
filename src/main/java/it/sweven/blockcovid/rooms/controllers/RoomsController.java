package it.sweven.blockcovid.rooms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/rooms")
@Tag(name = "Rooms")
@SecurityRequirement(name = "bearer")
public interface RoomsController {}
