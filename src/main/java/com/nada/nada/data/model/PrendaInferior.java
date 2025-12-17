package com.nada.nada.data.model;

import com.nada.nada.data.model.enums.CategoriaInferior;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una prenda inferior (pantalón, falda, shorts, etc.).
 * Extiende de Prenda y añade la categoría específica para prendas inferiores.
 */
@Entity
@DiscriminatorValue("INFERIOR")
public class PrendaInferior extends Prenda {

    /** Categoría de la prenda inferior (pantalón, falda, shorts, etc.) */
    private CategoriaInferior categoriaInferior;

    /** Conjuntos que incluyen esta prenda inferior */
    @OneToMany(mappedBy = "prendaInferior", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Conjunto> conjuntos = new ArrayList<>();

    /**
     * Constructor por defecto requerido por JPA.
     */
    public PrendaInferior() {
    }

    /**
     * Constructor con atributos específicos de prenda inferior.
     *
     * @param categoriaInferior categoría de la prenda
     * @param conjuntos lista de conjuntos asociados
     */
    public PrendaInferior(CategoriaInferior categoriaInferior, List<Conjunto> conjuntos) {
        this.categoriaInferior = categoriaInferior;
        this.conjuntos = conjuntos;
    }

    /**
     * Constructor completo con todos los atributos.
     */
    public PrendaInferior(String nombre, String color, String marca, Usuario usuario,
                          String talla, String urlImagen, CategoriaInferior categoriaInferior,
                          List<Conjunto> conjuntos) {
        super(nombre, color, marca, usuario, talla, urlImagen);
        this.categoriaInferior = categoriaInferior;
        this.conjuntos = conjuntos;
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    public CategoriaInferior getCategoriaInferior() {
        return categoriaInferior;
    }

    public void setCategoriaInferior(CategoriaInferior categoriaInferior) {
        this.categoriaInferior = categoriaInferior;
    }

    public List<Conjunto> getConjuntos() {
        return conjuntos;
    }

    public void setConjuntos(List<Conjunto> conjuntos) {
        this.conjuntos = conjuntos;
    }

    /**
     * Método alternativo para establecer la categoría (compatibilidad).
     * @deprecated Usar setCategoriaInferior en su lugar
     */
    @Deprecated
    public void setCategoria(CategoriaInferior categoriaInferior) {
        this.categoriaInferior = categoriaInferior;
    }
}
