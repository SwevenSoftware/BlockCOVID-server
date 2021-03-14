package it.sweven.blockcovid.routers;

import it.sweven.blockcovid.assemblers.AdminUserModelAssembler;
import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.security.Authority;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;
import it.sweven.blockcovid.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AdminRouterTest {

    private AdminRouter router;
    private UserAuthenticationService authenticationService;
    private UserRegistrationService registrationService;
    private AdminUserModelAssembler adminUserModelAssembler;
    private UserService userService;

    @BeforeEach
    void setUp() {
        authenticationService = mock(UserAuthenticationService.class);
        registrationService = mock(UserRegistrationService.class);
        adminUserModelAssembler = mock(AdminUserModelAssembler.class);
        userService = mock(UserService.class);
        router = new AdminRouter(authenticationService, registrationService, adminUserModelAssembler);
    }

    @Test
    void register() {
        Credentials testCredentials = new Credentials("testUser", "testPassword", Set.of(Authority.USER));
        userService.loadUserByUsername("admin");
        router.register(testCredentials, )
    }
}