package com.nada.nada.dto.chat;

/**
 * DTO para las respuestas del chat.
 * Contiene la respuesta generada por el chatbot.
 */
public class ChatResponseDto {

    /** Respuesta del chatbot */
    private String reply;

    /**
     * Constructor por defecto.
     */
    public ChatResponseDto() {
    }

    /**
     * Constructor con respuesta.
     *
     * @param reply respuesta del chatbot
     */
    public ChatResponseDto(String reply) {
        this.reply = reply;
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
