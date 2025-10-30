package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.PrendaRepository;
import com.nada.nada.data.repository.UsuarioRepository;
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

    @Autowired UsuarioRepository usuarioRepository;
    @Autowired PrendaRepository prendaRepository;
    @Autowired EntityManager em;

    private PrendaSuperior sup(Usuario u) {
        PrendaSuperior p = new PrendaSuperior();
        p.setUsuario(u);
        p.setCategoria(CategoriaSuperior.CAMISETA);
        p.setColor("Azul");
        p.setMarca("Zara");
        p.setTalla("M");
        return p;
    }

    private PrendaInferior inf(Usuario u) {
        PrendaInferior p = new PrendaInferior();
        p.setUsuario(u);
        p.setCategoria(CategoriaInferior.PANTALON);
        p.setColor("Blanco");
        p.setMarca("Levis");
        p.setTalla("40");
        return p;
    }

    private PrendaCalzado calz(Usuario u) {
        PrendaCalzado p = new PrendaCalzado();
        p.setUsuario(u);
        p.setCategoria(CategoriaCalzado.ZAPATILLAS);
        p.setColor("Negro");
        p.setMarca("Nike");
        p.setTalla("42");
        return p;
    }

    @Test
    @Transactional
    void usuarioPoseeMultiplesPrendas() {
        Usuario usuario = new Usuario(); // evita depender de constructores no existentes
        usuarioRepository.save(usuario);

        prendaRepository.save(sup(usuario));
        prendaRepository.save(inf(usuario));
        prendaRepository.save(calz(usuario));
        em.flush();
        em.refresh(usuario); // asegura cargar la colección

        assertThat(usuario.getPrendas()).hasSize(3);
        assertThat(usuario.getPrendas())
                .allMatch(p -> p.getUsuario() != null && p.getUsuario().equals(usuario));
    }

    @Test
    @Transactional
    void eliminarPrendaNoEliminaUsuario() {
        Usuario usuario = new Usuario();
        usuarioRepository.save(usuario);

        PrendaSuperior p = sup(usuario);
        prendaRepository.save(p);
        em.flush();

        prendaRepository.delete(p);
        em.flush();

        assertThat(usuarioRepository.count()).isEqualTo(1L);
    }
}