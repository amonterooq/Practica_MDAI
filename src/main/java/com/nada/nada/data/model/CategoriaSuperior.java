package com.nada.nada.data.model;

public enum CategoriaSuperior {
    ABRIGO("Abrigo"),
    AMERICANA("Americana"),
    BLUSA("Blusa"),
    BODI("Bodi"),
    CAMISA("Camisa"),
    CAMISETA("Camiseta"),
    CÁRDIGAN("Cárdigan"),
    CAZADORA("Cazadora"),
    CHALECO("Chaleco"),
    CHAQUETA("Chaqueta"),
    CORPIÑO("Corpiño"),
    JERSEY("Jersey"),
    KIMONO("Kimono"),
    POLO("Polo"),
    SUDADERA("Sudadera"),
    TOP("Top");

    private final String etiqueta;

    CategoriaSuperior(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static boolean esValida(String valor) {
        if (valor == null) return false;
        String v = valor.trim();
        for (CategoriaSuperior c : values()) {
            if (c.etiqueta.equals(v) || c.name().equalsIgnoreCase(v)) {
                return true;
            }
        }
        return false;
    }
}
