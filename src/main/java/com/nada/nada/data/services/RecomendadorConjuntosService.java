package com.nada.nada.data.services;

import com.nada.nada.dto.chat.RecomendacionConjuntoRequestDto;
import com.nada.nada.dto.chat.RecomendacionConjuntoResponseDto;

public interface RecomendadorConjuntosService {

    RecomendacionConjuntoResponseDto recomendarConjunto(Long usuarioId,
                                                         Long superiorFijoId,
                                                         Long inferiorFijoId,
                                                         Long calzadoFijoId);

    // Nuevo método que recibe todas las preferencias del chat
    RecomendacionConjuntoResponseDto recomendarConjunto(Long usuarioId,
                                                         RecomendacionConjuntoRequestDto request);
}
