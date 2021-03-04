package it.sweven.blockcovid.routers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

  @RequestMapping(value = "/login")
  public String indexLogin() {
    return "index";
  }

  @RequestMapping(value = "/reservations")
  public String indexReservations() {
    return "index";
  }

  @RequestMapping(value = "/desk")
  public String indexDesk() {
    return "index";
  }
}
