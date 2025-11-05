package com.nada.nada.data.repository;

import com.nada.nada.data.model.CategoriaInferior;
import com.nada.nada.data.model.PrendaInferior;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrendaInferiorRepository extends CrudRepository<PrendaInferior, Long> {
    public List<PrendaInferior> findAllByUsuario_IdAndCategoriaInferior(Long usuarioId, CategoriaInferior categoriaInferior);
}
