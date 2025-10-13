package com.nada.nada;

import com.nada.nada.data.model.Direccion;
import com.nada.nada.data.model.User;
import com.nada.nada.data.repository.DireccionRepository;
import com.nada.nada.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class UserDemoApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DireccionRepository direccionRepository;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void testUserDireccionRelationship() {
		// Crear un usuario
		User user = new User("Juan Perez");

		// Crear direcciones asociadas al usuario
		Direccion dir1 = new Direccion("Calle Falsa 123", user);
		Direccion dir2 = new Direccion("Avenida Siempre Viva 456", user);
		user.addDireccion(dir1);
		user.addDireccion(dir2);

		userRepository.save(user);

        User fetchedUser = userRepository.findById(user.getDNI()).orElse(null);
        assertThat(fetchedUser).isNotNull();
        System.out.println(fetchedUser.getDirecciones());
        assertThat(fetchedUser.getDirecciones()).hasSize(2);

        // Modificar una dirección
        fetchedUser.getDirecciones().get(0).setName("Nueva Calle Falsa 123");
        userRepository.save(fetchedUser);

        // Fetch again para verificar la modificación
        User updatedUser = userRepository.findById(user.getDNI()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getDirecciones()).hasSize(2);
        assertThat(updatedUser.getDirecciones().get(0).getName()).isEqualTo("Nueva Calle Falsa 123");
        assertThat(updatedUser.getDirecciones().get(1).getName()).isEqualTo("Avenida Siempre Viva 456");

        Direccion fetchedDireccion = direccionRepository.findByName("Nueva Calle Falsa 123");
        assertThat(fetchedDireccion).isNotNull();
        assertThat(fetchedDireccion.getUser().getDNI()).isEqualTo(user.getDNI());

    }
}
