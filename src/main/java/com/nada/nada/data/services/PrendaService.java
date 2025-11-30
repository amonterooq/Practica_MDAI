package com.nada.nada.data.services;

import com.nada.nada.data.model.*;

import java.util.List;
import java.util.Optional;

public interface PrendaService {

    PrendaSuperior guardarPrendaSuperior(PrendaSuperior prenda);
    PrendaInferior guardarPrendaInferior(PrendaInferior prenda);
    PrendaCalzado guardarPrendaCalzado(PrendaCalzado prenda);
    Optional<Prenda> buscarPrendaPorId(Long id);
    boolean borrarPrenda(Long id);
    Prenda actualizarPrenda(Prenda prenda);
    List<Prenda> buscarPrendasPorUsuarioId(Long usuarioId);
    long contarPrendasPorUsuario(Long usuarioId);

    // Búsqueda combinada por usuario con filtros opcionales
    List<Prenda> buscarPrendasFiltradas(Long usuarioId,
                                        String nombreEmpiezaPor,
                                        String tipoPrenda,
                                        String categoria,
                                        String color,
                                        String marca,
                                        String talla);

    void validarDatosNuevaPrenda(String nombre,
                                 String tipoPrenda,
                                 CategoriaSuperior catSuperior,
                                 CategoriaInferior catInferior,
                                 CategoriaCalzado catCalzado,
                                 String marca,
                                 String talla,
                                 String color);
}
