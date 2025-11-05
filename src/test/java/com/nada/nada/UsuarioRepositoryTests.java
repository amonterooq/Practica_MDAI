package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UsuarioRepositoryTests {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PrendaSuperiorRepository prendaSuperiorRepository;

    @Autowired
    private PrendaInferiorRepository prendaInferiorRepository;

    @Autowired
    private PrendaCalzadoRepository prendaCalzadoRepository;

    @Autowired
    private ConjuntoRepository conjuntoRepository;

    private Usuario nuevoUsuario(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@gmail.com");
        return u;
    }

    private PrendaSuperior nuevaSuperior(Usuario u, String nombre) {
        PrendaSuperior p = new PrendaSuperior();
        p.setNombre(nombre);
        p.setColor("Blanco");
        p.setMarca("Acme");
        p.setUsuario(u);
        p.setTalla("M");
        p.setDirImagen("http://img/superior.png");
        p.setCategoria(CategoriaSuperior.CAMISETA);
        p.setManga(Manga.CORTA);
        return p;
    }

    private PrendaInferior nuevaInferior(Usuario u, String nombre) {
        PrendaInferior p = new PrendaInferior();
        p.setNombre(nombre);
        p.setColor("Azul");
        p.setMarca("Acme");
        p.setUsuario(u);
        p.setTalla("32");
        p.setDirImagen("http://img/inferior.png");
        p.setCategoriaInferior(CategoriaInferior.JEAN);
        return p;
    }

    private PrendaCalzado nuevoCalzado(Usuario u, String nombre) {
        PrendaCalzado p = new PrendaCalzado();
        p.setNombre(nombre);
        p.setColor("Negro");
        p.setMarca("Acme");
        p.setUsuario(u);
        p.setTalla("42");
        p.setDirImagen("http://img/calzado.png");
        p.setCategoria(CategoriaCalzado.DEPORTIVO);
        return p;
    }

    @Test
    void testComprobarDatosIniciales() {
        Usuario fran = usuarioRepository.findByUsername("fran");
        assertNotNull(fran);
        assertEquals("fran", fran.getUsername());
    }

    @Test
    void testGuardarYBuscarUsuarioPorUsername() {
        Usuario u = nuevoUsuario("anita");
        usuarioRepository.save(u);

        Usuario encontrado = usuarioRepository.findByUsername("anita");
        assertNotNull(encontrado);
        assertEquals("anita", encontrado.getUsername());
        assertNotNull(encontrado.getId());
    }

    @Test
    @Transactional
    void testUsuarioPoseePrendasYConjuntos() {
        Usuario u = usuarioRepository.save(nuevoUsuario("bobby"));

        PrendaSuperior sup = prendaSuperiorRepository.save(nuevaSuperior(u, "Camiseta Blanca"));
        PrendaInferior inf = prendaInferiorRepository.save(nuevaInferior(u, "Jeans Azul"));
        PrendaCalzado cal = prendaCalzadoRepository.save(nuevoCalzado(u, "Zapatillas"));

        Conjunto c = new Conjunto();
        c.setNombre("Casual");
        c.setDescripcion("Conjunto casual");
        c.setUsuario(u);
        c.setPrendaSuperior(sup);
        c.setPrendaInferior(inf);
        c.setPrendaCalzado(cal);
        conjuntoRepository.save(c);

        u.addPrenda(sup);
        u.addPrenda(inf);
        u.addPrenda(cal);
        u.addConjunto(c);


        Usuario recargado = usuarioRepository.findByUsername("bobby");
        assertNotNull(recargado);
        assertEquals(3, recargado.getPrendas().size());
        assertEquals(1, recargado.getConjuntos().size());
    }

    @Test
    void testBuscarUsuarioNoExistenteDevuelveNull() {
        Usuario inexistente = usuarioRepository.findByUsername("no-existe");
        assertThat(inexistente).isNull();
    }

    @Test
    void testActualizarDatosBasicosUsuario() {
        Usuario u = usuarioRepository.save(nuevoUsuario("carl"));

        Usuario fetched = usuarioRepository.findByUsername("carl");
        fetched.setEmail("nuevo@gmail.com");
        usuarioRepository.save(fetched);

        Usuario after = usuarioRepository.findByUsername("carl");
        assertThat(after.getEmail()).isEqualTo("nuevo@gmail.com");
    }

    @Test
    @Transactional
    void testNavegarRelacionesDesdeUsuario() {
        Usuario u = usuarioRepository.save(nuevoUsuario("dora"));

        PrendaSuperior sup = prendaSuperiorRepository.save(nuevaSuperior(u, "Top Dora"));
        PrendaInferior inf = prendaInferiorRepository.save(nuevaInferior(u, "Bottom Dora"));
        PrendaCalzado cal = prendaCalzadoRepository.save(nuevoCalzado(u, "Shoes Dora"));

        Conjunto c = new Conjunto();
        c.setNombre("Completo Dora");
        c.setDescripcion("Set completo");
        c.setUsuario(u);
        c.setPrendaSuperior(sup);
        c.setPrendaInferior(inf);
        c.setPrendaCalzado(cal);
        conjuntoRepository.save(c);

        u.addPrenda(sup);
        u.addPrenda(inf);
        u.addPrenda(cal);
        u.addConjunto(c);
        usuarioRepository.save(u);

        Usuario fetched = usuarioRepository.findById(u.getId()).orElseThrow();
        assertThat(fetched.getConjuntos()).hasSize(1);

        Conjunto rc = fetched.getConjuntos().iterator().next();
        assertThat(rc.getPrendaSuperior().getUsuario().getId()).isEqualTo(fetched.getId());
        assertThat(rc.getPrendaInferior().getUsuario().getId()).isEqualTo(fetched.getId());
        assertThat(rc.getPrendaCalzado().getUsuario().getId()).isEqualTo(fetched.getId());
    }
}
