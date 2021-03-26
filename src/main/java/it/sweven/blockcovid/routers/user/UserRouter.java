package it.sweven.blockcovid.routers.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/user")
@Tag(name = "User")
public interface UserRouter {}
