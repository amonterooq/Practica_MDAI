package com.nada.nada.dto.chat;

/**
 * DTO para las solicitudes de mensajes del chat.
 * Contiene el mensaje de texto enviado por el usuario.
 */
public class ChatRequestDto {

    /** Mensaje de texto del usuario */
    private String message;

    /**
     * Constructor por defecto.
     */
    public ChatRequestDto() {
    }

    /**
     * Constructor con mensaje.
     *
     * @param message mensaje del usuario
     */
    public ChatRequestDto(String message) {
        this.message = message;
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
