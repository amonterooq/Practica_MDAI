package com.nada.nada.data.model;

public enum CategoriaCalzado {
    ALPARGATA("Alpargata"),
    BAILARINA("Bailarina"),
    BOTA("Bota"),
    BOTAS_MILITARES("Botas militares"),
    BOTIN("Botín"),
    CHANCLA("Chancla"),
    DEPORTIVO("Deportivo"),
    MOCASIN("Mocasín"),
    NÁUTICO("Náutico"),
    SANDALIA("Sandalia"),
    SNEAKER("Sneaker"),
    TACON("Tacón"),
    ZAPATILLA_CASUAL("Zapatilla casual"),
    ZAPATO_FORMAL("Zapato formal");

    private final String etiqueta;

    CategoriaCalzado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static boolean esValida(String valor) {
        if (valor == null) return false;
        String v = valor.trim();
        for (CategoriaCalzado c : values()) {
            if (c.etiqueta.equals(v) || c.name().equalsIgnoreCase(v)) {
                return true;
            }
        }
        return false;
    }
}
