package com.nada.nada.data.model.enums;

public enum TallaCalzado {
    N34("34"),
    N35("35"),
    N36("36"),
    N37("37"),
    N38("38"),
    N39("39"),
    N40("40"),
    N41("41"),
    N42("42"),
    N43("43"),
    N44("44"),
    N45("45"),
    N46("46");

    private final String etiqueta;

    TallaCalzado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static boolean esValida(String valor) {
        if (valor == null) return false;
        String v = valor.trim();
        for (TallaCalzado t : values()) {
            if (t.etiqueta.equals(v)) {
                return true;
            }
        }
        return false;
    }
}

