package com.nada.nada.data.services;

import com.nada.nada.data.model.*;
import com.nada.nada.data.model.enums.CategoriaCalzado;
import com.nada.nada.data.model.enums.CategoriaInferior;
import com.nada.nada.data.model.enums.CategoriaSuperior;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para la gestión de prendas de ropa.
 * Define las operaciones CRUD y de búsqueda para prendas.
 */
public interface PrendaService {

    /**
     * Guarda una nueva prenda superior.
     *
     * @param prenda prenda superior a guardar
     * @return la prenda guardada con su ID asignado
     */
    PrendaSuperior guardarPrendaSuperior(PrendaSuperior prenda);

    /**
     * Guarda una nueva prenda inferior.
     *
     * @param prenda prenda inferior a guardar
     * @return la prenda guardada con su ID asignado
     */
    PrendaInferior guardarPrendaInferior(PrendaInferior prenda);

    /**
     * Guarda un nuevo calzado.
     *
     * @param prenda calzado a guardar
     * @return el calzado guardado con su ID asignado
     */
    PrendaCalzado guardarPrendaCalzado(PrendaCalzado prenda);

    /**
     * Busca una prenda por su ID.
     *
     * @param id ID de la prenda
     * @return Optional con la prenda si existe
     */
    Optional<Prenda> buscarPrendaPorId(Long id);

    /**
     * Elimina una prenda y su imagen asociada.
     *
     * @param id ID de la prenda a eliminar
     * @return true si se eliminó correctamente
     */
    boolean borrarPrenda(Long id);

    /**
     * Actualiza los datos de una prenda existente.
     *
     * @param prenda prenda con los datos actualizados
     * @return la prenda actualizada
     */
    Prenda actualizarPrenda(Prenda prenda);

    /**
     * Obtiene todas las prendas de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de prendas del usuario
     */
    List<Prenda> buscarPrendasPorUsuarioId(Long usuarioId);

    /**
     * Cuenta el número total de prendas de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return número de prendas
     */
    long contarPrendasPorUsuario(Long usuarioId);

    /**
     * Busca prendas de un usuario aplicando múltiples filtros opcionales.
     *
     * @param usuarioId ID del usuario
     * @param nombreEmpiezaPor filtro por nombre (contiene)
     * @param tipoPrenda filtro por tipo (superior, inferior, calzado)
     * @param categoria filtro por categoría específica
     * @param color filtro por color
     * @param marca filtro por marca
     * @param talla filtro por talla
     * @return lista de prendas que cumplen los filtros
     */
    List<Prenda> buscarPrendasFiltradas(Long usuarioId,
                                        String nombreEmpiezaPor,
                                        String tipoPrenda,
                                        String categoria,
                                        String color,
                                        String marca,
                                        String talla);

    /**
     * Valida los datos para crear una nueva prenda.
     *
     * @throws IllegalArgumentException si algún dato es inválido
     */
    void validarDatosNuevaPrenda(String nombre,
                                 String tipoPrenda,
                                 CategoriaSuperior catSuperior,
                                 CategoriaInferior catInferior,
                                 CategoriaCalzado catCalzado,
                                 String marca,
                                 String talla,
                                 String color);

    /**
     * Normaliza una marca introducida por el usuario.
     * Si coincide con una marca predefinida, devuelve el nombre estándar.
     *
     * @param marcaIntroducida marca escrita por el usuario
     * @return marca normalizada
     */
    String normalizarMarca(String marcaIntroducida);

    /**
     * Obtiene las marcas únicas de las prendas de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de marcas únicas
     */
    List<String> obtenerMarcasDelUsuario(Long usuarioId);

    /**
     * Obtiene los colores únicos de las prendas de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de colores únicos
     */
    List<String> obtenerColoresDelUsuario(Long usuarioId);
}
