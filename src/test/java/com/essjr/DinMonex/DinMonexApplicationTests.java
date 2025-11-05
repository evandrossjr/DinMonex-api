package com.essjr.DinMonex;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@Disabled("Ignorado no pipeline, pois carrega o contexto completo")
class DinMonexApplicationTests {

	@Test
	void contextLoads() {
	}

}
