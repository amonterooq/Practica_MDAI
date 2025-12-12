package com.nada.nada.data.services;

import com.nada.nada.data.model.Conjunto;

import java.util.List;
import java.util.Optional;

public interface ConjuntoService {
    Conjunto guardarConjunto(Conjunto conjunto);
    Optional<Conjunto> buscarConjuntoPorId(Long id);
    List<Conjunto> buscarConjuntosPorUsuarioId(Long usuarioId);
    boolean borrarConjunto(Long id);
    List<Conjunto> buscarConjuntosConPrenda(Long prendaId);
}
