package com.nada.nada.data.services;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.PrendaCalzadoRepository;
import com.nada.nada.data.repository.PrendaInferiorRepository;
import com.nada.nada.data.repository.PrendaRepository;
import com.nada.nada.data.repository.PrendaSuperiorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PrendaServiceImpl implements PrendaService {
    private static final Logger logger = LoggerFactory.getLogger(PrendaServiceImpl.class);

    private final PrendaRepository prendaRepository;
    private final PrendaSuperiorRepository prendaSuperiorRepository;
    private final PrendaInferiorRepository prendaInferiorRepository;
    private final PrendaCalzadoRepository prendaCalzadoRepository;

    @Autowired
    public PrendaServiceImpl(PrendaRepository prendaRepository,
                             PrendaSuperiorRepository prendaSuperiorRepository,
                             PrendaInferiorRepository prendaInferiorRepository,
                             PrendaCalzadoRepository prendaCalzadoRepository) {
        this.prendaRepository = prendaRepository;
        this.prendaSuperiorRepository = prendaSuperiorRepository;
        this.prendaInferiorRepository = prendaInferiorRepository;
        this.prendaCalzadoRepository = prendaCalzadoRepository;
    }

    @Override
    public Prenda guardarPrenda(Prenda prenda) {
        if (prenda == null) {
            logger.warn("guardarPrenda: prenda nula");
            throw new IllegalArgumentException("La prenda no puede ser nula");
        }
        // Validaciones básicas para evitar datos inválidos
        validacionPrendaBasic(prenda);

        // Si la prenda tiene usuario, comprobar que el id exista (si está disponible)
        if (prenda.getUsuario() != null && prenda.getUsuario().getId() == null) {
            logger.warn("guardarPrenda: usuario asociado sin ID");
            throw new IllegalArgumentException("El usuario asociado a la prenda debe tener un ID");
        }

        try {
            Prenda saved = prendaRepository.save(prenda);
            logger.info("Prenda guardada con id={}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("guardarPrenda: error al guardar prenda", e);
            throw new RuntimeException("Error al guardar la prenda: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Prenda> buscarPrendaPorId(Long id) {
        if (id == null) {
            logger.warn("buscarPrendaPorId: id nulo");
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        if (id <= 0) {
            logger.warn("buscarPrendaPorId: id no válido id={}", id);
            throw new IllegalArgumentException("El ID debe ser un número positivo");
        }
        try {
            // Usar el Optional que devuelve JpaRepository.findById
            Optional<Prenda> opt = prendaRepository.findById(id);
            if (!opt.isPresent()) {
                logger.info("buscarPrendaPorId: no encontrada prenda con id={}", id);
            } else {
                logger.debug("buscarPrendaPorId: encontrada prenda con id={}", id);
            }
            return opt;
        } catch (Exception e) {
            logger.error("buscarPrendaPorId: error al buscar prenda id={}", id, e);
            throw new RuntimeException("Error al buscar la prenda: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Prenda> buscarTodasPrendas() {
        try {
            List<Prenda> prendas = new ArrayList<>();
            prendaRepository.findAll().forEach(prendas::add);
            logger.debug("buscarTodasPrendas: obtenidas {} prendas", prendas.size());
            return prendas;
        } catch (Exception e) {
            logger.error("buscarTodasPrendas: error al obtener prendas", e);
            throw new RuntimeException("Error al buscar todas las prendas: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean borrarPrenda(Long id) {
        if (id == null) {
            logger.warn("borrarPrenda: id nulo");
            return false;
        }
        if (id <= 0) {
            logger.warn("borrarPrenda: id no válido id={}", id);
            return false;
        }
        try {
            if (!prendaRepository.existsById(id)) {
                logger.info("borrarPrenda: no existe prenda con id={}", id);
                return false;
            }
            prendaRepository.deleteById(id);
            boolean stillExists = prendaRepository.existsById(id);
            if (stillExists) {
                logger.error("borrarPrenda: fallo al borrar prenda id={}", id);
                return false;
            }
            logger.info("borrarPrenda: prenda borrada id={}", id);
            return true;
        } catch (Exception e) {
            logger.error("borrarPrenda: error al borrar prenda id={}", id, e);
            throw new RuntimeException("Error al borrar la prenda: " + e.getMessage(), e);
        }
    }

    @Override
    public Prenda actualizarPrenda(Prenda prenda) {
        if (prenda == null || prenda.getId() == null) {
            logger.warn("actualizarPrenda: prenda o id nulo");
            throw new IllegalArgumentException("La prenda y su ID no pueden ser nulos");
        }
        if (prenda.getId() <= 0) {
            logger.warn("actualizarPrenda: id no válido id={}", prenda.getId());
            throw new IllegalArgumentException("El ID debe ser un número positivo");
        }
        try {
            if (!prendaRepository.existsById(prenda.getId())) {
                logger.warn("actualizarPrenda: no existe prenda id={}", prenda.getId());
                throw new IllegalArgumentException("No existe una prenda con el ID: " + prenda.getId());
            }

            // Validaciones básicas antes de actualizar
            validacionPrendaBasic(prenda);

            Prenda updated = prendaRepository.save(prenda);
            logger.info("Prenda actualizada id={}", updated.getId());
            return updated;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("actualizarPrenda: error al actualizar prenda id={}", prenda.getId(), e);
            throw new RuntimeException("Error al actualizar la prenda: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Prenda> buscarPrendasPorUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            logger.warn("buscarPrendasPorUsuarioId: usuarioId nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (usuarioId <= 0) {
            logger.warn("buscarPrendasPorUsuarioId: usuarioId no válido id={}", usuarioId);
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo");
        }
        try {
            List<Prenda> prendas = new ArrayList<>();
            prendaRepository.findAll().forEach(prenda -> {
                if (prenda.getUsuario() != null && prenda.getUsuario().getId() != null && prenda.getUsuario().getId().equals(usuarioId)) {
                    prendas.add(prenda);
                }
            });
            logger.debug("buscarPrendasPorUsuarioId: usuarioId={} -> {} prendas", usuarioId, prendas.size());
            return prendas;
        } catch (Exception e) {
            logger.error("buscarPrendasPorUsuarioId: error al buscar prendas usuarioId={}", usuarioId, e);
            throw new RuntimeException("Error al buscar prendas del usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Prenda> buscarPrendasPorColor(Long usuarioId, String color) {
        if (usuarioId == null) {
            logger.warn("buscarPrendasPorColor: usuarioId nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (usuarioId <= 0) {
            logger.warn("buscarPrendasPorColor: usuarioId no válido id={}", usuarioId);
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo");
        }
        if (color == null || color.trim().isEmpty()) {
            logger.warn("buscarPrendasPorColor: color nulo o vacío para usuarioId={}", usuarioId);
            throw new IllegalArgumentException("El color no puede ser nulo o vacío");
        }
        try {
            List<Prenda> result = prendaRepository.findAllByUsuario_IdAndColorContainingIgnoreCase(usuarioId, color.trim());
            logger.debug("buscarPrendasPorColor: usuarioId={} color='{}' -> {} resultados", usuarioId, color, result.size());
            return result;
        } catch (Exception e) {
            logger.error("buscarPrendasPorColor: error usuarioId={} color={}", usuarioId, color, e);
            throw new RuntimeException("Error al buscar prendas por color: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Prenda> buscarPrendasPorMarca(Long usuarioId, String marca) {
        if (usuarioId == null) {
            logger.warn("buscarPrendasPorMarca: usuarioId nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (usuarioId <= 0) {
            logger.warn("buscarPrendasPorMarca: usuarioId no válido id={}", usuarioId);
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo");
        }
        if (marca == null || marca.trim().isEmpty()) {
            logger.warn("buscarPrendasPorMarca: marca nula o vacía para usuarioId={}", usuarioId);
            throw new IllegalArgumentException("La marca no puede ser nula o vacía");
        }
        try {
            List<Prenda> result = prendaRepository.findAllByUsuario_IdAndMarcaContainingIgnoreCase(usuarioId, marca.trim());
            logger.debug("buscarPrendasPorMarca: usuarioId={} marca='{}' -> {} resultados", usuarioId, marca, result.size());
            return result;
        } catch (Exception e) {
            logger.error("buscarPrendasPorMarca: error usuarioId={} marca={}", usuarioId, marca, e);
            throw new RuntimeException("Error al buscar prendas por marca: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Prenda> buscarPrendasPorTalla(Long usuarioId, String talla) {
        if (usuarioId == null) {
            logger.warn("buscarPrendasPorTalla: usuarioId nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (usuarioId <= 0) {
            logger.warn("buscarPrendasPorTalla: usuarioId no válido id={}", usuarioId);
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo");
        }
        if (talla == null || talla.trim().isEmpty()) {
            logger.warn("buscarPrendasPorTalla: talla nula o vacía para usuarioId={}", usuarioId);
            throw new IllegalArgumentException("La talla no puede ser nula o vacía");
        }
        try {
            List<Prenda> result = prendaRepository.findAllByUsuario_IdAndTallaIgnoreCase(usuarioId, talla.trim());
            logger.debug("buscarPrendasPorTalla: usuarioId={} talla='{}' -> {} resultados", usuarioId, talla, result.size());
            return result;
        } catch (Exception e) {
            logger.error("buscarPrendasPorTalla: error usuarioId={} talla={}", usuarioId, talla, e);
            throw new RuntimeException("Error al buscar prendas por talla: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PrendaSuperior> buscarPrendasSuperioresPorCategoria(Long usuarioId, CategoriaSuperior categoria) {
        if (usuarioId == null) {
            logger.warn("buscarPrendasSuperioresPorCategoria: usuarioId nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (usuarioId <= 0) {
            logger.warn("buscarPrendasSuperioresPorCategoria: usuarioId no válido id={}", usuarioId);
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo");
        }
        if (categoria == null) {
            logger.warn("buscarPrendasSuperioresPorCategoria: categoria nula para usuarioId={}", usuarioId);
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        try {
            List<PrendaSuperior> result = prendaSuperiorRepository.findAllByUsuario_IdAndCategoria(usuarioId, categoria);
            logger.debug("buscarPrendasSuperioresPorCategoria: usuarioId={} categoria={} -> {} resultados", usuarioId, categoria, result.size());
            return result;
        } catch (Exception e) {
            logger.error("buscarPrendasSuperioresPorCategoria: error usuarioId={} categoria={}", usuarioId, categoria, e);
            throw new RuntimeException("Error al buscar prendas superiores por categoría: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PrendaInferior> buscarPrendasInferioresPorCategoria(Long usuarioId, CategoriaInferior categoria) {
        if (usuarioId == null) {
            logger.warn("buscarPrendasInferioresPorCategoria: usuarioId nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (usuarioId <= 0) {
            logger.warn("buscarPrendasInferioresPorCategoria: usuarioId no válido id={}", usuarioId);
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo");
        }
        if (categoria == null) {
            logger.warn("buscarPrendasInferioresPorCategoria: categoria nula para usuarioId={}", usuarioId);
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        try {
            List<PrendaInferior> result = prendaInferiorRepository.findAllByUsuario_IdAndCategoriaInferior(usuarioId, categoria);
            logger.debug("buscarPrendasInferioresPorCategoria: usuarioId={} categoria={} -> {} resultados", usuarioId, categoria, result.size());
            return result;
        } catch (Exception e) {
            logger.error("buscarPrendasInferioresPorCategoria: error usuarioId={} categoria={}", usuarioId, categoria, e);
            throw new RuntimeException("Error al buscar prendas inferiores por categoría: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PrendaCalzado> buscarPrendasCalzadoPorCategoria(Long usuarioId, CategoriaCalzado categoria) {
        if (usuarioId == null) {
            logger.warn("buscarPrendasCalzadoPorCategoria: usuarioId nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (usuarioId <= 0) {
            logger.warn("buscarPrendasCalzadoPorCategoria: usuarioId no válido id={}", usuarioId);
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo");
        }
        if (categoria == null) {
            logger.warn("buscarPrendasCalzadoPorCategoria: categoria nula para usuarioId={}", usuarioId);
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        try {
            List<PrendaCalzado> result = prendaCalzadoRepository.findAllByUsuario_IdAndCategoria(usuarioId, categoria);
            logger.debug("buscarPrendasCalzadoPorCategoria: usuarioId={} categoria={} -> {} resultados", usuarioId, categoria, result.size());
            return result;
        } catch (Exception e) {
            logger.error("buscarPrendasCalzadoPorCategoria: error usuarioId={} categoria={}", usuarioId, categoria, e);
            throw new RuntimeException("Error al buscar calzado por categoría: " + e.getMessage(), e);
        }
    }

    @Override
    public PrendaSuperior guardarPrendaSuperior(PrendaSuperior prenda) {
        if (prenda == null) {
            logger.warn("guardarPrendaSuperior: prenda nula");
            throw new IllegalArgumentException("La prenda superior no puede ser nula");
        }
        validacionPrendaBasic(prenda);
        if (prenda.getCategoria() == null) {
            logger.warn("guardarPrendaSuperior: categoria nula");
            throw new IllegalArgumentException("La categoría de la prenda superior no puede ser nula");
        }
        try {
            PrendaSuperior saved = prendaSuperiorRepository.save(prenda);
            logger.info("PrendaSuperior guardada id={} categoria={}", saved.getId(), saved.getCategoria());
            return saved;
        } catch (Exception e) {
            logger.error("guardarPrendaSuperior: error al guardar", e);
            throw new RuntimeException("Error al guardar la prenda superior: " + e.getMessage(), e);
        }
    }

    @Override
    public PrendaInferior guardarPrendaInferior(PrendaInferior prenda) {
        if (prenda == null) {
            logger.warn("guardarPrendaInferior: prenda nula");
            throw new IllegalArgumentException("La prenda inferior no puede ser nula");
        }
        validacionPrendaBasic(prenda);
        if (prenda.getCategoriaInferior() == null) {
            logger.warn("guardarPrendaInferior: categoria nula");
            throw new IllegalArgumentException("La categoría de la prenda inferior no puede ser nula");
        }
        try {
            PrendaInferior saved = prendaInferiorRepository.save(prenda);
            logger.info("PrendaInferior guardada id={} categoria={}", saved.getId(), saved.getCategoriaInferior());
            return saved;
        } catch (Exception e) {
            logger.error("guardarPrendaInferior: error al guardar", e);
            throw new RuntimeException("Error al guardar la prenda inferior: " + e.getMessage(), e);
        }
    }

    @Override
    public PrendaCalzado guardarPrendaCalzado(PrendaCalzado prenda) {
        if (prenda == null) {
            logger.warn("guardarPrendaCalzado: prenda nula");
            throw new IllegalArgumentException("El calzado no puede ser nulo");
        }
        validacionPrendaBasic(prenda);
        if (prenda.getCategoria() == null) {
            logger.warn("guardarPrendaCalzado: categoria nula");
            throw new IllegalArgumentException("La categoría del calzado no puede ser nula");
        }
        try {
            PrendaCalzado saved = prendaCalzadoRepository.save(prenda);
            logger.info("PrendaCalzado guardada id={} categoria={}", saved.getId(), saved.getCategoria());
            return saved;
        } catch (Exception e) {
            logger.error("guardarPrendaCalzado: error al guardar", e);
            throw new RuntimeException("Error al guardar el calzado: " + e.getMessage(), e);
        }
    }

    @Override
    public long contarPrendasPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            logger.warn("contarPrendasPorUsuario: usuarioId nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (usuarioId <= 0) {
            logger.warn("contarPrendasPorUsuario: usuarioId no válido id={}", usuarioId);
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo");
        }
        try {
            long count = prendaRepository.countByUsuario_Id(usuarioId);
            logger.debug("contarPrendasPorUsuario: usuarioId={} -> {} prendas", usuarioId, count);
            return count;
        } catch (Exception e) {
            logger.error("contarPrendasPorUsuario: error usuarioId={}", usuarioId, e);
            throw new RuntimeException("Error al contar prendas del usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existePrendaPorId(Long id) {
        if (id == null) {
            logger.warn("existePrendaPorId: id nulo");
            return false;
        }
        if (id <= 0) {
            logger.warn("existePrendaPorId: id no válido id={}", id);
            return false;
        }
        try {
            boolean exists = prendaRepository.existsById(id);
            logger.debug("existePrendaPorId: id={} -> {}", id, exists);
            return exists;
        } catch (Exception e) {
            logger.error("existePrendaPorId: error id={}", id, e);
            return false;
        }
    }

    @Override
    public List<Prenda> buscarPrendasFiltradas(Long usuarioId,
                                               String nombreEmpiezaPor,
                                               String tipoPrenda,
                                               String categoria,
                                               String color,
                                               String marca,
                                               String talla) {
        // Partimos de todas las prendas del usuario y filtramos en memoria.
        List<Prenda> base = buscarPrendasPorUsuarioId(usuarioId);

        if (base.isEmpty()) {
            return base;
        }

        return base.stream()
                .filter(p -> {
                    if (nombreEmpiezaPor != null && !nombreEmpiezaPor.isBlank()) {
                        if (p.getNombre() == null || !p.getNombre().toLowerCase().startsWith(nombreEmpiezaPor.toLowerCase())) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter(p -> {
                    if (color != null && !color.isBlank()) {
                        if (p.getColor() == null || !p.getColor().toLowerCase().contains(color.toLowerCase())) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter(p -> {
                    if (marca != null && !marca.isBlank()) {
                        if (p.getMarca() == null || !p.getMarca().toLowerCase().contains(marca.toLowerCase())) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter(p -> {
                    if (talla != null && !talla.isBlank()) {
                        if (p.getTalla() == null || !p.getTalla().equalsIgnoreCase(talla.trim())) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter(p -> {
                    if (tipoPrenda == null || tipoPrenda.isBlank() || "todos".equalsIgnoreCase(tipoPrenda)) {
                        return true;
                    }
                    return switch (tipoPrenda) {
                        case "superior" -> p instanceof PrendaSuperior;
                        case "inferior" -> p instanceof PrendaInferior;
                        case "calzado" -> p instanceof PrendaCalzado;
                        default -> true;
                    };
                })
                .filter(p -> {
                    if (categoria == null || categoria.isBlank()) {
                        return true;
                    }
                    if (p instanceof PrendaSuperior sup) {
                        return sup.getCategoria() != null && sup.getCategoria().name().equalsIgnoreCase(categoria);
                    } else if (p instanceof PrendaInferior inf) {
                        return inf.getCategoriaInferior() != null && inf.getCategoriaInferior().name().equalsIgnoreCase(categoria);
                    } else if (p instanceof PrendaCalzado cal) {
                        return cal.getCategoria() != null && cal.getCategoria().name().equalsIgnoreCase(categoria);
                    }
                    return true;
                })
                .toList();
    }

    /**
     * Método auxiliar para validar campos básicos comunes de cualquier prenda.
     * Lanza IllegalArgumentException si encuentra datos inválidos.
     */
    private void validacionPrendaBasic(Prenda prenda) {
        // Validar que la prenda esté asociada a un usuario
        if (prenda.getUsuario() == null) {
            logger.warn("validacionPrendaBasic: usuario nulo en prenda");
            throw new IllegalArgumentException("La prenda debe estar asociada a un usuario");
        }

        // Validar longitud del color
        if (prenda.getColor() != null) {
            String color = prenda.getColor().trim();
            if (color.isEmpty()) {
                logger.warn("validacionPrendaBasic: color vacío");
                throw new IllegalArgumentException("El color no puede estar vacío");
            }
            if (color.length() > 50) {
                logger.warn("validacionPrendaBasic: color demasiado largo (len={})", color.length());
                throw new IllegalArgumentException("El color no puede tener más de 50 caracteres");
            }
        }

        // Validar longitud de la marca
        if (prenda.getMarca() != null) {
            String marca = prenda.getMarca().trim();
            if (marca.isEmpty()) {
                logger.warn("validacionPrendaBasic: marca vacía");
                throw new IllegalArgumentException("La marca no puede estar vacía");
            }
            if (marca.length() > 100) {
                logger.warn("validacionPrendaBasic: marca demasiado larga (len={})", marca.length());
                throw new IllegalArgumentException("La marca no puede tener más de 100 caracteres");
            }
        }

        // Validar longitud de la talla
        if (prenda.getTalla() != null) {
            String talla = prenda.getTalla().trim();
            if (talla.isEmpty()) {
                logger.warn("validacionPrendaBasic: talla vacía");
                throw new IllegalArgumentException("La talla no puede estar vacía");
            }
            if (talla.length() > 20) {
                logger.warn("validacionPrendaBasic: talla demasiado larga (len={})", talla.length());
                throw new IllegalArgumentException("La talla no puede tener más de 20 caracteres");
            }
        }

        // Validar nombre de la prenda
        if (prenda.getNombre() != null) {
            String nombre = prenda.getNombre().trim();
            if (nombre.isEmpty()) {
                logger.warn("validacionPrendaBasic: nombre vacío");
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }
            if (nombre.length() > 100) {
                logger.warn("validacionPrendaBasic: nombre demasiado largo (len={})", nombre.length());
                throw new IllegalArgumentException("El nombre no puede tener más de 100 caracteres");
            }
        }
    }
}
