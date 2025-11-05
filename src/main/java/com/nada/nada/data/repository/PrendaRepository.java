package com.nada.nada.data.repository;

import com.nada.nada.data.model.Prenda;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PrendaRepository extends CrudRepository<Prenda, Long> {
    public Prenda findByNombre(String nombre);
    public Prenda findById(long id);
    public long countByUsuario_Id(long usuarioId);

    public List<Prenda> findAllByUsuario_IdAndColorContainingIgnoreCase(Long usuarioId, String color);
    public List<Prenda> findAllByUsuario_IdAndMarcaContainingIgnoreCase(Long usuarioId, String marca);
    public List<Prenda> findAllByUsuario_IdAndTallaIgnoreCase(Long usuarioId, String talla);
}
