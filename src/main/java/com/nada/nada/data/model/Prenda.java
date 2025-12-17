package com.nada.nada.data.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase abstracta base para todas las prendas de ropa.
 * Utiliza herencia JOINED para que cada tipo de prenda tenga su propia tabla.
 * Las subclases son: PrendaSuperior, PrendaInferior, PrendaCalzado.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_prenda", discriminatorType = DiscriminatorType.STRING)
public abstract class Prenda {

    /** Identificador único de la prenda */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /** Nombre descriptivo de la prenda */
    protected String nombre;

    /** Color de la prenda */
    protected String color;

    /** Marca de la prenda */
    protected String marca;

    /** Talla de la prenda */
    protected String talla;

    /** Ruta relativa a la imagen de la prenda */
    protected String dirImagen;

    /** Fecha y hora de creación del registro */
    @Column(name = "created_at", updatable = false)
    protected LocalDateTime createdAt;

    /** Usuario propietario de la prenda */
    @ManyToOne
    protected Usuario usuario;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Prenda() {
    }

    /**
     * Constructor con todos los campos básicos.
     *
     * @param nombre nombre de la prenda
     * @param color color de la prenda
     * @param marca marca de la prenda
     * @param usuario usuario propietario
     * @param talla talla de la prenda
     * @param dirImagen ruta de la imagen
     */
    public Prenda(String nombre, String color, String marca, Usuario usuario, String talla, String dirImagen) {
        this.nombre = nombre;
        this.color = color;
        this.marca = marca;
        this.usuario = usuario;
        this.talla = talla;
        this.dirImagen = dirImagen;
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

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

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getDirImagen() {
        return dirImagen;
    }

    public void setDirImagen(String dirImagen) {
        this.dirImagen = dirImagen;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // =====================================================================
    // CALLBACKS JPA
    // =====================================================================

    /**
     * Callback ejecutado antes de persistir la entidad.
     * Establece la fecha de creación automáticamente.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // =====================================================================
    // EQUALS, HASHCODE Y TOSTRING
    // =====================================================================

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

    @Override
    public String toString() {
        return "Prenda{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", color='" + color + '\'' +
                ", marca='" + marca + '\'' +
                ", talla='" + talla + '\'' +
                ", dirImagen='" + dirImagen + '\'' +
                '}';
    }
}
