package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.ConjuntoRepository;
import com.nada.nada.data.repository.PrendaCalzadoRepository;
import com.nada.nada.data.repository.PrendaInferiorRepository;
import com.nada.nada.data.repository.PrendaSuperiorRepository;
import com.nada.nada.data.repository.UsuarioRepository;
import com.nada.nada.data.services.ConjuntoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ConjuntoServiceTests {

    @Autowired
    private ConjuntoService conjuntoService;

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
        u.setEmail(username + "@mail.com");
        return usuarioRepository.save(u);
    }

    private PrendaSuperior sup(Usuario u) {
        PrendaSuperior p = new PrendaSuperior();
        p.setNombre("Superior");
        p.setColor("Blanco");
        p.setMarca("Marca");
        p.setUsuario(u);
        p.setTalla("M");
        p.setDirImagen("images/sup.png");
        p.setCategoria(CategoriaSuperior.CAMISETA);
        p.setManga(Manga.CORTA);
        return prendaSuperiorRepository.save(p);
    }

    private PrendaInferior inf(Usuario u) {
        PrendaInferior p = new PrendaInferior();
        p.setNombre("Inferior");
        p.setColor("Negro");
        p.setMarca("Marca");
        p.setUsuario(u);
        p.setTalla("32");
        p.setDirImagen("images/inf.png");
        p.setCategoriaInferior(CategoriaInferior.PANTALON);
        return prendaInferiorRepository.save(p);
    }

    private PrendaCalzado cal(Usuario u) {
        PrendaCalzado p = new PrendaCalzado();
        p.setNombre("Calzado");
        p.setColor("Azul");
        p.setMarca("Marca");
        p.setUsuario(u);
        p.setTalla("42");
        p.setDirImagen("images/calz.png");
        p.setCategoria(CategoriaCalzado.DEPORTIVO);
        return prendaCalzadoRepository.save(p);
    }

    @Test
    void testGuardarYBuscarConjuntoPorId() {
        Usuario u = nuevoUsuario("usuarioConjunto");
        PrendaSuperior ps = sup(u);
        PrendaInferior pi = inf(u);
        PrendaCalzado pc = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Conjunto Servicio");
        c.setDescripcion("Descripcion");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);

        Conjunto guardado = conjuntoService.guardarConjunto(c);

        Optional<Conjunto> rec = conjuntoService.buscarConjuntoPorId(guardado.getId());
        assertTrue(rec.isPresent());
        assertEquals("Conjunto Servicio", rec.get().getNombre());
    }

    @Test
    void testGuardarConjuntoConDescripcionLargaLanzaExcepcion() {
        Usuario u = nuevoUsuario("usuarioConjuntoLargo");
        Conjunto c = new Conjunto();
        c.setUsuario(u);
        c.setNombre("Nombre");
        c.setDescripcion("x".repeat(257));
        assertThrows(IllegalArgumentException.class, () -> conjuntoService.guardarConjunto(c));
    }

    @Test
    void testBuscarConjuntosPorUsuarioIdSoloDevuelveLosSuyos() {
        Usuario u1 = nuevoUsuario("usuarioConjunto1");
        Usuario u2 = nuevoUsuario("usuarioConjunto2");
        PrendaSuperior ps1 = sup(u1);
        PrendaInferior pi1 = inf(u1);
        PrendaCalzado pc1 = cal(u1);
        PrendaSuperior ps2 = sup(u2);
        PrendaInferior pi2 = inf(u2);
        PrendaCalzado pc2 = cal(u2);

        Conjunto c1 = new Conjunto();
        c1.setNombre("Conjunto 1");
        c1.setUsuario(u1);
        c1.setPrendaSuperior(ps1);
        c1.setPrendaInferior(pi1);
        c1.setPrendaCalzado(pc1);
        conjuntoService.guardarConjunto(c1);

        Conjunto c2 = new Conjunto();
        c2.setNombre("Conjunto 2");
        c2.setUsuario(u2);
        c2.setPrendaSuperior(ps2);
        c2.setPrendaInferior(pi2);
        c2.setPrendaCalzado(pc2);
        conjuntoService.guardarConjunto(c2);

        List<Conjunto> deU1 = conjuntoService.buscarConjuntosPorUsuarioId(u1.getId());
        assertEquals(1, deU1.size());
        assertEquals("Conjunto 1", deU1.get(0).getNombre());
    }

    @Test
    void testBorrarConjuntoExistenteDevuelveTrueYLoElimina() {
        Usuario u = nuevoUsuario("usuarioConjuntoBorrar");
        PrendaSuperior ps = sup(u);
        PrendaInferior pi = inf(u);
        PrendaCalzado pc = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Conjunto Borrar");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        Conjunto guardado = conjuntoService.guardarConjunto(c);
        Long id = guardado.getId();

        boolean ok = conjuntoService.borrarConjunto(id);
        assertTrue(ok);
        assertFalse(conjuntoRepository.findById(id).isPresent());
    }

    @Test
    void testGuardarConjuntoConPrendasDeOtroUsuarioFalla() {
        Usuario u1 = nuevoUsuario("usuarioDuenoConjunto");
        Usuario u2 = nuevoUsuario("usuarioOtroConjunto");
        PrendaSuperior ps = sup(u2); // pertenece a u2

        Conjunto c = new Conjunto();
        c.setNombre("Conjunto Incorrecto");
        c.setUsuario(u1);
        c.setPrendaSuperior(ps);

        assertThrows(IllegalArgumentException.class, () -> conjuntoService.guardarConjunto(c));
    }

    @Test
    void testGuardarConjuntoSinPrendaSuperiorLanzaExcepcion() {
        Usuario u = nuevoUsuario("usuarioSinSup");
        PrendaInferior pi = inf(u);
        PrendaCalzado pc = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Conjunto incompleto");
        c.setUsuario(u);
        // c.setPrendaSuperior(null); // faltante
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);

        assertThrows(IllegalArgumentException.class, () -> conjuntoService.guardarConjunto(c));
    }

    @Test
    void testGuardarConjuntoSinPrendaInferiorLanzaExcepcion() {
        Usuario u = nuevoUsuario("usuarioSinInf");
        PrendaSuperior ps = sup(u);
        PrendaCalzado pc = cal(u);

        Conjunto c = new Conjunto();
        c.setNombre("Conjunto incompleto");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        // c.setPrendaInferior(null); // faltante
        c.setPrendaCalzado(pc);

        assertThrows(IllegalArgumentException.class, () -> conjuntoService.guardarConjunto(c));
    }

    @Test
    void testGuardarConjuntoSinPrendaCalzadoLanzaExcepcion() {
        Usuario u = nuevoUsuario("usuarioSinCalzado");
        PrendaSuperior ps = sup(u);
        PrendaInferior pi = inf(u);

        Conjunto c = new Conjunto();
        c.setNombre("Conjunto incompleto");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        // c.setPrendaCalzado(null); // faltante

        assertThrows(IllegalArgumentException.class, () -> conjuntoService.guardarConjunto(c));
    }
}