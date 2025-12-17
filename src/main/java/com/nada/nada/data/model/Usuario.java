package com.nada.nada.data.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa a un usuario del sistema.
 * Un usuario puede tener múltiples prendas, conjuntos y posts.
 * También puede dar likes a publicaciones de otros usuarios.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    /** Identificador único del usuario */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de usuario para login */
    private String username;

    /** Contraseña del usuario (sin encriptar en esta versión) */
    private String password;

    /** Email del usuario */
    private String email;

    /** Lista de prendas que pertenecen al usuario */
    @OneToMany(mappedBy = "usuario", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Prenda> prendas = new ArrayList<>();

    /** Lista de conjuntos creados por el usuario */
    @OneToMany(mappedBy = "usuario", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Conjunto> conjuntos = new ArrayList<>();

    /** Lista de publicaciones del usuario */
    @OneToMany(mappedBy = "usuario", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    /** Lista de posts a los que el usuario ha dado like */
    @ManyToMany
    private List<Post> postsLikeados = new ArrayList<>();

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Usuario() {
    }

    /**
     * Constructor con los campos básicos del usuario.
     *
     * @param username nombre de usuario
     * @param password contraseña
     * @param email email del usuario
     */
    public Usuario(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Prenda> getPrendas() {
        return prendas;
    }

    public void setPrendas(List<Prenda> prendas) {
        this.prendas = prendas;
    }

    public List<Conjunto> getConjuntos() {
        return conjuntos;
    }

    public void setConjuntos(List<Conjunto> conjuntos) {
        this.conjuntos = conjuntos;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Post> getPostsLikeados() {
        return postsLikeados;
    }

    public void setPostsLikeados(List<Post> postsLikeados) {
        this.postsLikeados = postsLikeados;
    }

    // =====================================================================
    // MÉTODOS DE NEGOCIO
    // =====================================================================

    /**
     * Añade una prenda a la colección del usuario.
     *
     * @param prenda prenda a añadir
     * @return true si se añadió correctamente
     */
    public boolean addPrenda(Prenda prenda) {
        return this.prendas.add(prenda);
    }

    /**
     * Añade un conjunto a la colección del usuario.
     *
     * @param conjunto conjunto a añadir
     * @return true si se añadió correctamente
     */
    public boolean addConjunto(Conjunto conjunto) {
        return this.conjuntos.add(conjunto);
    }

    /**
     * Añade un post a la colección del usuario.
     *
     * @param post post a añadir
     * @return true si se añadió correctamente
     */
    public boolean addPost(Post post) {
        return this.posts.add(post);
    }

    /**
     * Añade un like a un post (gestiona la relación bidireccional).
     *
     * @param post post al que dar like
     * @return true si se añadió el like, false si ya existía
     */
    public boolean likePost(Post post) {
        if (!this.postsLikeados.contains(post)) {
            this.postsLikeados.add(post);
            post.getUsuariosQueDieronLike().add(this);
            return true;
        }
        return false;
    }

    /**
     * Quita un like de un post (gestiona la relación bidireccional).
     *
     * @param post post del que quitar el like
     * @return true si se quitó el like, false si no existía
     */
    public boolean unlikePost(Post post) {
        if (this.postsLikeados.remove(post)) {
            post.getUsuariosQueDieronLike().remove(this);
            return true;
        }
        return false;
    }

    // =====================================================================
    // EQUALS, HASHCODE Y TOSTRING
    // =====================================================================

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
