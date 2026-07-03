package com.techchallenge.restaurant;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class RestaurantApplicationTests {

	@Test
	void testMain(){
		String[] args = {"--spring.profiles.active=test", "--spring.main.web-application-type=none"};

		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
			assertDoesNotThrow(() -> RestaurantApplication.main(args));
			mocked.verify(() -> SpringApplication.run(RestaurantApplication.class, args));
		}
	}

}
