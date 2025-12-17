package com.nada.nada.data.services;

import com.nada.nada.dto.chat.RecomendacionConjuntoRequestDto;
import com.nada.nada.dto.chat.RecomendacionConjuntoResponseDto;

/**
 * Interfaz de servicio para la generación de recomendaciones de conjuntos.
 * Proporciona algoritmos inteligentes para sugerir combinaciones de prendas.
 */
public interface RecomendadorConjuntosService {

    /**
     * Genera una recomendación de conjunto con prendas fijas opcionales.
     * Método simplificado para compatibilidad.
     *
     * @param usuarioId ID del usuario
     * @param superiorFijoId ID de prenda superior fija (opcional)
     * @param inferiorFijoId ID de prenda inferior fija (opcional)
     * @param calzadoFijoId ID de calzado fijo (opcional)
     * @return DTO con la recomendación generada
     */
    RecomendacionConjuntoResponseDto recomendarConjunto(Long usuarioId,
                                                         Long superiorFijoId,
                                                         Long inferiorFijoId,
                                                         Long calzadoFijoId);

    /**
     * Genera una recomendación de conjunto basada en preferencias completas.
     * Soporta múltiples modos: SORPRESA, COLOR, MARCA, TIEMPO, SIN_REPETIR, etc.
     *
     * @param usuarioId ID del usuario
     * @param request DTO con todas las preferencias de recomendación
     * @return DTO con la recomendación generada
     */
    RecomendacionConjuntoResponseDto recomendarConjunto(Long usuarioId,
                                                         RecomendacionConjuntoRequestDto request);
}
