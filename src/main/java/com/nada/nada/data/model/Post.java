package com.nada.nada.data.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa una publicación de un conjunto.
 * Permite a los usuarios compartir sus conjuntos y recibir likes de otros.
 * Tiene una relación bidireccional con Conjunto (uno a uno).
 */
@Entity
public class Post {

    /** Identificador único del post */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuario que creó el post */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Conjunto que se publica en este post */
    @OneToOne
    @JoinColumn(name = "conjunto_id", unique = true)
    private Conjunto conjunto;

    /** Lista de usuarios que han dado like a este post (relación ManyToMany inversa) */
    @ManyToMany(mappedBy = "postsLikeados")
    private List<Usuario> usuariosQueDieronLike = new ArrayList<>();

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Post() {
    }

    /**
     * Constructor con usuario y conjunto.
     *
     * @param usuario usuario autor del post
     * @param conjunto conjunto que se publica
     */
    public Post(Usuario usuario, Conjunto conjunto) {
        this.usuario = usuario;
        this.conjunto = conjunto;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Conjunto getConjunto() {
        return conjunto;
    }

    public void setConjunto(Conjunto conjunto) {
        this.conjunto = conjunto;
    }

    public List<Usuario> getUsuariosQueDieronLike() {
        return usuariosQueDieronLike;
    }

    public void setUsuariosQueDieronLike(List<Usuario> usuariosQueDieronLike) {
        this.usuariosQueDieronLike = usuariosQueDieronLike;
    }

    // =====================================================================
    // EQUALS, HASHCODE Y TOSTRING
    // =====================================================================

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", usuarioId=" + (usuario != null ? usuario.getId() : null) +
                ", conjuntoId=" + (conjunto != null ? conjunto.getId() : null) +
                ", likes=" + usuariosQueDieronLike.size() +
                '}';
    }
}
