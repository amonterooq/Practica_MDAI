package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PrendaRepositoryTest {

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
    void testComprobarDatosIniciales() {
        Prenda p = prendaRepository.findById(1);
        assertNotNull(p);
        assertEquals("Camiseta blanca", p.getNombre());
    }

    @Test
    void testGuardarYBuscarPorNombreEnRepositorioBase() {
        Usuario u = seedUsuario();

        PrendaSuperior sup = new PrendaSuperior();
        sup.setNombre("Camiseta Blanca");
        sup.setColor("Blanco");
        sup.setMarca("Acme");
        sup.setUsuario(u);
        sup.setTalla("M");
        sup.setDirImagen("url");
        sup.setCategoria(CategoriaSuperior.CAMISETA);
        sup.setManga(Manga.CORTA);

        prendaRepository.save(sup);

        Prenda encontrada = prendaRepository.findByNombre("Camiseta Blanca");
        assertNotNull(encontrada);
        assertEquals("Camiseta Blanca", encontrada.getNombre());
        assertTrue(encontrada instanceof PrendaSuperior);
    }

    @Test
    void testPersistenciaDeHerenciaSubclases() {
        int numPrendasInicial = (int) prendaRepository.count();
        Usuario u = seedUsuario();

        PrendaSuperior sup = new PrendaSuperior();
        sup.setNombre("Camisa");
        sup.setColor("Azul");
        sup.setMarca("Marca");
        sup.setUsuario(u);
        sup.setTalla("L");
        sup.setDirImagen("u1");
        sup.setCategoria(CategoriaSuperior.CAMISA);
        sup.setManga(Manga.LARGA);

        PrendaInferior inf = new PrendaInferior();
        inf.setNombre("Pantalon");
        inf.setColor("Negro");
        inf.setMarca("Marca");
        inf.setUsuario(u);
        inf.setTalla("32");
        inf.setDirImagen("u2");
        inf.setCategoriaInferior(CategoriaInferior.PANTALON);

        PrendaCalzado cal = new PrendaCalzado();
        cal.setNombre("Zapato");
        cal.setColor("Marron");
        cal.setMarca("Marca");
        cal.setUsuario(u);
        cal.setTalla("42");
        cal.setDirImagen("u3");
        cal.setCategoria(CategoriaCalzado.FORMAL);

        prendaSuperiorRepository.save(sup);
        prendaInferiorRepository.save(inf);
        prendaCalzadoRepository.save(cal);

        assertEquals(numPrendasInicial + 3, prendaRepository.count());
        assertTrue(prendaRepository.findByNombre("Camisa") instanceof PrendaSuperior);
        assertTrue(prendaRepository.findByNombre("Pantalon") instanceof PrendaInferior);
        assertTrue(prendaRepository.findByNombre("Zapato") instanceof PrendaCalzado);
    }

    @Test
    void testRelacionPrendaConUsuario() {
        Usuario u = seedUsuario();

        PrendaInferior inf = new PrendaInferior();
        inf.setNombre("Jean");
        inf.setColor("Azul");
        inf.setMarca("Acme");
        inf.setUsuario(u);
        inf.setTalla("32");
        inf.setDirImagen("url");
        inf.setCategoriaInferior(CategoriaInferior.JEAN);

        prendaRepository.save(inf);

        Prenda p = prendaRepository.findByNombre("Jean");
        assertNotNull(p.getUsuario());
        assertEquals("user1", p.getUsuario().getUsername());
    }


    @Test
    void testUsuarioPoseeMultiplesPrendasSegunRepositorioBase() {
        Usuario u = seedUsuario();

        PrendaSuperior sup = new PrendaSuperior();
        sup.setNombre("Top User");
        sup.setColor("Azul");
        sup.setMarca("Zara");
        sup.setUsuario(u);
        sup.setTalla("M");
        sup.setDirImagen("url-sup");
        sup.setCategoria(CategoriaSuperior.CAMISETA);
        sup.setManga(Manga.CORTA);

        PrendaInferior inf = new PrendaInferior();
        inf.setNombre("Bottom User");
        inf.setColor("Blanco");
        inf.setMarca("Levis");
        inf.setUsuario(u);
        inf.setTalla("40");
        inf.setDirImagen("url-inf");
        inf.setCategoriaInferior(CategoriaInferior.PANTALON);

        PrendaCalzado calz = new PrendaCalzado();
        calz.setNombre("Shoes User");
        calz.setColor("Negro");
        calz.setMarca("Nike");
        calz.setUsuario(u);
        calz.setTalla("42");
        calz.setDirImagen("url-calz");
        calz.setCategoria(CategoriaCalzado.FORMAL);

        prendaRepository.save(sup);
        prendaRepository.save(inf);
        prendaRepository.save(calz);

        // verifica por el repositorio (no por la colección inversa)
        assertEquals(3, prendaRepository.countByUsuario_Id(u.getId()));

        assertTrue(java.util.stream.Stream.of("Top User","Bottom User","Shoes User")
                .map(prendaRepository::findByNombre)
                .allMatch(p -> p != null && p.getUsuario() != null && p.getUsuario().getId().equals(u.getId())));
    }

    @Test
    void testEliminarPrendaNoEliminaUsuario() {
        Usuario u = seedUsuario();

        PrendaSuperior p = new PrendaSuperior();
        p.setNombre("Eliminar-Segura");
        p.setColor("Gris");
        p.setMarca("Acme");
        p.setUsuario(u);
        p.setTalla("M");
        p.setDirImagen("url");
        p.setCategoria(CategoriaSuperior.CAMISETA);
        p.setManga(Manga.CORTA);

        p = prendaRepository.save(p);

        prendaRepository.delete(p);

        assertTrue(usuarioRepository.findById(u.getId()).isPresent());
    }

    @Test
    void testBuscarPorNombreInexistenteDevuelveNull() {
        Prenda p = prendaRepository.findByNombre("no-existe-xyz");
        assertNull(p);
    }

    @Test
    void testActualizarCamposDePrenda() {
        Usuario u = seedUsuario();

        PrendaSuperior p = new PrendaSuperior();
        p.setNombre("Editable");
        p.setColor("Rojo");
        p.setMarca("Brand");
        p.setUsuario(u);
        p.setTalla("L");
        p.setDirImagen("u");
        p.setCategoria(CategoriaSuperior.CAMISA);
        p.setManga(Manga.LARGA);

        p = prendaRepository.save(p);

        Prenda loaded = prendaRepository.findByNombre("Editable");
        assertNotNull(loaded);
        loaded.setColor("Verde");
        prendaRepository.save(loaded);

        Prenda after = prendaRepository.findById(loaded.getId()).orElseThrow();
        assertEquals("Verde", after.getColor());
    }
}
