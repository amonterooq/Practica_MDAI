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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class ConjuntoServiceImpl implements ConjuntoService {

    private static final Logger logger = LoggerFactory.getLogger(ConjuntoServiceImpl.class);

    private final ConjuntoRepository conjuntoRepository;
    private final PrendaSuperiorRepository prendaSuperiorRepository;
    private final PrendaInferiorRepository prendaInferiorRepository;
    private final PrendaCalzadoRepository prendaCalzadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PostService postService;

    @Autowired
    public ConjuntoServiceImpl(ConjuntoRepository conjuntoRepository, PrendaSuperiorRepository prendaSuperiorRepository, PrendaInferiorRepository prendaInferiorRepository,
                               PrendaCalzadoRepository prendaCalzadoRepository, UsuarioRepository usuarioRepository, PostService postService) {
        this.conjuntoRepository = conjuntoRepository;
        this.prendaSuperiorRepository = prendaSuperiorRepository;
        this.prendaInferiorRepository = prendaInferiorRepository;
        this.prendaCalzadoRepository = prendaCalzadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.postService = postService;
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
        List<Conjunto> conjuntos = conjuntoRepository.findByUsuario_Id(usuarioId);
        // Ordenar por id descendente: el conjunto más reciente (id más alto) primero
        conjuntos.sort((c1, c2) -> {
            Long id1 = c1.getId();
            Long id2 = c2.getId();
            if (id1 == null && id2 == null) return 0;
            if (id1 == null) return 1;
            if (id2 == null) return -1;
            return id2.compareTo(id1);
        });
        return conjuntos;
    }

    @Override
    public boolean borrarConjunto(Long id) {
        if (id == null) {
            return false;
        }
        try {
            Optional<Conjunto> conjuntoOpt = conjuntoRepository.findById(id);
            if (conjuntoOpt.isPresent()) {
                Conjunto conjunto = conjuntoOpt.get();

                // Si el conjunto tiene un post asociado, limpiar sus likes antes de borrar
                // El orphanRemoval de Conjunto borrará el post automáticamente al hacer setPost(null)
                if (conjunto.getPost() != null) {
                    Long postId = conjunto.getPost().getId();

                    // Eliminar los likes usando el servicio
                    postService.eliminarLikesDelPost(postId);

                    // NO llamar a eliminarPost aquí, el orphanRemoval ya lo hará
                }

                // Al borrar el conjunto, orphanRemoval borra automáticamente el post asociado
                conjuntoRepository.delete(conjunto);
                return true;
            }
            return false;
        } catch (EmptyResultDataAccessException erdae) {
            return false;
        } catch (Exception e) {
            logger.error("Error al borrar el conjunto con id: " + id, e);
            throw new RuntimeException("Error al borrar el conjunto", e);
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

        // Validar que el conjunto tiene exactamente las tres prendas necesarias
        if (conjunto.getPrendaSuperior() == null ||
                conjunto.getPrendaInferior() == null ||
                conjunto.getPrendaCalzado() == null) {
            throw new IllegalArgumentException("El conjunto debe tener una prenda superior, una inferior y un calzado");
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

    @Override
    public List<Conjunto> buscarConjuntosConPrenda(Long prendaId) {
        if (prendaId == null || prendaId <= 0) {
            logger.warn("buscarConjuntosConPrenda: prendaId inválido");
            return List.of();
        }

        try {
            List<Conjunto> todos = StreamSupport
                .stream(conjuntoRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

            List<Conjunto> afectados = todos.stream()
                .filter(c -> {
                    if (c.getPrendaSuperior() != null && prendaId.equals(c.getPrendaSuperior().getId())) return true;
                    if (c.getPrendaInferior() != null && prendaId.equals(c.getPrendaInferior().getId())) return true;
                    return c.getPrendaCalzado() != null && prendaId.equals(c.getPrendaCalzado().getId());
                })
                .collect(Collectors.toList());

            logger.debug("buscarConjuntosConPrenda: prendaId={} -> {} conjuntos afectados", prendaId, afectados.size());
            return afectados;
        } catch (Exception e) {
            logger.error("buscarConjuntosConPrenda: error al buscar conjuntos con prendaId={}", prendaId, e);
            return List.of();
        }
    }
}
