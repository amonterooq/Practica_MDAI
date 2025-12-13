package com.nada.nada.data.model;

import com.nada.nada.data.model.enums.CategoriaInferior;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("INFERIOR")
public class PrendaInferior extends Prenda {

    private CategoriaInferior categoriaInferior;

    @OneToMany(mappedBy = "prendaInferior", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Conjunto> conjuntos = new ArrayList<>();

    public PrendaInferior() {}

    public PrendaInferior(CategoriaInferior categoriaInferior, List<Conjunto> conjuntos) {
        this.categoriaInferior = categoriaInferior;
        this.conjuntos = conjuntos;
    }

    public PrendaInferior(String nombre, String color, String marca, Usuario usuario, String talla, String urlImagen, CategoriaInferior categoriaInferior, List<Conjunto> conjuntos) {
        super(nombre, color, marca, usuario, talla, urlImagen);
        this.categoriaInferior = categoriaInferior;
        this.conjuntos = conjuntos;
    }

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

    public void setCategoria(CategoriaInferior categoriaInferior) {

    }
}
