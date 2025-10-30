package com.nada.nada;

import com.nada.nada.data.model.Conjunto;
import com.nada.nada.data.model.Prenda;
import com.nada.nada.data.model.PrendaCalzado;
import com.nada.nada.data.model.PrendaInferior;
import com.nada.nada.data.model.PrendaSuperior;
import com.nada.nada.data.model.Usuario;
import com.nada.nada.data.repository.ConjuntoRepository;
import com.nada.nada.data.repository.PrendaCalzadoRepository;
import com.nada.nada.data.repository.PrendaInferiorRepository;
import com.nada.nada.data.repository.PrendaRepository;
import com.nada.nada.data.repository.PrendaSuperiorRepository;
import com.nada.nada.data.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class UsuarioRepositoryTests {

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

    private PrendaSuperior saveSuperior(PrendaSuperior p) {
        return prendaSuperiorRepository != null ? prendaSuperiorRepository.save(p) : (PrendaSuperior) prendaRepository.save(p);
    }

    private PrendaInferior saveInferior(PrendaInferior p) {
        return prendaInferiorRepository != null ? prendaInferiorRepository.save(p) : (PrendaInferior) prendaRepository.save(p);
    }

    private PrendaCalzado saveCalzado(PrendaCalzado p) {
        return prendaCalzadoRepository != null ? prendaCalzadoRepository.save(p) : (PrendaCalzado) prendaRepository.save(p);
    }

    @Test
    void contextLoads() {
        assertThat(usuarioRepository).isNotNull();
    }

    @Test
    @Transactional
    void usuarioPoseeMultiplesPrendas() {
        // Arrange
        Usuario u1 = new Usuario("Usuario Uno"); // Ajusta constructor/campos
        u1 = usuarioRepository.save(u1);

        PrendaSuperior sup = new PrendaSuperior("Camisa", u1); // Ajusta constructor/campos
        PrendaInferior inf = new PrendaInferior("Pantalón", u1);
        PrendaCalzado cal = new PrendaCalzado("Zapatillas", u1);

        // Si tus entidades mantienen la bidirección, añade la prenda al usuario también.
        u1.addPrenda(sup);
        u1.addPrenda(inf);
        u1.addPrenda(cal);

        saveSuperior(sup);
        saveInferior(inf);
        saveCalzado(cal);
        u1 = usuarioRepository.save(u1);

        // Act
        Usuario fetched = usuarioRepository.findById(u1.getId()).orElseThrow();

        // Assert
        assertThat(fetched.getPrendas()).hasSize(3);
        assertThat(fetched.getPrendas())
                .extracting(Prenda::getUsuario)
                .allMatch(u -> u.getId().equals(u1.getId()));
    }

    @Test
    @Transactional
    void usuarioCreaConjuntoConSusPrendas() {
        // Arrange: usuario y sus tres prendas
        Usuario u1 = usuarioRepository.save(new Usuario("Creador")); // Ajusta constructor
        PrendaSuperior sup = saveSuperior(new PrendaSuperior("Camisa Blanca", u1));
        PrendaInferior inf = saveInferior(new PrendaInferior("Jeans Azules", u1));
        PrendaCalzado cal = saveCalzado(new PrendaCalzado("Botas Negras", u1));

        // Mantener lado inverso si aplica
        u1.addPrenda(sup);
        u1.addPrenda(inf);
        u1.addPrenda(cal);
        usuarioRepository.save(u1);

        // Crear conjunto
        Conjunto conj = new Conjunto("Casual Día", u1); // Ajusta constructor/campos
        conj.setSuperior(sup);
        conj.setInferior(inf);
        conj.setCalzado(cal);

        conjuntoRepository.save(conj);

        // Act
        Conjunto fetched = conjuntoRepository.findById(conj.getId()).orElseThrow();

        // Assert
        assertThat(fetched.getUsuario().getId()).isEqualTo(u1.getId());
        assertThat(fetched.getSuperior()).isNotNull();
        assertThat(fetched.getInferior()).isNotNull();
        assertThat(fetched.getCalzado()).isNotNull();

        // Todas las prendas del conjunto pertenecen al mismo usuario creador
        assertThat(fetched.getSuperior().getUsuario().getId()).isEqualTo(u1.getId());
        assertThat(fetched.getInferior().getUsuario().getId()).isEqualTo(u1.getId());
        assertThat(fetched.getCalzado().getUsuario().getId()).isEqualTo(u1.getId());
    }

    @Test
    @Transactional
    void noPermiteConjuntoConPrendasDeOtroUsuario() {
        // Arrange: dos usuarios
        Usuario u1 = usuarioRepository.save(new Usuario("Creador U1"));
        Usuario u2 = usuarioRepository.save(new Usuario("Propietario U2"));

        // Prendas de usuarios distintos
        PrendaSuperior supU1 = saveSuperior(new PrendaSuperior("Camisa U1", u1));
        PrendaInferior infU1 = saveInferior(new PrendaInferior("Pantalón U1", u1));
        PrendaCalzado calU2 = saveCalzado(new PrendaCalzado("Zapatillas U2", u2)); // de otro usuario

        u1.addPrenda(supU1);
        u1.addPrenda(infU1);
        usuarioRepository.save(u1);

        // Conjunto de U1 con un calzado que pertenece a U2 debe fallar:
        Conjunto invalid = new Conjunto("Mixto ILEGAL", u1);
        invalid.setSuperior(supU1);
        invalid.setInferior(infU1);
        invalid.setCalzado(calU2); // viola la regla de negocio

        // Assert: o bien tu dominio lanza IllegalArgumentException, o la BD lanza DataIntegrityViolationException.
        assertThatThrownBy(() -> conjuntoRepository.saveAndFlush(invalid))
                .isInstanceOfAny(IllegalArgumentException.class, DataIntegrityViolationException.class);
    }
}

