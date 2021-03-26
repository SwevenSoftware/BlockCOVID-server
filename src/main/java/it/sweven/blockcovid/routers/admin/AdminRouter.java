package it.sweven.blockcovid.routers.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/admin")
@Tag(name = "Admin")
public interface AdminRouter {}
