package com.nada.nada.data.model.enums;

public enum CategoriaInferior {
    BERMUDA("Bermuda"),
    FALDA_CORTA("Falda corta"),
    FALDA_LARGA("Falda larga"),
    FALDA_MIDI("Falda midi"),
    JEAN("Jean"),
    JOGGER("Jogger"),
    LEGGINGS("Leggings"),
    MONO_CORTO("Mono corto"),
    MONO_LARGO("Mono largo"),
    PANTALON("Pantalón"),
    PANTALON_CHANDAL("Pantalón chándal"),
    PANTALON_VAQUERO("Pantalón vaquero"),
    PANTALON_VESTIR("Pantalón de vestir"),
    SHORT("Short");

    private final String etiqueta;

    CategoriaInferior(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static boolean esValida(String valor) {
        if (valor == null) return false;
        String v = valor.trim();
        for (CategoriaInferior c : values()) {
            if (c.etiqueta.equals(v) || c.name().equalsIgnoreCase(v)) {
                return true;
            }
        }
        return false;
    }
}
