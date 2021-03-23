package it.sweven.blockcovid.routers.admin;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AdminRegistrationRouterTest {
  private @Autowired MockMvc mockMvc;
  protected @Autowired WebApplicationContext context;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  void validProcess() throws Exception {
    User admin = new User("admin", "pass", Set.of(Authority.ADMIN));
    mockMvc
        .perform(
            post("/api/admin/user/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"username\":\"user\", \"password\":\"password\", \"authorities\":[\"USER\"]}")
                .header(HttpHeaders.AUTHORIZATION, "auth")
                .with(user(admin)))
        .andExpect(status().isOk());
  }

  @Test
  void thisShouldFail() throws Exception {
    mockMvc
        .perform(
            post("/api/admin/user/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"username\":\"user\", \"password\":\"password\", \"authorities\":[\"USER\"]}"))
        .andExpect(status().isUnauthorized());
  }
}
