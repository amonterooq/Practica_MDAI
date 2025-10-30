package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.PrendaRepository;
import com.nada.nada.data.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserPrendaRepositoryTests {

    @Autowired UserRepository userRepository;
    @Autowired PrendaRepository prendaRepository;
    @Autowired EntityManager em;

    @BeforeEach
    void clean() {
        prendaRepository.deleteAll();
        userRepository.deleteAll();
    }

    private PrendaSuperior sup(User u) {
        PrendaSuperior p = new PrendaSuperior();
        p.setUsuario(u);
        p.setCategoria(CategoriaSuperior.CAMISETA);
        p.setColor("Azul");
        p.setMarca("Zara");
        p.setTalla("M");
        return p;
    }

    private PrendaInferior inf(User u) {
        PrendaInferior p = new PrendaInferior();
        p.setUserio(u);
        p.setCategoria(CategoriaInferior.PANTALON);
        p.setColor("Blanco");
        p.setMarca("Levis");
        p.setTalla("40");
        return p;
    }

    private PrendaCalzado calz(User u) {
        PrendaCalzado p = new PrendaCalzado();
        p.setUser(u);
        p.setCategoria("zapatillas");
        p.setColor("Negro");
        p.setMarca("Nike");
        p.setTalla("42");
        return p;
    }

    @Test
    @Transactional
    void usuarioPoseeMultiplesPrendas() {
        User user = new User("Usuario Prendas");
        userRepository.saveAndFlush(user);

        prendaRepository.save(sup(user));
        prendaRepository.save(inf(user));
        prendaRepository.save(calz(user));
        prendaRepository.flush();
        em.clear();

        User fetched = userRepository.findById(user.getDNI()).orElseThrow();
        assertThat(fetched.getPrendas()).hasSize(3);
        assertThat(fetched.getPrendas())
                .extracting("user.DNI")
                .containsOnly(user.getDNI());
    }

    @Test
    @Transactional
    void eliminarPrendaNoEliminaUsuario() {
        User user = new User("Usuario B");
        userRepository.saveAndFlush(user);

        PrendaSuperior p = sup(user);
        prendaRepository.saveAndFlush(p);

        prendaRepository.delete(p);
        prendaRepository.flush();

        assertThat(userRepository.findById(user.getDNI())).isPresent();
    }
}
