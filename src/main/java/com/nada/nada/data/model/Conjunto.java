package com.nada.nada.data.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Conjunto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(length = 256)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    private PrendaSuperior prendaSuperior;

    @ManyToOne
    private PrendaInferior prendaInferior;

    @ManyToOne
    private PrendaCalzado prendaCalzado;

    public Conjunto() {

    }

    public Conjunto(String nombre, Usuario usuario, String descripcion, PrendaSuperior prendaSuperior, PrendaInferior prendaInferior, PrendaCalzado prendaCalzado) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.descripcion = descripcion;
        this.prendaSuperior = prendaSuperior;
        this.prendaInferior = prendaInferior;
        this.prendaCalzado = prendaCalzado;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public PrendaSuperior getPrendaSuperior() {
        return prendaSuperior;
    }

    public void setPrendaSuperior(PrendaSuperior prendaSuperior) {
        this.prendaSuperior = prendaSuperior;
    }

    public PrendaInferior getPrendaInferior() {
        return prendaInferior;
    }

    public void setPrendaInferior(PrendaInferior prendaInferior) {
        this.prendaInferior = prendaInferior;
    }

    public PrendaCalzado getPrendaCalzado() {
        return prendaCalzado;
    }

    public void setPrendaCalzado(PrendaCalzado prendaCalzado) {
        this.prendaCalzado = prendaCalzado;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Conjunto conjunto)) return false;
        return Objects.equals(id, conjunto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Conjunto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", usuario=" + usuario +
                ", prendaSuperior=" + prendaSuperior +
                ", prendaInferior=" + prendaInferior +
                ", prendaCalzado=" + prendaCalzado +
                '}';
    }
}
