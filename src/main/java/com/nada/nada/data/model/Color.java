package com.nada.nada.data.model;

public enum Color {
    A_CUADROS("A cuadros"),
    A_RAYAS("A rayas"),
    AMARILLO("Amarillo"),
    ANIMAL_PRINT("Animal print"),
    Azul("Azul"),
    AZUL_CIELO("Azul cielo"),
    AZUL_CLARO("Azul claro"),
    AZUL_ELECTRICO("Azul eléctrico"),
    AZUL_MARINO("Azul marino"),
    AZUL_OSCURO("Azul oscuro"),
    BEIGE("Beige"),
    BEIGE_ROSADO("Beige rosado"),
    BLANCO("Blanco"),
    BRONCE("Bronce"),
    BURDEOS("Burdeos"),
    CAMEL("Camel"),
    CAQUI("Caqui"),
    CHOCOLATE("Chocolate"),
    COBRIZO("Cobrizo"),
    CORAL("Coral"),
    CREMA("Crema"),
    CRUDO("Crudo"),
    DORADO("Dorado"),
    ESTAMPADO("Estampado"),
    FLORAL("Floral"),
    FUCSIA("Fucsia"),
    GRANATE("Granate"),
    GRIS("Gris"),
    GRIS_CLARO("Gris claro"),
    GRIS_OSCURO("Gris oscuro"),
    LAVANDA("Lavanda"),
    LILA("Lila"),
    MARFIL("Marfil"),
    MARRON("Marrón"),
    MORADO("Morado"),
    MOSTAZA("Mostaza"),
    NARANJA("Naranja"),
    NEGRO("Negro"),
    PLATEADO("Plateado"),
    ROJO("Rojo"),
    ROSA("Rosa"),
    ROSA_PALO("Rosa palo"),
    TEAL("Teal"),
    TURQUESA("Turquesa"),
    VERDE_BOTELLA("Verde botella"),
    VERDE_CLARO("Verde claro"),
    VERDE_MENTA("Verde menta"),
    VERDE_MILITAR("Verde militar"),
    VERDE_OSCURO("Verde oscuro"),
    OTRO("Otro");

    private final String etiqueta;

    Color(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static boolean esValida(String valor) {
        if (valor == null) return false;
        String v = valor.trim();
        for (Color c : values()) {
            if (c.etiqueta.equals(v) || c.name().equalsIgnoreCase(v)) {
                return true;
            }
        }
        return false;
    }
}

