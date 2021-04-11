package it.sweven.blockcovid.users.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/users")
@Tag(name = "Users")
@SecurityRequirement(name = "bearer")
public interface UsersController {}
