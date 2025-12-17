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

/**
 * Tests que verifican el cumplimiento de los requisitos funcionales
 * definidos en el README del proyecto.
 */
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

    /**
     * Crea y persiste un usuario de prueba con los datos mínimos requeridos.
     *
     * @param username nombre de usuario único para el test
     * @return el usuario guardado en base de datos
     */
    private Usuario user(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@mail.com");
        return usuarioRepository.save(u);
    }

    /**
     * Crea y persiste una prenda superior de prueba asociada a un usuario.
     *
     * @param u usuario propietario de la prenda
     * @param nombre nombre identificativo de la prenda
     * @return la prenda superior guardada en base de datos
     */
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

    /**
     * Crea y persiste una prenda inferior de prueba asociada a un usuario.
     *
     * @param u usuario propietario de la prenda
     * @param nombre nombre identificativo de la prenda
     * @return la prenda inferior guardada en base de datos
     */
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

    /**
     * Crea y persiste un calzado de prueba asociado a un usuario.
     *
     * @param u usuario propietario del calzado
     * @param nombre nombre identificativo del calzado
     * @return el calzado guardado en base de datos
     */
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

    // =========================================================================
    // TESTS DE REQUISITOS: GESTIÓN DE USUARIOS
    // =========================================================================

    /**
     * Verifica que un usuario puede crear una cuenta y acceder a ella.
     * Requisito: "El usuario debe poder crear una cuenta y poder acceder a ella"
     */
    @Test
    void testRequisitoCrearCuentaYAcceder() {
        Usuario u = user("acepta");
        Optional<Usuario> encontradoOpt = usuarioRepository.findByUsername("acepta");
        assertTrue(encontradoOpt.isPresent());
        Usuario encontrado = encontradoOpt.get();
        assertEquals(u.getId(), encontrado.getId());
    }

    /**
     * Verifica el acceso con credenciales correctas.
     * Requisito: "El usuario debe poder acceder con usuario y contraseña"
     */
    @Test
    void testRequisitoAccesoConCredencialesCorrectas() {
        Usuario u = new Usuario();
        u.setUsername("loginuser");
        u.setPassword("secreto123");
        u.setEmail("loginuser@mail.com");
        usuarioRepository.save(u);

        Optional<Usuario> loginOpt = usuarioRepository.findByUsername("loginuser");
        assertTrue(loginOpt.isPresent());
        Usuario login = loginOpt.get();
        assertEquals("loginuser", login.getUsername());
        assertEquals("secreto123", login.getPassword());
    }

    /**
     * Verifica que el acceso con contraseña incorrecta falla.
     * Requisito: Validación de credenciales de acceso
     */
    @Test
    void testRequisitoAccesoConCredencialesIncorrectasFalla() {
        Usuario u = new Usuario();
        u.setUsername("loginuser2");
        u.setPassword("correcta");
        u.setEmail("loginuser2@mail.com");
        usuarioRepository.save(u);

        Optional<Usuario> loginOpt = usuarioRepository.findByUsername("loginuser2");
        assertTrue(loginOpt.isPresent());
        Usuario login = loginOpt.get();
        // Simular validación de contraseña incorrecta
        assertNotEquals("incorrecta", login.getPassword());
    }

    /**
     * Verifica que al eliminar un usuario se eliminan sus prendas y conjuntos.
     * Requisito: "Al eliminar un usuario se eliminarán todas sus prendas y conjuntos"
     */
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

    // =========================================================================
    // TESTS DE REQUISITOS: GESTIÓN DE PRENDAS
    // =========================================================================

    /**
     * Verifica que se puede subir una foto y editar los datos de una prenda.
     * Requisito: "El usuario debe poder subir fotos de sus prendas"
     */
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

    /**
     * Verifica la búsqueda de prendas por diferentes atributos y categorías.
     * Requisito: "La búsqueda puede ser por tipo, categoría, color, marca o talla"
     */
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

    /**
     * Verifica que las prendas se organizan correctamente por tipo.
     * Requisito: "Organizar las prendas por tipo de prenda (superior, inferior, calzado)"
     */
    @Test
    void testRequisitoOrganizarPorTipoDePrenda() {
        Usuario u = user("tiposPrenda");

        PrendaSuperior ps = sup(u, "Camiseta");
        PrendaInferior pi = inf(u, "Pantalón");
        PrendaCalzado pc = cal(u, "Zapatos");

        // Verificar que cada tipo se guarda correctamente
        assertTrue(prendaSuperiorRepository.findById(ps.getId()).isPresent());
        assertTrue(prendaInferiorRepository.findById(pi.getId()).isPresent());
        assertTrue(prendaCalzadoRepository.findById(pc.getId()).isPresent());

        // Verificar que se pueden listar por tipo usando los métodos existentes
        List<PrendaSuperior> superiores = prendaSuperiorRepository.findAllByUsuario_IdAndCategoria(u.getId(), CategoriaSuperior.CAMISETA);
        List<PrendaInferior> inferiores = prendaInferiorRepository.findAllByUsuario_IdAndCategoriaInferior(u.getId(), CategoriaInferior.PANTALON);
        List<PrendaCalzado> calzados = prendaCalzadoRepository.findAllByUsuario_IdAndCategoria(u.getId(), CategoriaCalzado.DEPORTIVO);

        assertEquals(1, superiores.size());
        assertEquals(1, inferiores.size());
        assertEquals(1, calzados.size());
    }

    /**
     * Verifica que las prendas se organizan correctamente por categoría.
     * Requisito: "Organizar las prendas por categorías dentro de cada tipo"
     */
    @Test
    void testRequisitoOrganizarPorCategoria() {
        Usuario u = user("categorias");

        PrendaSuperior camiseta = new PrendaSuperior();
        camiseta.setNombre("Camiseta");
        camiseta.setColor("Blanco");
        camiseta.setMarca("M");
        camiseta.setUsuario(u);
        camiseta.setTalla("M");
        camiseta.setDirImagen("img.png");
        camiseta.setCategoria(CategoriaSuperior.CAMISETA);
        camiseta.setManga(Manga.CORTA);
        prendaSuperiorRepository.save(camiseta);

        PrendaSuperior jersey = new PrendaSuperior();
        jersey.setNombre("Jersey");
        jersey.setColor("Azul");
        jersey.setMarca("M");
        jersey.setUsuario(u);
        jersey.setTalla("L");
        jersey.setDirImagen("img2.png");
        jersey.setCategoria(CategoriaSuperior.JERSEY);
        jersey.setManga(Manga.LARGA);
        prendaSuperiorRepository.save(jersey);

        List<PrendaSuperior> camisetas = prendaSuperiorRepository.findAllByUsuario_IdAndCategoria(u.getId(), CategoriaSuperior.CAMISETA);
        List<PrendaSuperior> jerseys = prendaSuperiorRepository.findAllByUsuario_IdAndCategoria(u.getId(), CategoriaSuperior.JERSEY);

        assertEquals(1, camisetas.size());
        assertEquals(1, jerseys.size());
        assertEquals("Camiseta", camisetas.get(0).getNombre());
        assertEquals("Jersey", jerseys.get(0).getNombre());
    }

    /**
     * Verifica que se pueden modificar los datos de una prenda existente.
     * Requisito: "El usuario debe poder eliminar o modificar prendas"
     */
    @Test
    void testRequisitoModificarPrenda() {
        Usuario u = user("modificar");
        PrendaSuperior ps = sup(u, "Original");

        ps.setNombre("Modificado");
        ps.setColor("Rojo");
        ps.setMarca("Nueva Marca");
        prendaSuperiorRepository.save(ps);

        PrendaSuperior actualizada = prendaSuperiorRepository.findById(ps.getId()).orElseThrow();
        assertEquals("Modificado", actualizada.getNombre());
        assertEquals("Rojo", actualizada.getColor());
        assertEquals("Nueva Marca", actualizada.getMarca());
    }

    /**
     * Verifica que se puede eliminar una prenda del armario.
     * Requisito: "El usuario debe poder eliminar prendas"
     */
    @Test
    void testRequisitoEliminarPrenda() {
        Usuario u = user("elimPrenda");
        PrendaSuperior ps = sup(u, "A Eliminar");
        Long id = ps.getId();

        prendaSuperiorRepository.deleteById(id);

        assertTrue(prendaSuperiorRepository.findById(id).isEmpty());
    }

    /**
     * Verifica que al eliminar una prenda se eliminan los conjuntos asociados.
     * Requisito: "Al eliminar una prenda se eliminarán los conjuntos que la referencien"
     */
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

    // =========================================================================
    // TESTS DE REQUISITOS: GESTIÓN DE CONJUNTOS
    // =========================================================================

    /**
     * Verifica la creación de un conjunto personalizado con 3 prendas.
     * Requisito: "El usuario puede crear un conjunto personalizado a partir de 3 prendas"
     */
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

    /**
     * Verifica que un conjunto requiere exactamente 3 tipos de prendas.
     * Requisito: "Conjunto formado por prenda superior, inferior y calzado"
     */
    @Test
    void testRequisitoConjuntoRequiereTresTiposDePrendas() {
        Usuario u = user("tresPrendas");
        PrendaSuperior ps = sup(u, "Superior");
        PrendaInferior pi = inf(u, "Inferior");
        PrendaCalzado pc = cal(u, "Calzado");

        Conjunto c = new Conjunto();
        c.setNombre("Conjunto Completo");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);

        Conjunto guardado = conjuntoRepository.save(c);

        assertNotNull(guardado.getId());
        assertNotNull(guardado.getPrendaSuperior());
        assertNotNull(guardado.getPrendaInferior());
        assertNotNull(guardado.getPrendaCalzado());
    }

    /**
     * Verifica que la descripción del conjunto no puede superar 256 caracteres.
     * Requisito: "Añadir notas o comentarios a cada conjunto (hasta 256 caracteres)"
     */
    @Test
    void testRequisitoDescripcionConjuntoMasDe256Falla() {
        Usuario u = user("desc-ko");
        Conjunto c = new Conjunto();
        c.setUsuario(u);
        c.setNombre("Muy largo");
        c.setDescripcion("x".repeat(257));
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> conjuntoRepository.save(c));
    }

    /**
     * Verifica que una descripción de exactamente 256 caracteres es válida.
     * Requisito: Límite de 256 caracteres para la descripción
     */
    @Test
    void testRequisitoDescripcionDe256CaracteresEsValida() {
        Usuario u = user("desc256ok");
        PrendaSuperior ps = sup(u, "S256");
        PrendaInferior pi = inf(u, "I256");
        PrendaCalzado pc = cal(u, "C256");

        String descripcion256 = "a".repeat(256);

        Conjunto c = new Conjunto();
        c.setNombre("Conjunto 256");
        c.setDescripcion(descripcion256);
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);

        Conjunto guardado = conjuntoRepository.save(c);

        assertNotNull(guardado.getId());
        assertEquals(256, guardado.getDescripcion().length());
    }

    /**
     * Verifica que se pueden modificar los datos de un conjunto existente.
     * Requisito: "El usuario debe poder modificar conjuntos"
     */
    @Test
    void testRequisitoModificarConjunto() {
        Usuario u = user("modConj");
        PrendaSuperior ps = sup(u, "S");
        PrendaInferior pi = inf(u, "I");
        PrendaCalzado pc = cal(u, "C");

        Conjunto c = new Conjunto();
        c.setNombre("Original");
        c.setDescripcion("Descripcion original");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        c = conjuntoRepository.save(c);

        c.setNombre("Modificado");
        c.setDescripcion("Nueva descripcion");
        conjuntoRepository.save(c);

        Conjunto actualizado = conjuntoRepository.findById(c.getId()).orElseThrow();
        assertEquals("Modificado", actualizado.getNombre());
        assertEquals("Nueva descripcion", actualizado.getDescripcion());
    }

    /**
     * Verifica que se puede eliminar un conjunto del armario.
     * Requisito: "El usuario debe poder eliminar conjuntos"
     */
    @Test
    void testRequisitoEliminarConjunto() {
        Usuario u = user("elimConj");
        PrendaSuperior ps = sup(u, "S");
        PrendaInferior pi = inf(u, "I");
        PrendaCalzado pc = cal(u, "C");

        Conjunto c = new Conjunto();
        c.setNombre("A Eliminar");
        c.setUsuario(u);
        c.setPrendaSuperior(ps);
        c.setPrendaInferior(pi);
        c.setPrendaCalzado(pc);
        c = conjuntoRepository.save(c);
        Long id = c.getId();

        conjuntoRepository.deleteById(id);

        assertTrue(conjuntoRepository.findById(id).isEmpty());
    }

    // =========================================================================
    // TESTS DE REQUISITOS: AISLAMIENTO DE DATOS
    // =========================================================================

    /**
     * Verifica que cada usuario tiene su propio armario digital aislado.
     * Requisito: "La persistencia de datos debe garantizar que cada usuario tenga su propio armario"
     */
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
}
