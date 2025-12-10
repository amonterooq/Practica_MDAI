package com.nada.nada.data.model;

public enum Marca {
    ADIDAS("Adidas"),
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
    CONVERSE("Converse"),
    CORTEFIEL("Cortefiel"),
    DESIGUAL("Desigual"),
    DIOR("Dior"),
    DOLCE_GABBANA("Dolce & Gabbana"),
    EL_CORTE_INGLES("El Corte Inglés"),
    FILA("Fila"),
    GUESS("Guess"),
    GUCCI("Gucci"),
    HM("H&M"),
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
    SFERA("Sfera"),
    SHEIN("Shein"),
    SPRINGFIELD("Springfield"),
    STRADIVARIUS("Stradivarius"),
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
}

