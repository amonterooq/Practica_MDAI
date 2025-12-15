package com.nada.nada.dto.chat;

public class RecomendacionPrendaDto {

    private Long id;
    private String tipo; // SUPERIOR, INFERIOR, CALZADO
    private String nombre;
    private String marca;
    private String color;
    private String categoria;
    private String imagenUrl;

    public RecomendacionPrendaDto() {
    }

    public RecomendacionPrendaDto(Long id, String tipo, String nombre, String marca, String color, String categoria, String imagenUrl) {
        this.id = id;
        this.tipo = tipo;
        this.nombre = nombre;
        this.marca = marca;
        this.color = color;
        this.categoria = categoria;
        this.imagenUrl = imagenUrl;
    }

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
