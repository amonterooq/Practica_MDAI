package com.nada.nada;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/*
  PrendaSubrepositoriosSmokeTest
  --------------------------------
  Propósito de esta prueba "smoke":
  - Verificar que el contexto de Spring arranca correctamente con el perfil 'test'.
  - Validar CRUD básico en cada sub-repositorio (PrendaSuperior, PrendaInferior, PrendaCalzado).
  - Comprobar que los mapeos JPA y las FKs hacia Usuario están bien cableados.
  Qué NO cubre:
  - Reglas de negocio ni búsquedas avanzadas; es una alarma temprana ante roturas de configuración/entidades.
  Cómo leerla:
  - u(): crea y persiste un Usuario de prueba.
  - crudBasicoEnCadaSubRepositorio(): persiste una prenda de cada subtipo y verifica su recuperación por id.
  Nota:
  - Si aparece "Unexpected token" al abrir este archivo, suele ser por BOM/encoding. Guarda el archivo como UTF-8 sin BOM.
*/
@SpringBootTest
@ActiveProfiles("test")
class PrendaSubrepositoriosSmokeTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PrendaSuperiorRepository prendaSuperiorRepository;
    @Autowired private PrendaInferiorRepository prendaInferiorRepository;
    @Autowired private PrendaCalzadoRepository prendaCalzadoRepository;

    /**
     * Crea y guarda un Usuario "owner" para asociar prendas en los tests.
     */
    private Usuario u() {
        Usuario u = new Usuario();
        u.setUsername("u-smoke");
        u.setPassword("pwd");
        u.setEmail("u@smoke.com");
        return usuarioRepository.save(u);
    }

    /**
     * Smoke test: persiste una prenda de cada subtipo y verifica que se recuperan por id.
     */
    @Test
    void crudBasicoEnCadaSubRepositorio() {
        Usuario owner = u();

        PrendaSuperior sup = new PrendaSuperior();
        sup.setNombre("Sup-smoke");
        sup.setColor("C");
        sup.setMarca("M");
        sup.setUsuario(owner);
        sup.setTalla("M");
        sup.setUrlImagen("u");
        sup.setCategoria(CategoriaSuperior.CAMISETA);
        sup.setManga(Manga.CORTA);
        sup = prendaSuperiorRepository.save(sup);

        PrendaInferior inf = new PrendaInferior();
        inf.setNombre("Inf-smoke");
        inf.setColor("C");
        inf.setMarca("M");
        inf.setUsuario(owner);
        inf.setTalla("32");
        inf.setUrlImagen("u");
        inf.setCategoriaInferior(CategoriaInferior.PANTALON);
        inf = prendaInferiorRepository.save(inf);

        PrendaCalzado calz = new PrendaCalzado();
        calz.setNombre("Calz-smoke");
        calz.setColor("C");
        calz.setMarca("M");
        calz.setUsuario(owner);
        calz.setTalla("42");
        calz.setUrlImagen("u");
        calz.setCategoria(CategoriaCalzado.DEPORTIVO);
        calz = prendaCalzadoRepository.save(calz);

        assertTrue(prendaSuperiorRepository.findById(sup.getId()).isPresent());
        assertTrue(prendaInferiorRepository.findById(inf.getId()).isPresent());
        assertTrue(prendaCalzadoRepository.findById(calz.getId()).isPresent());
    }
}
