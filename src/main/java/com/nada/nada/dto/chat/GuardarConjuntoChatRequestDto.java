package com.nada.nada.dto.chat;

public class GuardarConjuntoChatRequestDto {

    private Long prendaSuperiorId;
    private Long prendaInferiorId;
    private Long prendaCalzadoId;
    private String nombre;
    private String notas;

    public GuardarConjuntoChatRequestDto() {
    }

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

