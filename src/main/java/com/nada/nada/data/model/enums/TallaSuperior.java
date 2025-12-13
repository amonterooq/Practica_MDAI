package com.nada.nada.data.model.enums;

public enum TallaSuperior {
    XS, S, M, L, XL, XXL, XXXL, XXXXL;

    public static boolean esValida(String valor) {
        if (valor == null) return false;
        String v = valor.trim().toUpperCase();
        for (TallaSuperior t : values()) {
            if (t.name().equals(v)) {
                return true;
            }
        }
        return false;
    }
}



