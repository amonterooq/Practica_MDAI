package com.nada.nada.data.services;

import com.nada.nada.data.model.Conjunto;

import java.util.List;
import java.util.Optional;

public interface ConjuntoService {
    Conjunto guardarConjunto(Conjunto conjunto);

    Optional<Conjunto> buscarConjuntoPorId(Long id);

    List<Conjunto> buscarConjuntosPorUsuarioId(Long usuarioId);

    Conjunto buscarConjuntoPorNombre(String nombre);

    boolean borrarConjunto(Long id);

    Conjunto actualizarConjunto(Conjunto conjunto);

    long contarConjuntosPorUsuario(Long usuarioId);
}
