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

    // Búsqueda combinada por usuario con filtros opcionales
    List<Prenda> buscarPrendasFiltradas(Long usuarioId,
                                        String nombreEmpiezaPor,
                                        String tipoPrenda,
                                        String categoria,
                                        String color,
                                        String marca,
                                        String talla);

    /**
     * Valida los datos introducidos en el formulario de creación de prenda.
     * Si algo obligatorio está vacío o es inválido lanza IllegalArgumentException
     * con un mensaje descriptivo para mostrar al usuario.
     */
    void validarDatosNuevaPrenda(String nombre,
                                 String tipoPrenda,
                                 CategoriaSuperior catSuperior,
                                 CategoriaInferior catInferior,
                                 CategoriaCalzado catCalzado,
                                 String marca,
                                 String talla,
                                 String color);
}
