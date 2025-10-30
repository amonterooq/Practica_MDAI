package com.nada.nada.data.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CALZADO")
public class PrendaCalzado extends Prenda{

    private CategoriaCalzado categoria;

    @OneToMany(mappedBy = "prendaCalzado")
    private List<Conjunto> conjuntos = new ArrayList<>();

    public PrendaCalzado() {

    }

    public PrendaCalzado(List<Conjunto> conjuntos, CategoriaCalzado categoria) {
        super();
        this.conjuntos = conjuntos;
        this.categoria = categoria;
    }

    public PrendaCalzado(String nombre, String color, String marca, Usuario usuario, String urlImagen, List<Conjunto> conjuntos, CategoriaCalzado categoria) {
        super(nombre, color, marca, usuario, urlImagen);
        this.conjuntos = conjuntos;
        this.categoria = categoria;
    }


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
