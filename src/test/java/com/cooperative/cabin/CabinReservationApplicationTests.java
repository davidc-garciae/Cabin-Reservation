package com.cooperative.cabin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestBeansConfiguration.class)
@org.springframework.test.context.ActiveProfiles("test")
class CabinReservationApplicationTests {

	@Test
	void contextLoads() {
	}

}
