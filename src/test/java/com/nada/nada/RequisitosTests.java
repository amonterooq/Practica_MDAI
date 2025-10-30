package com.nada.nada;

import com.nada.nada.data.model.Direccion;
import com.nada.nada.data.model.User;
import com.nada.nada.data.repository.DireccionRepository;
import com.nada.nada.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RequisitosTests {

	@Test
	void contextLoads() { //mirar que es esto
	}

	@Test
	void testAlgo() {

    }
}
