package it.sweven.blockcovid.users.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.sweven.blockcovid.SpringSecurityAuthTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
// @ComponentScan(excludeFilters={@ComponentScan.Filter(type= FilterType.CUSTOM,classes=
// TypeExcludeFilter.class),})
// @SpringBootTest(classes = SpringSecurityAuthTestConfig.class)
// @AutoConfigureMockMvc
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes =
// SpringSecurityAuthTestConfig.class)
// @SpringJUnitConfig
@WebAppConfiguration
@ContextConfiguration(classes = SpringSecurityAuthTestConfig.class)
/*@SpringBootTest(classes = {WebSecurityConfiguration.class,
TokenAuthenticationProvider.class,
UUIDAuthenticationService.class,
UserService.class,
TestConfig.class,
SecretConfiguration.class,
TokenService.class})*/
@AutoConfigureMockMvc
// @WebMvcTest(LoginController.class)
@EnableMongoRepositories
// @ContextConfiguration(classes = {WebSecurityConfiguration.class,
// TokenAuthenticationProvider.class, UUIDAuthenticationService.class, UserService.class})
public class AccountInfoControllerIntegrationTest {

  /*@Autowired
  private WebApplicationContext webApplicationContext;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
      mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }*/

  @Autowired private MockMvc mockMvc;

  /*@MockBean
  private UserAuthenticationService authenticationService;

  @MockBean
  private UserAssembler userAssembler;*/

  @Test
  void test1() {
    assertTrue(true);
  }

  @Test
  //    @WithUserDetails("admin")
  void test2() throws Exception {
    mockMvc.perform(get("/api/account/info")).andDo(print()).andExpect(status().isOk()).andReturn();
  }
}
