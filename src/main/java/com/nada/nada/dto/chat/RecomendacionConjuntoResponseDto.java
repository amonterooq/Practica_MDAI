package com.nada.nada.dto.chat;

import java.util.List;

/**
 * DTO para las respuestas de recomendación de conjuntos.
 * Contiene las prendas recomendadas, un mensaje explicativo y
 * información sobre prendas faltantes.
 */
public class RecomendacionConjuntoResponseDto {

    /** Mensaje explicativo de la recomendación */
    private String mensaje;

    /** Prenda superior recomendada */
    private RecomendacionPrendaDto superior;

    /** Prenda inferior recomendada */
    private RecomendacionPrendaDto inferior;

    /** Calzado recomendado */
    private RecomendacionPrendaDto calzado;

    /** Lista de tipos de prenda que faltan en el armario del usuario */
    private List<String> faltantes;

    /**
     * Constructor por defecto.
     */
    public RecomendacionConjuntoResponseDto() {
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public RecomendacionPrendaDto getSuperior() {
        return superior;
    }

    public void setSuperior(RecomendacionPrendaDto superior) {
        this.superior = superior;
    }

    public RecomendacionPrendaDto getInferior() {
        return inferior;
    }

    public void setInferior(RecomendacionPrendaDto inferior) {
        this.inferior = inferior;
    }

    public RecomendacionPrendaDto getCalzado() {
        return calzado;
    }

    public void setCalzado(RecomendacionPrendaDto calzado) {
        this.calzado = calzado;
    }

    public List<String> getFaltantes() {
        return faltantes;
    }

    public void setFaltantes(List<String> faltantes) {
        this.faltantes = faltantes;
    }
}
