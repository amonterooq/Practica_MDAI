package com.nada.nada.data.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("INFERIOR")
public class PrendaInferior extends Prenda {

    private CategoriaInferior categoriaInferior;

    @OneToMany(mappedBy = "prendaInferior")
    private List<Conjunto> conjuntos = new ArrayList<>();

    public PrendaInferior() {}

    public PrendaInferior(CategoriaInferior categoriaInferior, List<Conjunto> conjuntos) {
        this.categoriaInferior = categoriaInferior;
        this.conjuntos = conjuntos;
    }

    public PrendaInferior(String nombre, String color, String marca, Usuario usuario, String urlImagen, CategoriaInferior categoriaInferior, List<Conjunto> conjuntos) {
        super(nombre, color, marca, usuario, urlImagen);
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

}
