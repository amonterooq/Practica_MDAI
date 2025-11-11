package com.nada.nada.data.services;

import com.nada.nada.data.model.*;

import java.util.List;
import java.util.Optional;

public interface PrendaService {

    // Operaciones CRUD básicas
    Prenda guardarPrenda(Prenda prenda);
    Optional<Prenda> buscarPrendaPorId(Long id);
    List<Prenda> buscarTodasPrendas();
    boolean borrarPrenda(Long id);
    Prenda actualizarPrenda(Prenda prenda);

    // Búsquedas por usuario y características
    List<Prenda> buscarPrendasPorUsuarioId(Long usuarioId);
    List<Prenda> buscarPrendasPorColor(Long usuarioId, String color);
    List<Prenda> buscarPrendasPorMarca(Long usuarioId, String marca);
    List<Prenda> buscarPrendasPorTalla(Long usuarioId, String talla);

    // Búsquedas específicas por tipo y categoría
    List<PrendaSuperior> buscarPrendasSuperioresPorCategoria(Long usuarioId, CategoriaSuperior categoria);
    List<PrendaInferior> buscarPrendasInferioresPorCategoria(Long usuarioId, CategoriaInferior categoria);
    List<PrendaCalzado> buscarPrendasCalzadoPorCategoria(Long usuarioId, CategoriaCalzado categoria);

    // Operaciones específicas por tipo
    PrendaSuperior guardarPrendaSuperior(PrendaSuperior prenda);
    PrendaInferior guardarPrendaInferior(PrendaInferior prenda);
    PrendaCalzado guardarPrendaCalzado(PrendaCalzado prenda);

    // Contar prendas de un usuario
    long contarPrendasPorUsuario(Long usuarioId);

    // Verificar existencia
    boolean existePrendaPorId(Long id);
}
