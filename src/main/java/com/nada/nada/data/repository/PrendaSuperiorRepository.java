package com.nada.nada.data.repository;

import com.nada.nada.data.model.CategoriaSuperior;
import com.nada.nada.data.model.PrendaSuperior;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrendaSuperiorRepository extends CrudRepository<PrendaSuperior, Long> {
    public List<PrendaSuperior> findAllByUsuario_IdAndCategoria(Long usuarioId, CategoriaSuperior categoria);
}
