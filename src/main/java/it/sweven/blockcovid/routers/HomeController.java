package it.sweven.blockcovid.routers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

  @RequestMapping(value = "/login")
  public String indexLogin() {
    return "index";
  }
}
