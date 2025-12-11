package com.nada.nada.data.repository;

import com.nada.nada.data.model.Prenda;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrendaRepository extends CrudRepository<Prenda, Long> {
    public Prenda findByNombre(String nombre);
    public Prenda findById(long id);
    public long countByUsuario_Id(long usuarioId);

    public List<Prenda> findAllByUsuario_IdAndColorContainingIgnoreCase(Long usuarioId, String color);
    public List<Prenda> findAllByUsuario_IdAndMarcaContainingIgnoreCase(Long usuarioId, String marca);
    public List<Prenda> findAllByUsuario_IdAndTallaIgnoreCase(Long usuarioId, String talla);
    
    @Query("SELECT DISTINCT p.marca FROM Prenda p WHERE p.usuario.id = :usuarioId ORDER BY p.marca")
    List<String> findDistinctMarcasByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT DISTINCT p.color FROM Prenda p WHERE p.usuario.id = :usuarioId ORDER BY p.color")
    List<String> findDistinctColoresByUsuarioId(@Param("usuarioId") Long usuarioId);
}
