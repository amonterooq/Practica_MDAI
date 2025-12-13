package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.model.enums.CategoriaCalzado;
import com.nada.nada.data.model.enums.CategoriaInferior;
import com.nada.nada.data.model.enums.CategoriaSuperior;
import com.nada.nada.data.model.enums.Manga;
import com.nada.nada.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RequisitosTests {

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
    @Autowired
    private ConjuntoRepository conjuntoRepository;

    private Usuario user(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@mail.com");
        return usuarioRepository.save(u);
    }

    private PrendaSuperior sup(Usuario u, String nombre) {
        PrendaSuperior p = new PrendaSuperior();
        p.setNombre(nombre);
        p.setColor("Blanco");
        p.setMarca("M");
        p.setUsuario(u);
        p.setTalla("M");
        p.setDirImagen("images/sup.png");
        p.setCategoria(CategoriaSuperior.CAMISETA);
        p.setManga(Manga.CORTA);
        return prendaSuperiorRepository.save(p);
    }

    private PrendaInferior inf(Usuario u, String nombre) {
        PrendaInferior p = new PrendaInferior();
        p.setNombre(nombre);
        p.setColor("Negro");
        p.setMarca("M");
        p.setUsuario(u);
        p.setTalla("32");
        p.setDirImagen("images/inf.png");
        p.setCategoriaInferior(CategoriaInferior.PANTALON);
        return prendaInferiorRepository.save(p);
    }

    private PrendaCalzado cal(Usuario u, String nombre) {
        PrendaCalzado p = new PrendaCalzado();
        p.setNombre(nombre);
        p.setColor("Azul");
        p.setMarca("M");
        p.setUsuario(u);
        p.setTalla("42");
        p.setDirImagen("images/calz.png");
        p.setCategoria(CategoriaCalzado.DEPORTIVO);
        return prendaCalzadoRepository.save(p);
    }

    // Requisito: crear cuenta y acceder
    @Test
    void testRequisitoCrearCuentaYAcceder() {
        Usuario u = user("acepta");
        Optional<Usuario> encontradoOpt = usuarioRepository.findByUsername("acepta");
        assertTrue(encontradoOpt.isPresent());
        Usuario encontrado = encontradoOpt.get();
        assertEquals(u.getId(), encontrado.getId());
    }

    // Requisito: subir foto y editar prenda
    @Test
    void testRequisitoSubirFotoYEditarPrenda() {
        Usuario u = user("foto");
        PrendaSuperior ps = sup(u, "Top Foto");
        ps.setDirImagen("images/top-foto.png");
        prendaSuperiorRepository.save(ps);

        Prenda loaded = prendaRepository.findById(ps.getId()).orElseThrow();
        assertEquals("images/top-foto.png", loaded.getDirImagen());

        loaded.setColor("Verde");
        prendaRepository.save(loaded);
        Prenda after = prendaRepository.findById(loaded.getId()).orElseThrow();
        assertEquals("Verde", after.getColor());
    }

    // Requisito: búsqueda por tipo/categoría/color/marca/talla
    @Test
    void testRequisitoBusquedaPorAtributosYCategorias() {
        Usuario u = user("search");
        PrendaSuperior ps = sup(u, "Camiseta Blanca");
        ps.setColor("Blanco");
        ps.setMarca("Zara");
        ps.setTalla("M");
        prendaSuperiorRepository.save(ps);

        PrendaInferior pi = inf(u, "Jean Azul");
        pi.setColor("Azul");
        pi.setMarca("Levis");
        pi.setTalla("32");
        pi.setCategoriaInferior(CategoriaInferior.JEAN);
        prendaInferiorRepository.save(pi);

        PrendaCalzado pc = cal(u, "Zapatilla Negra");
        pc.setColor("Negro");
        pc.setMarca("Nike");
        pc.setTalla("42");
        pc.setCategoria(CategoriaCalzado.DEPORTIVO);
        prendaCalzadoRepository.save(pc);

        List<Prenda> porColor = prendaRepository.findAllByUsuario_IdAndColorContainingIgnoreCase(u.getId(), "blan");
        assertEquals(1, porColor.size());
        assertEquals(ps.getId(), porColor.get(0).getId());

        List<Prenda> porMarca = prendaRepository.findAllByUsuario_IdAndMarcaContainingIgnoreCase(u.getId(), "zara");
        assertEquals(1, porMarca.size());
        assertEquals(ps.getId(), porMarca.get(0).getId());

        List<Prenda> porTalla = prendaRepository.findAllByUsuario_IdAndTallaIgnoreCase(u.getId(), "32");
        assertEquals(1, porTalla.size());
        assertEquals(pi.getId(), porTalla.get(0).getId());

        assertEquals(1, prendaSuperiorRepository.findAllByUsuario_IdAndCategoria(u.getId(), CategoriaSuperior.CAMISETA).size());
        assertEquals(1, prendaInferiorRepository.findAllByUsuario_IdAndCategoriaInferior(u.getId(), CategoriaInferior.JEAN).size());
        assertEquals(1, prendaCalzadoRepository.findAllByUsuario_IdAndCategoria(u.getId(), CategoriaCalzado.DEPORTIVO).size());
    }

    // Requisito: crear conjunto con varias prendas
    @Test
    void testRequisitoCrearConjuntoPersonalizado() {
        Usuario u = user("setuser");
        PrendaSuperior ps = sup(u, "Sup");
        PrendaInferior pi = inf(u, "Inf");
        PrendaCalzado pc = cal(u, "Cal");

        Conjunto c = new Conjunto();
        c.setNombre("Mi Set");
        c.setDescripcion("Notas del set");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        Conjunto saved = conjuntoRepository.save(c);

        Conjunto rec = conjuntoRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Mi Set", rec.getNombre());
        assertEquals(ps.getId(), rec.getPrendaSuperior().getId());
        assertEquals(pi.getId(), rec.getPrendaInferior().getId());
        assertEquals(pc.getId(), rec.getPrendaCalzado().getId());
    }

    // Requisito: límite de descripción 256 caracteres
    @Test
    void testRequisitoDescripcionConjuntoMasDe256Falla() {
        Usuario u = user("desc-ko");
        Conjunto c = new Conjunto();
        c.setUsuario(u);
        c.setNombre("Muy largo");
        c.setDescripcion("x".repeat(257));
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> conjuntoRepository.save(c));
    }

    // Requisito: eliminar conjunto no debe eliminar prendas
    @Test
    void testRequisitoEliminarConjuntoNoEliminaPrendas() {
        Usuario u = user("keep");
        PrendaSuperior ps = sup(u, "A");
        PrendaInferior pi = inf(u, "B");
        PrendaCalzado pc = cal(u, "C");

        Conjunto c = new Conjunto();
        c.setNombre("Look");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        c = conjuntoRepository.save(c);

        conjuntoRepository.deleteById(c.getId());

        assertTrue(prendaSuperiorRepository.findById(ps.getId()).isPresent());
        assertTrue(prendaInferiorRepository.findById(pi.getId()).isPresent());
        assertTrue(prendaCalzadoRepository.findById(pc.getId()).isPresent());
    }

    // Requisito: aislamiento de armarios por usuario y listados
    @Test
    void testRequisitoListadoPorUsuarioYArmarioAislado() {
        Usuario u1 = user("u1");
        Usuario u2 = user("u2");

        PrendaSuperior ps1 = sup(u1, "u1-s");
        PrendaInferior pi1 = inf(u1, "u1-i");
        PrendaCalzado pc1 = cal(u1, "u1-c");

        PrendaSuperior ps2 = sup(u2, "u2-s");
        PrendaInferior pi2 = inf(u2, "u2-i");
        PrendaCalzado pc2 = cal(u2, "u2-c");

        Conjunto c1 = new Conjunto();
        c1.setNombre("c1");
        c1.setUsuario(u1);
        c1.setPrendaSuperior(ps1);
        c1.setPrendaInferior(pi1);
        c1.setPrendaCalzado(pc1);
        conjuntoRepository.save(c1);
        Conjunto c2 = new Conjunto();
        c2.setNombre("c2");
        c2.setUsuario(u2);
        c2.setPrendaSuperior(ps2);
        c2.setPrendaInferior(pi2);
        c2.setPrendaCalzado(pc2);
        conjuntoRepository.save(c2);

        assertEquals(3, prendaRepository.countByUsuario_Id(u1.getId()));
        assertEquals(3, prendaRepository.countByUsuario_Id(u2.getId()));
        assertEquals(1, conjuntoRepository.countByUsuario_Id(u1.getId()));
        assertEquals(1, conjuntoRepository.countByUsuario_Id(u2.getId()));
    }

    // Requisito: eliminar usuario elimina sus prendas y conjuntos
    @Test
    void testRequisitoEliminarUsuarioEliminaSusPrendasYConjuntos() {
        Usuario u = user("cascade");
        PrendaSuperior ps = sup(u, "Sup");
        PrendaInferior pi = inf(u, "Inf");
        PrendaCalzado pc = cal(u, "Cal");
        Conjunto c = new Conjunto();
        c.setNombre("Set");
        c.setDescripcion("full");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        conjuntoRepository.save(c);

        Long uid = u.getId();
        Long psId = ps.getId();
        Long piId = pi.getId();
        Long pcId = pc.getId();
        Long cId = c.getId();

        usuarioRepository.deleteById(uid);

        assertTrue(usuarioRepository.findById(uid).isEmpty());
        assertTrue(prendaSuperiorRepository.findById(psId).isEmpty());
        assertTrue(prendaInferiorRepository.findById(piId).isEmpty());
        assertTrue(prendaCalzadoRepository.findById(pcId).isEmpty());
        assertTrue(conjuntoRepository.findById(cId).isEmpty());
    }

    // Requisito: eliminar prenda elimina conjuntos asociados
    @Test
    void testRequisitoEliminarPrendaEliminaConjuntosAsociados() {
        Usuario u = user("paula");

        // Prendas
        PrendaSuperior ps1 = sup(u, "Camiseta Roja");
        PrendaSuperior ps2 = sup(u, "Camiseta Azul");
        PrendaInferior pi1 = inf(u, "Pantalón Negro");
        PrendaInferior pi2 = inf(u, "Pantalón Gris");
        PrendaCalzado pc1 = cal(u, "Zapatillas Negras");
        PrendaCalzado pc2 = cal(u, "Botas Marrones");

        // Conjuntos independientes por tipo de prenda objetivo
        Conjunto cSup = new Conjunto();
        cSup.setNombre("Look Superior");
        cSup.setUsuario(u);
        cSup.setPrendaSuperior(ps1);
        cSup.setPrendaInferior(pi2);
        cSup.setPrendaCalzado(pc2);
        cSup = conjuntoRepository.save(cSup);

        Conjunto cInf = new Conjunto();
        cInf.setNombre("Look Inferior");
        cInf.setUsuario(u);
        cInf.setPrendaSuperior(ps2);
        cInf.setPrendaInferior(pi1);
        cInf.setPrendaCalzado(pc2);
        cInf = conjuntoRepository.save(cInf);

        Conjunto cCal = new Conjunto();
        cCal.setNombre("Look Calzado");
        cCal.setUsuario(u);
        cCal.setPrendaSuperior(ps2);
        cCal.setPrendaInferior(pi2);
        cCal.setPrendaCalzado(pc1);
        cCal = conjuntoRepository.save(cCal);

        // Borrar superior -> solo cae cSup
        prendaSuperiorRepository.deleteById(ps1.getId());
        assertTrue(conjuntoRepository.findById(cSup.getId()).isEmpty());
        assertTrue(conjuntoRepository.findById(cInf.getId()).isPresent());
        assertTrue(conjuntoRepository.findById(cCal.getId()).isPresent());

        // Borrar inferior -> solo cae cInf
        prendaInferiorRepository.deleteById(pi1.getId());
        assertTrue(conjuntoRepository.findById(cInf.getId()).isEmpty());
        assertTrue(conjuntoRepository.findById(cCal.getId()).isPresent());

        // Borrar calzado -> solo cae cCal
        prendaCalzadoRepository.deleteById(pc1.getId());
        assertTrue(conjuntoRepository.findById(cCal.getId()).isEmpty());
    }
}
