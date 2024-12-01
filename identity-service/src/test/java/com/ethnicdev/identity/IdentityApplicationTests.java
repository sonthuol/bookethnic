package com.ethnicdev.identity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("/test.properties")
class IdentityApplicationTests {

    @Test
    void contextLoads() {}
}
