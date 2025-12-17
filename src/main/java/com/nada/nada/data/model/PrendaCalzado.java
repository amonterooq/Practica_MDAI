package com.nada.nada.data.model;

import com.nada.nada.data.model.enums.CategoriaCalzado;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa calzado (zapatillas, botas, sandalias, etc.).
 * Extiende de Prenda y añade la categoría específica para calzado.
 */
@Entity
@DiscriminatorValue("CALZADO")
public class PrendaCalzado extends Prenda {

    /** Categoría del calzado (zapatillas, botas, sandalias, etc.) */
    private CategoriaCalzado categoria;

    /** Conjuntos que incluyen este calzado */
    @OneToMany(mappedBy = "prendaCalzado", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Conjunto> conjuntos = new ArrayList<>();

    /**
     * Constructor por defecto requerido por JPA.
     */
    public PrendaCalzado() {
    }

    /**
     * Constructor con atributos específicos de calzado.
     *
     * @param conjuntos lista de conjuntos asociados
     * @param categoria categoría del calzado
     */
    public PrendaCalzado(List<Conjunto> conjuntos, CategoriaCalzado categoria) {
        super();
        this.conjuntos = conjuntos;
        this.categoria = categoria;
    }

    /**
     * Constructor completo con todos los atributos.
     */
    public PrendaCalzado(String nombre, String color, String marca, Usuario usuario,
                         String talla, String urlImagen, List<Conjunto> conjuntos,
                         CategoriaCalzado categoria) {
        super(nombre, color, marca, usuario, talla, urlImagen);
        this.conjuntos = conjuntos;
        this.categoria = categoria;
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    public CategoriaCalzado getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaCalzado categoria) {
        this.categoria = categoria;
    }

    public List<Conjunto> getConjuntos() {
        return conjuntos;
    }

    public void setConjuntos(List<Conjunto> conjuntos) {
        this.conjuntos = conjuntos;
    }
}
