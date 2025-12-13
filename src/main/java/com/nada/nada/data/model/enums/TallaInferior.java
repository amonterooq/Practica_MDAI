package com.nada.nada.data.model.enums;

public enum TallaInferior {
    T32("32"),
    T34("34"),
    T36("36"),
    T38("38"),
    T40("40"),
    T42("42"),
    T44("44"),
    T46("46"),
    T48("48"),
    T50("50");

    private final String etiqueta;

    TallaInferior(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static boolean esValida(String valor) {
        if (valor == null) return false;
        String v = valor.trim();
        for (TallaInferior t : values()) {
            if (t.etiqueta.equals(v)) {
                return true;
            }
        }
        return false;
    }
}

