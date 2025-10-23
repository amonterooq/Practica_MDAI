package com.nada.nada.data.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Conjunto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @ManyToOne
    private Usuario usuario;

    public Conjunto() {

    }
    public Conjunto(Long id, String nombre, Usuario usuario) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conjunto)) return false;
        Conjunto that = (Conjunto) o;
        return id != null && that.id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
