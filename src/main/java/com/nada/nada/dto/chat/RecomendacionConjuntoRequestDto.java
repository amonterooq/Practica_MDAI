package com.nada.nada.dto.chat;

import java.util.List;

/**
 * DTO para las solicitudes de recomendación de conjuntos.
 * Contiene todas las preferencias y filtros para generar recomendaciones personalizadas.
 */
public class RecomendacionConjuntoRequestDto {

    /** ID de prenda superior fija (opcional) */
    private Long superiorFijoId;

    /** ID de prenda inferior fija (opcional) */
    private Long inferiorFijoId;

    /** ID de calzado fijo (opcional) */
    private Long calzadoFijoId;

    /** Modo de recomendación: SORPRESA, COLOR, MARCA, TIEMPO, OCASION, SIN_REPETIR, COMPLETAR */
    private String modo;

    /** Filtro por color (opcional) */
    private String colorFiltro;

    /** Filtro por marca (opcional) */
    private String marcaFiltro;

    /** Condición climática: FRIO, TEMPLADO, CALOR */
    private String tiempo;

    /** Tipo de ocasión: TRABAJO, FIESTA, DEPORTE, CASUAL */
    private String ocasion;

    /** IDs de prendas a evitar en esta sesión (modo SIN_REPETIR) */
    private List<Long> prendasEvitarIds;

    /** Claves de conjuntos ya mostrados en esta sesión (formato: "supId-infId-calId") */
    private List<String> conjuntosUsados;

    /** Tipo de combinación para modos COLOR/MARCA: TODO, COMBINADO */
    private String tipoCombinacion;

    /** Intensidad del criterio: PROTAGONISTA, TOQUE */
    private String intensidad;

    /**
     * Constructor por defecto.
     */
    public RecomendacionConjuntoRequestDto() {
    }

    // =====================================================================
    // GETTERS Y SETTERS
    // =====================================================================

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

    public String getTipoCombinacion() {
        return tipoCombinacion;
    }

    public void setTipoCombinacion(String tipoCombinacion) {
        this.tipoCombinacion = tipoCombinacion;
    }

    public String getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(String intensidad) {
        this.intensidad = intensidad;
    }
}
