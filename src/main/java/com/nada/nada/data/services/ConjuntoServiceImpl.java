package com.nada.nada.data.services;

import com.nada.nada.data.model.Conjunto;
import com.nada.nada.data.model.PrendaCalzado;
import com.nada.nada.data.model.PrendaInferior;
import com.nada.nada.data.model.PrendaSuperior;
import com.nada.nada.data.repository.ConjuntoRepository;
import com.nada.nada.data.repository.PrendaCalzadoRepository;
import com.nada.nada.data.repository.PrendaInferiorRepository;
import com.nada.nada.data.repository.PrendaSuperiorRepository;
import com.nada.nada.data.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConjuntoServiceImpl implements ConjuntoService {

    private final ConjuntoRepository conjuntoRepository;
    private final PrendaSuperiorRepository prendaSuperiorRepository;
    private final PrendaInferiorRepository prendaInferiorRepository;
    private final PrendaCalzadoRepository prendaCalzadoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ConjuntoServiceImpl(ConjuntoRepository conjuntoRepository, PrendaSuperiorRepository prendaSuperiorRepository, PrendaInferiorRepository prendaInferiorRepository,
                               PrendaCalzadoRepository prendaCalzadoRepository, UsuarioRepository usuarioRepository) {
        this.conjuntoRepository = conjuntoRepository;
        this.prendaSuperiorRepository = prendaSuperiorRepository;
        this.prendaInferiorRepository = prendaInferiorRepository;
        this.prendaCalzadoRepository = prendaCalzadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Conjunto guardarConjunto(Conjunto conjunto) {
        validacionConjuntoBasico(conjunto);

        // comprobar que usuario existe en BD
        Long usuarioId = conjunto.getUsuario().getId();
        if (usuarioId == null || !usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException("El usuario asociado al conjunto no existe o no tiene ID");
        }
        validacionPrendasPertenencia(conjunto, usuarioId);

        try {
            return conjuntoRepository.save(conjunto);
        } catch (DataIntegrityViolationException dive) {
            throw new RuntimeException("Error de integridad al guardar el conjunto: " + dive.getMessage(), dive);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al guardar el conjunto", e);
        }
    }

    @Override
    public Optional<Conjunto> buscarConjuntoPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        return conjuntoRepository.findById(id);
    }

    @Override
    public List<Conjunto> buscarConjuntosPorUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El id de usuario no puede ser nulo");
        }
        return conjuntoRepository.findByUsuario_Id(usuarioId);
    }

    @Override
    public boolean borrarConjunto(Long id) {
        if (id == null) {
            return false;
        }
        try {
            conjuntoRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException erdae) {
            // Ya fue eliminado o no existía
            return false;
        } catch (DataIntegrityViolationException dive) {
            throw new RuntimeException("No se pudo eliminar el conjunto por violación de integridad: " + dive.getMessage(), dive);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al eliminar el conjunto", e);
        }
    }

    // Validaciones auxiliares
    private void validacionConjuntoBasico(Conjunto conjunto) {
        if (conjunto == null){
            throw new IllegalArgumentException("El conjunto no puede ser nulo");
        } else if (conjunto.getUsuario() == null){
            throw new IllegalArgumentException("El conjunto debe pertenecer a un usuario");
        } else if (conjunto.getNombre() == null || conjunto.getNombre().trim().isEmpty()){
            throw new IllegalArgumentException("El nombre del conjunto no puede estar vacío");
        } else if (conjunto.getDescripcion() != null && conjunto.getDescripcion().length() > 256){
            throw new IllegalArgumentException("La descripción no puede superar los 256 caracteres");
        }
    }

    private void validacionPrendasPertenencia(Conjunto conjunto, Long usuarioId) {
        // superior
        if (conjunto.getPrendaSuperior() != null) {
            Long id = conjunto.getPrendaSuperior().getId();
            if (id == null) throw new IllegalArgumentException("La prenda superior debe tener ID");
            PrendaSuperior ps = prendaSuperiorRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("La prenda superior con id " + id + " no existe"));
            if (ps.getUsuario() == null || !usuarioId.equals(ps.getUsuario().getId()))
                throw new IllegalArgumentException("La prenda superior no pertenece al usuario del conjunto");
        }
        // inferior
        if (conjunto.getPrendaInferior() != null) {
            Long id = conjunto.getPrendaInferior().getId();
            if (id == null) throw new IllegalArgumentException("La prenda inferior debe tener ID");
            PrendaInferior pi = prendaInferiorRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("La prenda inferior con id " + id + " no existe"));
            if (pi.getUsuario() == null || !usuarioId.equals(pi.getUsuario().getId()))
                throw new IllegalArgumentException("La prenda inferior no pertenece al usuario del conjunto");
        }
        // calzado
        if (conjunto.getPrendaCalzado() != null) {
            Long id = conjunto.getPrendaCalzado().getId();
            if (id == null) throw new IllegalArgumentException("La prenda de calzado debe tener ID");
            PrendaCalzado pc = prendaCalzadoRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("La prenda de calzado con id " + id + " no existe"));
            if (pc.getUsuario() == null || !usuarioId.equals(pc.getUsuario().getId()))
                throw new IllegalArgumentException("La prenda de calzado no pertenece al usuario del conjunto");
        }
    }
}
