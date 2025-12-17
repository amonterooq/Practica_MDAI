package com.nada.nada.data.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un conjunto de ropa.
 * Un conjunto está compuesto por una prenda superior, una inferior y un calzado.
 * Puede ser publicado como un Post para que otros usuarios lo vean.
 */
@Entity
public class Conjunto {

    /** Identificador único del conjunto */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del conjunto */
    private String nombre;

    /** Descripción opcional del conjunto (máx 256 caracteres) */
    @Column(length = 256)
    private String descripcion;

    /** Fecha y hora de creación */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Usuario propietario del conjunto */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Prenda superior del conjunto (camiseta, jersey, etc.) */
    @ManyToOne
    private PrendaSuperior prendaSuperior;

    /** Prenda inferior del conjunto (pantalón, falda, etc.) */
    @ManyToOne
    private PrendaInferior prendaInferior;

    /** Calzado del conjunto */
    @ManyToOne
    private PrendaCalzado prendaCalzado;

    /** Post asociado si el conjunto está publicado (relación bidireccional) */
    @OneToOne(mappedBy = "conjunto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Post post;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Conjunto() {
    }

    /**
     * Constructor con los campos principales.
     *
     * @param nombre nombre del conjunto
     * @param usuario usuario propietario
     * @param descripcion descripción del conjunto
     * @param prendaSuperior prenda superior
     * @param prendaInferior prenda inferior
     * @param prendaCalzado calzado
     */
    public Conjunto(String nombre, Usuario usuario, String descripcion,
                    PrendaSuperior prendaSuperior, PrendaInferior prendaInferior, PrendaCalzado prendaCalzado) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.descripcion = descripcion;
        this.prendaSuperior = prendaSuperior;
        this.prendaInferior = prendaInferior;
        this.prendaCalzado = prendaCalzado;
        this.post = null;
    }

    /**
     * Constructor completo incluyendo post.
     */
    public Conjunto(String nombre, Usuario usuario, String descripcion,
                    PrendaSuperior prendaSuperior, PrendaInferior prendaInferior,
                    PrendaCalzado prendaCalzado, Post post) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.descripcion = descripcion;
        this.prendaSuperior = prendaSuperior;
        this.prendaInferior = prendaInferior;
        this.prendaCalzado = prendaCalzado;
        this.post = post;
    }

    // =====================================================================
    // CALLBACKS JPA
    // =====================================================================

    /**
     * Callback ejecutado antes de persistir la entidad.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    // =====================================================================
    // EQUALS, HASHCODE Y TOSTRING
    // =====================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conjunto)) return false;
        Conjunto conjunto = (Conjunto) o;
        return id != null && Objects.equals(id, conjunto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Conjunto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
