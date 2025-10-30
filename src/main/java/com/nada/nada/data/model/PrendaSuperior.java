package com.nada.nada.data.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("SUPERIOR")
public class PrendaSuperior extends Prenda {

    private CategoriaSuperior categoria;
    private Manga manga;

    @OneToMany(mappedBy = "prendaSuperior", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Conjunto> conjuntos = new ArrayList<>();

    public PrendaSuperior() {
    }

    public PrendaSuperior(CategoriaSuperior categoria, List<Conjunto> conjuntos, Manga manga) {
        this.categoria = categoria;
        this.conjuntos = conjuntos;
        this.manga = manga;
    }

    public PrendaSuperior(String nombre, String color, String marca, Usuario usuario, String urlImagen, CategoriaSuperior categoria, List<Conjunto> conjuntos, Manga manga) {
        super(nombre, color, marca, usuario, urlImagen);
        this.categoria = categoria;
        this.conjuntos = conjuntos;
        this.manga = manga;
    }

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
