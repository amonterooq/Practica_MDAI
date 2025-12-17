package com.nada.nada.dto.chat;

/**
 * DTO para solicitar guardar un conjunto desde el chat.
 * Contiene los IDs de las prendas seleccionadas y metadatos del conjunto.
 */
public class GuardarConjuntoChatRequestDto {

    /** ID de la prenda superior */
    private Long prendaSuperiorId;

    /** ID de la prenda inferior */
    private Long prendaInferiorId;

    /** ID del calzado */
    private Long prendaCalzadoId;

    /** Nombre del conjunto */
    private String nombre;

    /** Notas o descripción del conjunto */
    private String notas;

    /**
     * Constructor por defecto.
     */
    public GuardarConjuntoChatRequestDto() {
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    public Long getPrendaSuperiorId() {
        return prendaSuperiorId;
    }

    public void setPrendaSuperiorId(Long prendaSuperiorId) {
        this.prendaSuperiorId = prendaSuperiorId;
    }

    public Long getPrendaInferiorId() {
        return prendaInferiorId;
    }

    public void setPrendaInferiorId(Long prendaInferiorId) {
        this.prendaInferiorId = prendaInferiorId;
    }

    public Long getPrendaCalzadoId() {
        return prendaCalzadoId;
    }

    public void setPrendaCalzadoId(Long prendaCalzadoId) {
        this.prendaCalzadoId = prendaCalzadoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
