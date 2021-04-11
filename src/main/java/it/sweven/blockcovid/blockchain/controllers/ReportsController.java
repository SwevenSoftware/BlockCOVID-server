package it.sweven.blockcovid.blockchain.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/reports")
@Tag(name = "Reports")
@SecurityRequirement(name = "bearer")
public interface ReportsController {}
