package com.nada.nada.data.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_prenda", discriminatorType = DiscriminatorType.STRING)
public abstract class Prenda {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String color;
    private String marca;

    //@ManyToOne(fetch = FetchType.LAZY) mirar
    //@JoinColumn(name = "usuario_id") mirar
    private Usuario usuario;


    public Prenda() {
    }

    public Prenda(String nombre, String color, String marca, Usuario usuario) {
        this.nombre = nombre;
        this.color = color;
        this.marca = marca;
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
        if (this == o) return true;
        if (!(o instanceof Prenda)) return false;
        Prenda other = (Prenda) o;
        return id != null && other.id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
