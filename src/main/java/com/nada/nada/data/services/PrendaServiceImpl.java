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
    private final Logger logger = LoggerFactory.getLogger(PrendaServiceImpl.class); // Dejo logger para buscar errores
    private final java.nio.file.Path IMAGES_BASE_DIR = java.nio.file.Paths.get("/app/static/images");

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
    public String normalizarMarca(String marcaIntroducida) {
        if (marcaIntroducida == null) {
            return null;
        }
        String raw = marcaIntroducida.trim();
        if (raw.isEmpty()) {
            return "";
        }

        String lower = raw.toLowerCase();

        // Tabla de marcas conocidas: clave en minúsculas -> forma estándar
        String[][] marcas = new String[][]{
                {"zara", "Zara"},
                {"mango", "Mango"},
                {"h&m", "H&M"},
                {"h & m", "H&M"},
                {"pull&bear", "Pull&Bear"},
                {"pull & bear", "Pull&Bear"},
                {"bershka", "Bershka"},
                {"stradivarius", "Stradivarius"},
                {"massimo dutti", "Massimo Dutti"},
                {"primark", "Primark"},
                {"springfield", "Springfield"},
                {"cortefiel", "Cortefiel"},
                {"lefties", "Lefties"},
                {"nike", "Nike"},
                {"adidas", "Adidas"},
                {"reebok", "Reebok"},
                {"puma", "Puma"},
                {"new balance", "New Balance"},
                {"converse", "Converse"},
                {"vans", "Vans"},
                {"levi's", "Levi's"},
                {"levis", "Levi's"},
                {"tommy hilfiger", "Tommy Hilfiger"},
                {"calvin klein", "Calvin Klein"},
                {"guess", "Guess"},
                {"desigual", "Desigual"},
                {"sfera", "Sfera"},
                {"pepe jeans", "Pepe Jeans"},
                {"only", "Only"},
                {"jack&jones", "Jack&Jones"},
                {"jack & jones", "Jack&Jones"},
                {"uniqlo", "Uniqlo"},
                {"benetton", "Benetton"},
                {"el corte ingles", "El Corte Inglés"},
                {"el corte inglés", "El Corte Inglés"}
        };

        for (String[] par : marcas) {
            if (lower.equals(par[0])) {
                return par[1];
            }
        }

        // Si no coincide con ninguna conocida, capitalizar de forma sencilla
        if (raw.length() == 1) {
            return raw.toUpperCase();
        }
        return raw.substring(0, 1).toUpperCase() + raw.substring(1).toLowerCase();
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
            Optional<Prenda> optPrenda = prendaRepository.findById(id);
            if (optPrenda.isEmpty()) {
                logger.info("borrarPrenda: no existe prenda con id={}", id);
                return false;
            }

            Prenda prenda = optPrenda.get();
            String dirImagen = prenda.getDirImagen();

            prendaRepository.deleteById(id);
            boolean stillExists = prendaRepository.existsById(id);
            if (stillExists) {
                logger.error("borrarPrenda: fallo al borrar prenda id={}", id);
                return false;
            }

            // Si la prenda tenía imagen asociada, intentamos borrar solo ese fichero
            if (dirImagen != null && !dirImagen.isBlank()) {
                try {
                    // dirImagen tiene formato "/images/{usuarioId}/fichero.jpg"
                    String relativePath = dirImagen.startsWith("/images/") ? dirImagen.substring("/images/".length()) : dirImagen;
                    java.nio.file.Path imagePath = IMAGES_BASE_DIR.resolve(relativePath.replace("/", java.io.File.separator));
                    java.nio.file.Files.deleteIfExists(imagePath);
                    logger.info("borrarPrenda: imagen borrada {}", imagePath.toAbsolutePath());
                } catch (Exception e) {
                    logger.warn("borrarPrenda: no se pudo borrar la imagen asociada a la prenda id={}: {}", id, e.getMessage());
                }
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
            // Aquí asumimos que 'prenda' ya es una entidad gestionada con los cambios aplicados
            Prenda updated = prendaRepository.save(prenda);
            logger.info("Prenda actualizada (simple) id={}", updated.getId());
            return updated;
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
                        return p.getNombre() != null && p.getNombre().toLowerCase().startsWith(nombreEmpiezaPor.toLowerCase());
                    }
                    return true;
                })
                .filter(p -> {
                    if (color != null && !color.isBlank()) {
                        return p.getColor() != null && p.getColor().toLowerCase().contains(color.toLowerCase());
                    }
                    return true;
                })
                .filter(p -> {
                    if (marca != null && !marca.isBlank()) {
                        return p.getMarca() != null && p.getMarca().toLowerCase().contains(marca.toLowerCase());
                    }
                    return true;
                })
                .filter(p -> {
                    if (talla != null && !talla.isBlank()) {
                        return p.getTalla() != null && p.getTalla().equalsIgnoreCase(talla.trim());
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
                .sorted((p1, p2) -> {
                    int t1 = tipoOrden(p1);
                    int t2 = tipoOrden(p2);
                    int cmpTipo = Integer.compare(t1, t2);
                    if (cmpTipo != 0) return cmpTipo;

                    String c1 = categoriaDe(p1);
                    String c2 = categoriaDe(p2);
                    int cmpCat = c1.compareToIgnoreCase(c2);
                    if (cmpCat != 0) return cmpCat;

                    String n1 = p1.getNombre() != null ? p1.getNombre() : "";
                    String n2 = p2.getNombre() != null ? p2.getNombre() : "";
                    return n1.compareToIgnoreCase(n2);
                })
                .toList();
    }

    // Orden de tipos: superior (0), inferior (1), calzado (2), resto (99)
    private int tipoOrden(Prenda p) {
        if (p instanceof PrendaSuperior) return 0;
        if (p instanceof PrendaInferior) return 1;
        if (p instanceof PrendaCalzado)  return 2;
        return 99;
    }

    // Devuelve la categoría como String (según el tipo de prenda)
    private String categoriaDe(Prenda p) {
        if (p instanceof PrendaSuperior sup && sup.getCategoria() != null) {
            return sup.getCategoria().name();
        }
        if (p instanceof PrendaInferior inf && inf.getCategoriaInferior() != null) {
            return inf.getCategoriaInferior().name();
        }
        if (p instanceof PrendaCalzado cal && cal.getCategoria() != null) {
            return cal.getCategoria().name();
        }
        return "";
    }

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

    @Override
    public void validarDatosNuevaPrenda(String nombre,
                                        String tipoPrenda,
                                        CategoriaSuperior catSuperior,
                                        CategoriaInferior catInferior,
                                        CategoriaCalzado catCalzado,
                                        String marca,
                                        String talla,
                                        String color) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la prenda es obligatorio.");
        }
        if (tipoPrenda == null || tipoPrenda.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un tipo de prenda.");
        }

        // Validar categoría según tipo
        switch (tipoPrenda) {
            case "superior" -> {
                if (catSuperior == null) {
                    throw new IllegalArgumentException("Debes seleccionar una categoría para la prenda superior.");
                }
            }
            case "inferior" -> {
                if (catInferior == null) {
                    throw new IllegalArgumentException("Debes seleccionar una categoría para la prenda inferior.");
                }
            }
            case "calzado" -> {
                if (catCalzado == null) {
                    throw new IllegalArgumentException("Debes seleccionar una categoría para el calzado.");
                }
            }
            default -> throw new IllegalArgumentException("Tipo de prenda no válido.");
        }

        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("La marca es obligatoria.");
        }
        if (talla == null || talla.trim().isEmpty()) {
            throw new IllegalArgumentException("La talla es obligatoria.");
        }

        // Validar que la talla sea una de las permitidas según el tipo de prenda
        String tallaNormalizada = talla.trim();
        switch (tipoPrenda) {
            case "superior" -> {
                if (!TallaSuperior.esValida(tallaNormalizada)) {
                    throw new IllegalArgumentException("La talla seleccionada no es válida para prendas superiores.");
                }
            }
            case "inferior" -> {
                if (!TallaInferior.esValida(tallaNormalizada)) {
                    throw new IllegalArgumentException("La talla seleccionada no es válida para prendas inferiores.");
                }
            }
            case "calzado" -> {
                if (!TallaCalzado.esValida(tallaNormalizada)) {
                    throw new IllegalArgumentException("La talla seleccionada no es válida para calzado.");
                }
            }
            default -> {
                // ya controlado antes, pero por seguridad
                throw new IllegalArgumentException("Tipo de prenda no válido.");
            }
        }

        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("El color es obligatorio.");
        }
    }
}
