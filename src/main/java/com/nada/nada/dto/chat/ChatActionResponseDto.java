package com.nada.nada.dto.chat;

/**
 * DTO para respuestas de acciones del chat (guardar conjunto, etc.).
 * Indica si la operación fue exitosa y proporciona un mensaje.
 */
public class ChatActionResponseDto {

    /** Indica si la operación fue exitosa */
    private boolean ok;

    /** Mensaje descriptivo del resultado */
    private String mensaje;

    /** ID del conjunto creado (si aplica) */
    private Long conjuntoId;

    /**
     * Constructor por defecto.
     */
    public ChatActionResponseDto() {
    }

    /**
     * Constructor con resultado y mensaje.
     *
     * @param ok indica si la operación fue exitosa
     * @param mensaje mensaje descriptivo
     */
    public ChatActionResponseDto(boolean ok, String mensaje) {
        this.ok = ok;
        this.mensaje = mensaje;
    }

    /**
     * Constructor completo con ID de conjunto.
     *
     * @param ok indica si la operación fue exitosa
     * @param mensaje mensaje descriptivo
     * @param conjuntoId ID del conjunto creado
     */
    public ChatActionResponseDto(boolean ok, String mensaje, Long conjuntoId) {
        this.ok = ok;
        this.mensaje = mensaje;
        this.conjuntoId = conjuntoId;
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Long getConjuntoId() {
        return conjuntoId;
    }

    public void setConjuntoId(Long conjuntoId) {
        this.conjuntoId = conjuntoId;
    }
}
