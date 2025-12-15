package com.nada.nada.dto.chat;

import java.util.List;

public class RecomendacionConjuntoRequestDto {

    private Long superiorFijoId;
    private Long inferiorFijoId;
    private Long calzadoFijoId;

    // Modo de recomendación (SORPRESA, COLOR, MARCA, TIEMPO, OCASION, SIN_REPETIR, COMPLETAR)
    private String modo;

    // Filtros y preferencias
    private String colorFiltro;  // opcional
    private String marcaFiltro;  // opcional
    private String tiempo;       // FRIO, TEMPLADO, CALOR
    private String ocasion;      // TRABAJO, FIESTA, DEPORTE, CASUAL

    // Para "Sin repetir": prendas que debemos evitar en esta sesión
    private List<Long> prendasEvitarIds;
    private List<String> conjuntosUsados; // claves "supId-infId-calId" ya servidas al usuario en esta sesión

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

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public String getColorFiltro() {
        return colorFiltro;
    }

    public void setColorFiltro(String colorFiltro) {
        this.colorFiltro = colorFiltro;
    }

    public String getMarcaFiltro() {
        return marcaFiltro;
    }

    public void setMarcaFiltro(String marcaFiltro) {
        this.marcaFiltro = marcaFiltro;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getOcasion() {
        return ocasion;
    }

    public void setOcasion(String ocasion) {
        this.ocasion = ocasion;
    }

    public List<Long> getPrendasEvitarIds() {
        return prendasEvitarIds;
    }

    public void setPrendasEvitarIds(List<Long> prendasEvitarIds) {
        this.prendasEvitarIds = prendasEvitarIds;
    }

    public List<String> getConjuntosUsados() {
        return conjuntosUsados;
    }

    public void setConjuntosUsados(List<String> conjuntosUsados) {
        this.conjuntosUsados = conjuntosUsados;
    }
}
