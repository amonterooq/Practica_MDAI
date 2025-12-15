package com.nada.nada.dto.chat;

import java.util.List;

public class RecomendacionConjuntoResponseDto {

    private String mensaje;
    private RecomendacionPrendaDto superior;
    private RecomendacionPrendaDto inferior;
    private RecomendacionPrendaDto calzado;
    private List<String> faltantes;

    public RecomendacionConjuntoResponseDto() {
    }

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

