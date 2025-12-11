package com.nada.nada.data.model;

public enum Marca {
    ADIDAS("Adidas"),
    AIR_JORDAN("Air Jordan"),
    ARMANI("Armani"),
    ASICS("Asics"),
    ASOS("Asos"),
    BALENCIAGA("Balenciaga"),
    BENETTON("Benetton"),
    BERSHKA("Bershka"),
    BIMBA_Y_LOLA("Bimba y Lola"),
    CALVIN_KLEIN("Calvin Klein"),
    CALZEDONIA("Calzedonia"),
    CHANEL("Chanel"),
    CLARKS("Clarks"),
    CONVERSE("Converse"),
    CORTEFIEL("Cortefiel"),
    DESIGUAL("Desigual"),
    DIOR("Dior"),
    DOLCE_GABBANA("Dolce & Gabbana"),
    EL_CORTE_INGLES("El Corte Inglés"),
    FILA("Fila"),
    GAP("Gap"),
    GUESS("Guess"),
    GUCCI("Gucci"),
    HM("H&M"),
    HUGO_BOSS("Hugo Boss"),
    INTIMISSIMI("Intimissimi"),
    JACK_JONES("Jack&Jones"),
    LACOSTE("Lacoste"),
    LEFTIES("Lefties"),
    LEVIS("Levi's"),
    MANGO("Mango"),
    MASSIMO_DUTTI("Massimo Dutti"),
    NEW_BALANCE("New Balance"),
    NIKE("Nike"),
    ONLY("Only"),
    OYSHO("Oysho"),
    PACO_MARTINEZ("Paco Martínez"),
    PEDRO_DEL_HIERRO("Pedro del Hierro"),
    PEPE_JEANS("Pepe Jeans"),
    PRADA("Prada"),
    PRIMARK("Primark"),
    PULL_BEAR("Pull&Bear"),
    PUMA("Puma"),
    RALPH_LAUREN("Ralph Lauren"),
    REEBOK("Reebok"),
    SALOMON("Salomon"),
    SFERA("Sfera"),
    SHEIN("Shein"),
    SPRINGFIELD("Springfield"),
    STRADIVARIUS("Stradivarius"),
    THE_NORTH_FACE("The North Face"),
    TOMMY_HILFIGER("Tommy Hilfiger"),
    UNDER_ARMOUR("Under Armour"),
    UNIQLO("Uniqlo"),
    VALENTINO("Valentino"),
    VANS("Vans"),
    WOMEN_SECRET("Women'Secret"),
    ZALANDO("Zalando"),
    ZARA("Zara"),
    OTRA("Otra");

    private final String etiqueta;

    Marca(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static boolean esValida(String valor) {
        if (valor == null) return false;
        String v = valor.trim();
        for (Marca m : values()) {
            if (m.etiqueta.equals(v) || m.name().equalsIgnoreCase(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convierte un string a un valor de Marca enum.
     * Primero intenta coincidir por etiqueta (case-insensitive).
     * Luego por name() (case-insensitive).
     * Luego normalizando espacios a '_', eliminando '&' y '\'' y comparando con name().
     * Si no encuentra coincidencia, devuelve Marca.OTRA.
     */
    public static Marca fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return Marca.OTRA;
        }
        
        String raw = valor.trim();
        
        // Intentar coincidir por etiqueta (case-insensitive)
        for (Marca m : values()) {
            if (m.etiqueta.equalsIgnoreCase(raw)) {
                return m;
            }
        }
        
        // Intentar coincidir por name() (case-insensitive)
        for (Marca m : values()) {
            if (m.name().equalsIgnoreCase(raw)) {
                return m;
            }
        }
        
        // Normalizar: espacios a '_', eliminar '&' y '\''
        String normalized = raw.replace(" ", "_")
                               .replace("&", "")
                               .replace("'", "");
        
        for (Marca m : values()) {
            if (m.name().equalsIgnoreCase(normalized)) {
                return m;
            }
        }
        
        return Marca.OTRA;
    }
}

