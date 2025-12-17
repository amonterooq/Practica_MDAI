package com.nada.nada.data.model;

import com.nada.nada.data.model.enums.CategoriaSuperior;
import com.nada.nada.data.model.enums.Manga;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una prenda superior (camiseta, jersey, abrigo, etc.).
 * Extiende de Prenda y añade atributos específicos como categoría y tipo de manga.
 */
@Entity
@DiscriminatorValue("SUPERIOR")
public class PrendaSuperior extends Prenda {

    /** Categoría de la prenda superior (camiseta, jersey, abrigo, etc.) */
    private CategoriaSuperior categoria;

    /** Tipo de manga de la prenda (corta, larga, sin mangas) */
    private Manga manga;

    /** Conjuntos que incluyen esta prenda superior */
    @OneToMany(mappedBy = "prendaSuperior", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Conjunto> conjuntos = new ArrayList<>();

    /**
     * Constructor por defecto requerido por JPA.
     */
    public PrendaSuperior() {
    }

    /**
     * Constructor con atributos específicos de prenda superior.
     *
     * @param categoria categoría de la prenda
     * @param conjuntos lista de conjuntos asociados
     * @param manga tipo de manga
     */
    public PrendaSuperior(CategoriaSuperior categoria, List<Conjunto> conjuntos, Manga manga) {
        this.categoria = categoria;
        this.conjuntos = conjuntos;
        this.manga = manga;
    }

    /**
     * Constructor completo con todos los atributos.
     */
    public PrendaSuperior(String nombre, String color, String marca, Usuario usuario,
                          String talla, String urlImagen, CategoriaSuperior categoria,
                          List<Conjunto> conjuntos, Manga manga) {
        super(nombre, color, marca, usuario, talla, urlImagen);
        this.categoria = categoria;
        this.conjuntos = conjuntos;
        this.manga = manga;
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    public CategoriaSuperior getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaSuperior categoria) {
        this.categoria = categoria;
    }

    public List<Conjunto> getConjuntos() {
        return conjuntos;
    }

    public void setConjuntos(List<Conjunto> conjuntos) {
        this.conjuntos = conjuntos;
    }

    public Manga getManga() {
        return manga;
    }

    public void setManga(Manga manga) {
        this.manga = manga;
    }
}
