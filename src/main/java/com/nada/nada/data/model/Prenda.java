package com.nada.nada.data.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CascadeType;

@Entity
public class Prenda {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private String color;
    private String marca;

    @ManyToOne
    private Usuario usuario;


    public Prenda() {
    }

    public Prenda(String nombre, String color, String marca, Usuario usuario) {
        this.nombre = nombre;
        this.color = color;
        this.marca = marca;
        this.usuario = usuario;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object o) {
        Object obj = (Prenda) o;
        return this.id == ((Prenda) obj).getId();
    }

}
