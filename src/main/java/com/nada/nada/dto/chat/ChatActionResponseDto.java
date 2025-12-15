package com.nada.nada.dto.chat;

public class ChatActionResponseDto {

    private boolean ok;
    private String mensaje;
    private Long conjuntoId;

    public ChatActionResponseDto() {
    }

    public ChatActionResponseDto(boolean ok, String mensaje) {
        this.ok = ok;
        this.mensaje = mensaje;
    }

    public ChatActionResponseDto(boolean ok, String mensaje, Long conjuntoId) {
        this.ok = ok;
        this.mensaje = mensaje;
        this.conjuntoId = conjuntoId;
    }

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

