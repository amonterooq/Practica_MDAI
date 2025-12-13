package com.nada.nada.data.repository;

import com.nada.nada.data.model.enums.CategoriaCalzado;
import com.nada.nada.data.model.PrendaCalzado;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrendaCalzadoRepository extends CrudRepository<PrendaCalzado, Long> {
    public List<PrendaCalzado> findAllByUsuario_IdAndCategoria(Long usuarioId, CategoriaCalzado categoria);
}
