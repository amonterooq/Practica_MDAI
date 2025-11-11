package com.nada.nada.data.services;

import com.nada.nada.data.model.*;
import com.nada.nada.data.repository.PrendaCalzadoRepository;
import com.nada.nada.data.repository.PrendaInferiorRepository;
import com.nada.nada.data.repository.PrendaRepository;
import com.nada.nada.data.repository.PrendaSuperiorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PrendaServiceImpl implements PrendaService {
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
            throw new IllegalArgumentException("La prenda no puede ser nula");
        }
        return prendaRepository.save(prenda);
    }

    @Override
    public Optional<Prenda> buscarPrendaPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return Optional.ofNullable(prendaRepository.findById(id.longValue()));
    }

    @Override
    public List<Prenda> buscarTodasPrendas() {
        List<Prenda> prendas = new ArrayList<>();
        prendaRepository.findAll().forEach(prendas::add);
        return prendas;
    }

    @Override
    public boolean borrarPrenda(Long id) {
        if (id == null || !prendaRepository.existsById(id)) {
            return false;
        }
        prendaRepository.deleteById(id);
        return true;
    }

    @Override
    public Prenda actualizarPrenda(Prenda prenda) {
        if (prenda == null || prenda.getId() == null) {
            throw new IllegalArgumentException("La prenda y su ID no pueden ser nulos");
        }
        if (!prendaRepository.existsById(prenda.getId())) {
            throw new IllegalArgumentException("No existe una prenda con el ID: " + prenda.getId());
        }
        return prendaRepository.save(prenda);
    }

    @Override
    public List<Prenda> buscarPrendasPorUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        List<Prenda> prendas = new ArrayList<>();
        prendaRepository.findAll().forEach(prenda -> {
            if (prenda.getUsuario() != null && prenda.getUsuario().getId().equals(usuarioId)) {
                prendas.add(prenda);
            }
        });
        return prendas;
    }

    @Override
    public List<Prenda> buscarPrendasPorColor(Long usuarioId, String color) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("El color no puede ser nulo o vacío");
        }
        return prendaRepository.findAllByUsuario_IdAndColorContainingIgnoreCase(usuarioId, color);
    }

    @Override
    public List<Prenda> buscarPrendasPorMarca(Long usuarioId, String marca) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("La marca no puede ser nula o vacía");
        }
        return prendaRepository.findAllByUsuario_IdAndMarcaContainingIgnoreCase(usuarioId, marca);
    }

    @Override
    public List<Prenda> buscarPrendasPorTalla(Long usuarioId, String talla) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (talla == null || talla.trim().isEmpty()) {
            throw new IllegalArgumentException("La talla no puede ser nula o vacía");
        }
        return prendaRepository.findAllByUsuario_IdAndTallaIgnoreCase(usuarioId, talla);
    }

    @Override
    public List<PrendaSuperior> buscarPrendasSuperioresPorCategoria(Long usuarioId, CategoriaSuperior categoria) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        return prendaSuperiorRepository.findAllByUsuario_IdAndCategoria(usuarioId, categoria);
    }

    @Override
    public List<PrendaInferior> buscarPrendasInferioresPorCategoria(Long usuarioId, CategoriaInferior categoria) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        return prendaInferiorRepository.findAllByUsuario_IdAndCategoriaInferior(usuarioId, categoria);
    }

    @Override
    public List<PrendaCalzado> buscarPrendasCalzadoPorCategoria(Long usuarioId, CategoriaCalzado categoria) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        return prendaCalzadoRepository.findAllByUsuario_IdAndCategoria(usuarioId, categoria);
    }

    @Override
    public PrendaSuperior guardarPrendaSuperior(PrendaSuperior prenda) {
        if (prenda == null) {
            throw new IllegalArgumentException("La prenda superior no puede ser nula");
        }
        return prendaSuperiorRepository.save(prenda);
    }

    @Override
    public PrendaInferior guardarPrendaInferior(PrendaInferior prenda) {
        if (prenda == null) {
            throw new IllegalArgumentException("La prenda inferior no puede ser nula");
        }
        return prendaInferiorRepository.save(prenda);
    }

    @Override
    public PrendaCalzado guardarPrendaCalzado(PrendaCalzado prenda) {
        if (prenda == null) {
            throw new IllegalArgumentException("El calzado no puede ser nulo");
        }
        return prendaCalzadoRepository.save(prenda);
    }

    @Override
    public long contarPrendasPorUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        return prendaRepository.countByUsuario_Id(usuarioId);
    }

    @Override
    public boolean existePrendaPorId(Long id) {
        if (id == null) {
            return false;
        }
        return prendaRepository.existsById(id);
    }
}
