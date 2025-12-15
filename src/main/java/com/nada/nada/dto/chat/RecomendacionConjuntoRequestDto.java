package com.nada.nada.dto.chat;

public class RecomendacionConjuntoRequestDto {

    private Long superiorFijoId;
    private Long inferiorFijoId;
    private Long calzadoFijoId;

    public RecomendacionConjuntoRequestDto() {
    }

    public Long getSuperiorFijoId() {
        return superiorFijoId;
    }

    public void setSuperiorFijoId(Long superiorFijoId) {
        this.superiorFijoId = superiorFijoId;
    }

    public Long getInferiorFijoId() {
        return inferiorFijoId;
    }

    public void setInferiorFijoId(Long inferiorFijoId) {
        this.inferiorFijoId = inferiorFijoId;
    }

    public Long getCalzadoFijoId() {
        return calzadoFijoId;
    }

    public void setCalzadoFijoId(Long calzadoFijoId) {
        this.calzadoFijoId = calzadoFijoId;
    }
}

