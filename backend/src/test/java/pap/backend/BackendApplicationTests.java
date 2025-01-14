package pap.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pap.backend.config.JwtService;
import pap.backend.mail.EmailConfiguration;
import pap.backend.mail.EmailService;

@SpringBootTest
class BackendApplicationTests {

    @MockBean
    private JwtService  jwtService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private EmailConfiguration emailConfiguration;

    @Test
    void contextLoads() {
    }


}
