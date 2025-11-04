package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class PrendaRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PrendaRepository prendaRepository;
    @Autowired
    private PrendaSuperiorRepository prendaSuperiorRepository;
    @Autowired
    private PrendaInferiorRepository prendaInferiorRepository;
    @Autowired
    private PrendaCalzadoRepository prendaCalzadoRepository;

    private Usuario seedUsuario() {
        Usuario u = new Usuario();
        u.setUsername("user1");
        u.setPassword("pwd");
        u.setEmail("user1@gmail.com");
        return usuarioRepository.save(u);
    }

    @Test
    void guardarYBuscarPorNombreEnRepositorioBase() {
        Usuario u = seedUsuario();

        PrendaSuperior sup = new PrendaSuperior();
        sup.setNombre("Camiseta Blanca");
        sup.setColor("Blanco");
        sup.setMarca("Acme");
        sup.setUsuario(u);
        sup.setTalla("M");
        sup.setUrlImagen("url");
        sup.setCategoria(CategoriaSuperior.CAMISETA);
        sup.setManga(Manga.CORTA);

        prendaRepository.save(sup);
        em.flush();
        em.clear();

        Prenda encontrada = prendaRepository.findByNombre("Camiseta Blanca");
        assertNotNull(encontrada);
        assertEquals("Camiseta Blanca", encontrada.getNombre());
        assertTrue(encontrada instanceof PrendaSuperior);
    }

    @Test
    void persistenciaDeHerencia_Subclases() {
        Usuario u = seedUsuario();

        PrendaSuperior sup = new PrendaSuperior();
        sup.setNombre("Camisa");
        sup.setColor("Azul");
        sup.setMarca("Marca");
        sup.setUsuario(u);
        sup.setTalla("L");
        sup.setUrlImagen("u1");
        sup.setCategoria(CategoriaSuperior.CAMISA);
        sup.setManga(Manga.LARGA);

        PrendaInferior inf = new PrendaInferior();
        inf.setNombre("Pantalon");
        inf.setColor("Negro");
        inf.setMarca("Marca");
        inf.setUsuario(u);
        inf.setTalla("32");
        inf.setUrlImagen("u2");
        inf.setCategoriaInferior(CategoriaInferior.PANTALON);

        PrendaCalzado cal = new PrendaCalzado();
        cal.setNombre("Zapato");
        cal.setColor("Marron");
        cal.setMarca("Marca");
        cal.setUsuario(u);
        cal.setTalla("42");
        cal.setUrlImagen("u3");
        cal.setCategoria(CategoriaCalzado.FORMAL);

        prendaSuperiorRepository.save(sup);
        prendaInferiorRepository.save(inf);
        prendaCalzadoRepository.save(cal);

        em.flush();
        em.clear();

        assertEquals(3, prendaRepository.count());
        assertTrue(prendaRepository.findByNombre("Camisa") instanceof PrendaSuperior);
        assertTrue(prendaRepository.findByNombre("Pantalon") instanceof PrendaInferior);
        assertTrue(prendaRepository.findByNombre("Zapato") instanceof PrendaCalzado);
    }

    @Test
    void relacionPrendaConUsuario() {
        Usuario u = seedUsuario();

        PrendaInferior inf = new PrendaInferior();
        inf.setNombre("Jean");
        inf.setColor("Azul");
        inf.setMarca("Acme");
        inf.setUsuario(u);
        inf.setTalla("32");
        inf.setUrlImagen("url");
        inf.setCategoriaInferior(CategoriaInferior.JEAN);

        prendaRepository.save(inf);
        em.flush();
        em.clear();

        Prenda p = prendaRepository.findByNombre("Jean");
        assertNotNull(p.getUsuario());
        assertEquals("user1", p.getUsuario().getUsername());
    }

    // java
    @org.springframework.transaction.annotation.Transactional
    @org.junit.jupiter.api.Test
    void usuarioPoseeMultiplesPrendas_segunRepositorioBase() {
        Usuario u = seedUsuario();

        PrendaSuperior sup = new PrendaSuperior();
        sup.setNombre("Top User");
        sup.setColor("Azul");
        sup.setMarca("Zara");
        sup.setUsuario(u);
        sup.setTalla("M");
        sup.setUrlImagen("url-sup");
        sup.setCategoria(CategoriaSuperior.CAMISETA);
        sup.setManga(Manga.CORTA);

        PrendaInferior inf = new PrendaInferior();
        inf.setNombre("Bottom User");
        inf.setColor("Blanco");
        inf.setMarca("Levis");
        inf.setUsuario(u);
        inf.setTalla("40");
        inf.setUrlImagen("url-inf");
        inf.setCategoriaInferior(CategoriaInferior.PANTALON);

        PrendaCalzado calz = new PrendaCalzado();
        calz.setNombre("Shoes User");
        calz.setColor("Negro");
        calz.setMarca("Nike");
        calz.setUsuario(u);
        calz.setTalla("42");
        calz.setUrlImagen("url-calz");
        calz.setCategoria(CategoriaCalzado.FORMAL);

        prendaRepository.save(sup);
        prendaRepository.save(inf);
        prendaRepository.save(calz);
        em.flush();
        em.clear();

        Usuario fetched = usuarioRepository.findById(u.getId()).orElseThrow();
        assertEquals(3, fetched.getPrendas().size());
        assertTrue(fetched.getPrendas().stream()
                .allMatch(p -> p.getUsuario() != null && p.getUsuario().getId().equals(u.getId())));
    }

    @org.junit.jupiter.api.Test
    void eliminarPrendaNoEliminaUsuario() {
        Usuario u = seedUsuario();

        PrendaSuperior p = new PrendaSuperior();
        p.setNombre("Eliminar-Segura");
        p.setColor("Gris");
        p.setMarca("Acme");
        p.setUsuario(u);
        p.setTalla("M");
        p.setUrlImagen("url");
        p.setCategoria(CategoriaSuperior.CAMISETA);
        p.setManga(Manga.CORTA);

        p = prendaRepository.save(p);
        em.flush();

        prendaRepository.delete(p);
        em.flush();
        em.clear();

        assertTrue(usuarioRepository.findById(u.getId()).isPresent());
    }

    @org.junit.jupiter.api.Test
    void buscarPorNombreInexistenteDevuelveNull() {
        Prenda p = prendaRepository.findByNombre("no-existe-xyz");
        assertNull(p);
    }

    @org.junit.jupiter.api.Test
    void actualizarCamposDePrenda() {
        Usuario u = seedUsuario();

        PrendaSuperior p = new PrendaSuperior();
        p.setNombre("Editable");
        p.setColor("Rojo");
        p.setMarca("Brand");
        p.setUsuario(u);
        p.setTalla("L");
        p.setUrlImagen("u");
        p.setCategoria(CategoriaSuperior.CAMISA);
        p.setManga(Manga.LARGA);

        p = prendaRepository.save(p);
        em.flush();
        em.clear();

        Prenda loaded = prendaRepository.findByNombre("Editable");
        assertNotNull(loaded);
        loaded.setColor("Verde");
        prendaRepository.save(loaded);
        em.flush();
        em.clear();

        Prenda after = prendaRepository.findById(loaded.getId()).orElseThrow();
        assertEquals("Verde", after.getColor());
    }
}
