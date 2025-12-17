package com.nada.nada.dto.chat;

/**
 * DTO para representar una prenda en las recomendaciones.
 * Contiene la información básica necesaria para mostrar la prenda al usuario.
 */
public class RecomendacionPrendaDto {

    /** ID de la prenda */
    private Long id;

    /** Tipo de prenda: SUPERIOR, INFERIOR, CALZADO */
    private String tipo;

    /** Nombre de la prenda */
    private String nombre;

    /** Marca de la prenda */
    private String marca;

    /** Color de la prenda */
    private String color;

    /** Categoría específica de la prenda */
    private String categoria;

    /** URL de la imagen de la prenda */
    private String imagenUrl;

    /**
     * Constructor por defecto.
     */
    public RecomendacionPrendaDto() {
    }

    /**
     * Constructor con todos los campos.
     *
     * @param id ID de la prenda
     * @param tipo tipo de prenda
     * @param nombre nombre de la prenda
     * @param marca marca de la prenda
     * @param color color de la prenda
     * @param categoria categoría de la prenda
     * @param imagenUrl URL de la imagen
     */
    public RecomendacionPrendaDto(Long id, String tipo, String nombre, String marca,
                                   String color, String categoria, String imagenUrl) {
        this.id = id;
        this.tipo = tipo;
        this.nombre = nombre;
        this.marca = marca;
        this.color = color;
        this.categoria = categoria;
        this.imagenUrl = imagenUrl;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
