package com.nada.nada.data.services;

import com.nada.nada.data.model.PrendaCalzado;
import com.nada.nada.data.model.PrendaInferior;
import com.nada.nada.data.model.PrendaSuperior;
import com.nada.nada.data.model.enums.CategoriaCalzado;
import com.nada.nada.data.model.enums.CategoriaInferior;
import com.nada.nada.data.model.enums.CategoriaSuperior;
import com.nada.nada.data.model.enums.Manga;

import java.util.*;

/**
 * Analizador climático que evalúa prendas según su idoneidad para diferentes climas
 * usando lógica visual y sentido común (SIN datos meteorológicos reales).
 */
public class AnalizadorClimatico {

    private static final Set<String> COLORES_CLAROS = Set.of(
            "blanco", "beige", "crema", "amarillo", "naranja", "rosa claro", "celeste", "gris claro"
    );

    private static final Set<String> COLORES_OSCUROS = Set.of(
            "negro", "azul marino", "gris oscuro", "marrón oscuro", "burdeos", "verde oscuro"
    );

    // Categorías superiores abrigadas (para frío)
    private static final Set<CategoriaSuperior> SUPERIORES_ABRIGADAS = Set.of(
            CategoriaSuperior.ABRIGO,
            CategoriaSuperior.CAZADORA,
            CategoriaSuperior.CHAQUETA,
            CategoriaSuperior.JERSEY,
            CategoriaSuperior.SUDADERA,
            CategoriaSuperior.CÁRDIGAN
    );

    // Categorías superiores ligeras (para calor)
    private static final Set<CategoriaSuperior> SUPERIORES_LIGERAS = Set.of(
            CategoriaSuperior.CAMISETA,
            CategoriaSuperior.TOP,
            CategoriaSuperior.BLUSA,
            CategoriaSuperior.POLO,
            CategoriaSuperior.BODI,
            CategoriaSuperior.CORPIÑO
    );

    // Categorías inferiores cubiertas (para frío)
    private static final Set<CategoriaInferior> INFERIORES_CUBIERTOS = Set.of(
            CategoriaInferior.PANTALON,
            CategoriaInferior.PANTALON_VAQUERO,
            CategoriaInferior.PANTALON_VESTIR,
            CategoriaInferior.JEAN,
            CategoriaInferior.LEGGINGS,
            CategoriaInferior.MONO_LARGO,
            CategoriaInferior.FALDA_LARGA
    );

    // Categorías inferiores ligeras (para calor)
    private static final Set<CategoriaInferior> INFERIORES_LIGEROS = Set.of(
            CategoriaInferior.SHORT,
            CategoriaInferior.BERMUDA,
            CategoriaInferior.FALDA_CORTA,
            CategoriaInferior.MONO_CORTO
    );

    // Categorías calzado cerrado (para frío)
    private static final Set<CategoriaCalzado> CALZADO_CERRADO = Set.of(
            CategoriaCalzado.BOTA,
            CategoriaCalzado.BOTAS_MILITARES,
            CategoriaCalzado.BOTIN,
            CategoriaCalzado.DEPORTIVO,
            CategoriaCalzado.SNEAKER,
            CategoriaCalzado.ZAPATILLA_CASUAL,
            CategoriaCalzado.ZAPATO_FORMAL
    );

    // Categorías calzado abierto/ligero (para calor)
    private static final Set<CategoriaCalzado> CALZADO_LIGERO = Set.of(
            CategoriaCalzado.SANDALIA,
            CategoriaCalzado.CHANCLA,
            CategoriaCalzado.BAILARINA,
            CategoriaCalzado.ALPARGATA
    );

    public static class ConjuntoEvaluado {
        public PrendaSuperior superior;
        public PrendaInferior inferior;
        public PrendaCalzado calzado;
        public int penalizacion;
        public List<String> razones;

        public ConjuntoEvaluado(PrendaSuperior superior, PrendaInferior inferior, PrendaCalzado calzado) {
            this.superior = superior;
            this.inferior = inferior;
            this.calzado = calzado;
            this.penalizacion = 0;
            this.razones = new ArrayList<>();
        }
    }

    /**
     * Evalúa un conjunto según el clima especificado.
     * Retorna un objeto con la penalización total (menor es mejor).
     */
    public static ConjuntoEvaluado evaluarConjunto(PrendaSuperior superior, PrendaInferior inferior,
                                                    PrendaCalzado calzado, String clima) {
        ConjuntoEvaluado evaluado = new ConjuntoEvaluado(superior, inferior, calzado);

        if (clima == null) {
            return evaluado;
        }

        switch (clima.toUpperCase()) {
            case "FRIO":
                evaluarParaFrio(evaluado);
                break;
            case "CALOR":
                evaluarParaCalor(evaluado);
                break;
            case "TEMPLADO":
                evaluarParaTemplado(evaluado);
                break;
        }

        return evaluado;
    }

    private static void evaluarParaFrio(ConjuntoEvaluado conjunto) {
        // SUPERIOR
        if (conjunto.superior != null) {
            CategoriaSuperior cat = conjunto.superior.getCategoria();
            Manga manga = conjunto.superior.getManga();

            // Penalizar prendas muy ligeras
            if (SUPERIORES_LIGERAS.contains(cat)) {
                if (manga == Manga.SIN_MANGA) {
                    conjunto.penalizacion += 3;
                } else if (manga == Manga.CORTA) {
                    conjunto.penalizacion += 2;
                } else {
                    conjunto.penalizacion += 1; // Ligera pero con manga larga es menos malo
                }
            }

            // Premiar prendas abrigadas (menos penalización = mejor)
            if (SUPERIORES_ABRIGADAS.contains(cat)) {
                // Perfecto para frío, sin penalización adicional
                // (no hacer nada, dejar penalización en 0 para estas prendas)
            }

            // Color: preferir oscuros/neutros
            if (esColorClaro(conjunto.superior.getColor())) {
                conjunto.penalizacion += 1;
            }
        }

        // INFERIOR
        if (conjunto.inferior != null) {
            CategoriaInferior cat = conjunto.inferior.getCategoriaInferior();

            // Penalizar prendas muy cortas/ligeras
            if (INFERIORES_LIGEROS.contains(cat)) {
                conjunto.penalizacion += 3;
            }

            // Color: preferir oscuros
            if (esColorClaro(conjunto.inferior.getColor())) {
                conjunto.penalizacion += 1;
            }
        }

        // CALZADO
        if (conjunto.calzado != null) {
            CategoriaCalzado cat = conjunto.calzado.getCategoria();

            // Penalizar calzado abierto/ligero
            if (CALZADO_LIGERO.contains(cat)) {
                conjunto.penalizacion += 2;
            }

            // Color: preferir oscuros
            if (esColorClaro(conjunto.calzado.getColor())) {
                conjunto.penalizacion += 1;
            }
        }
    }

    private static void evaluarParaCalor(ConjuntoEvaluado conjunto) {
        // SUPERIOR
        if (conjunto.superior != null) {
            CategoriaSuperior cat = conjunto.superior.getCategoria();
            Manga manga = conjunto.superior.getManga();

            // Penalizar prendas muy abrigadas
            if (SUPERIORES_ABRIGADAS.contains(cat)) {
                if (cat == CategoriaSuperior.ABRIGO) {
                    conjunto.penalizacion += 3;
                } else {
                    conjunto.penalizacion += 2;
                }
            }

            // Penalizar manga larga en prendas no abrigadas
            if (!SUPERIORES_ABRIGADAS.contains(cat) && manga == Manga.LARGA) {
                conjunto.penalizacion += 1;
            }

            // Color: preferir claros
            if (esColorOscuro(conjunto.superior.getColor())) {
                conjunto.penalizacion += 1;
            }
        }

        // INFERIOR
        if (conjunto.inferior != null) {
            CategoriaInferior cat = conjunto.inferior.getCategoriaInferior();

            // Penalizar prendas muy cubiertas (excepto opciones frescas)
            if (INFERIORES_CUBIERTOS.contains(cat)) {
                if (cat != CategoriaInferior.FALDA_LARGA) { // Falda larga puede ser fresca
                    conjunto.penalizacion += 1;
                }
            }

            // Color: preferir claros
            if (esColorOscuro(conjunto.inferior.getColor())) {
                conjunto.penalizacion += 1;
            }
        }

        // CALZADO
        if (conjunto.calzado != null) {
            CategoriaCalzado cat = conjunto.calzado.getCategoria();

            // Penalizar calzado muy cerrado/abrigado
            if (cat == CategoriaCalzado.BOTA || cat == CategoriaCalzado.BOTAS_MILITARES) {
                conjunto.penalizacion += 2;
            }

            // Color: preferir claros
            if (esColorOscuro(conjunto.calzado.getColor())) {
                conjunto.penalizacion += 1;
            }
        }
    }

    private static void evaluarParaTemplado(ConjuntoEvaluado conjunto) {
        // Para templado, penalizamos extremos

        // SUPERIOR
        if (conjunto.superior != null) {
            CategoriaSuperior cat = conjunto.superior.getCategoria();
            Manga manga = conjunto.superior.getManga();

            // Penalizar extremos: muy abrigado o muy ligero
            if (cat == CategoriaSuperior.ABRIGO) {
                conjunto.penalizacion += 2;
            } else if (manga == Manga.SIN_MANGA && SUPERIORES_LIGERAS.contains(cat)) {
                conjunto.penalizacion += 1;
            }
        }

        // INFERIOR
        if (conjunto.inferior != null) {
            CategoriaInferior cat = conjunto.inferior.getCategoriaInferior();

            // Penalizar extremos: muy corto
            if (cat == CategoriaInferior.SHORT || cat == CategoriaInferior.MONO_CORTO) {
                conjunto.penalizacion += 1;
            }
        }

        // CALZADO
        if (conjunto.calzado != null) {
            CategoriaCalzado cat = conjunto.calzado.getCategoria();

            // Penalizar extremos: botas o chanclas
            if (cat == CategoriaCalzado.BOTA || cat == CategoriaCalzado.BOTAS_MILITARES ||
                cat == CategoriaCalzado.CHANCLA) {
                conjunto.penalizacion += 1;
            }
        }

        // En templado, el color es más flexible, no penalizamos tanto
    }

    /**
     * Genera un mensaje explicativo basado en el clima y la evaluación.
     */
    public static String generarExplicacion(String clima, ConjuntoEvaluado mejor, int totalCandidatos,
                                           boolean pudoAdaptar) {
        if (clima == null) {
            return "He combinado prendas de tu armario creando un conjunto equilibrado.";
        }

        StringBuilder sb = new StringBuilder();

        if (!pudoAdaptar) {
            sb.append("Con tus prendas actuales no puedo adaptar mucho el conjunto al tiempo, pero te propongo una opción equilibrada.");
            return sb.toString();
        }

        switch (clima.toUpperCase()) {
            case "FRIO":
                sb.append("❄️ Conjunto pensado para frío: ");
                if (mejor.penalizacion == 0) {
                    sb.append("combinación bien cubierta y abrigada.");
                } else if (mejor.penalizacion <= 2) {
                    sb.append("he evitado prendas muy ligeras y he optado por una combinación más cerrada.");
                } else {
                    sb.append("he intentado priorizar prendas más cubiertas dentro de tu armario.");
                }
                break;

            case "CALOR":
                sb.append("☀️ Conjunto pensado para calor: ");
                if (mejor.penalizacion == 0) {
                    sb.append("combinación ligera y fresca.");
                } else if (mejor.penalizacion <= 2) {
                    sb.append("pensado para evitar sensación de calor, priorizando prendas más ligeras.");
                } else {
                    sb.append("he buscado las opciones más frescas de tu armario.");
                }
                break;

            case "TEMPLADO":
                sb.append("🌤️ Conjunto pensado para clima templado: ");
                if (mejor.penalizacion == 0) {
                    sb.append("combinación equilibrada perfecta para temperaturas suaves.");
                } else if (mejor.penalizacion <= 2) {
                    sb.append("equilibrado para temperaturas moderadas, evitando extremos.");
                } else {
                    sb.append("opción versátil para clima variable.");
                }
                break;

            default:
                return "He combinado prendas de tu armario creando un conjunto equilibrado.";
        }

        return sb.toString();
    }

    /**
     * Determina si hay suficiente variedad de prendas para adaptar al clima.
     */
    public static boolean puedeAdaptarAlClima(List<PrendaSuperior> superiores,
                                             List<PrendaInferior> inferiores,
                                             List<PrendaCalzado> calzados,
                                             String clima) {
        if (clima == null) return true;

        // Si hay al menos 2 opciones en cada categoría, asumimos que puede haber variedad
        return superiores.size() >= 2 && inferiores.size() >= 2 && calzados.size() >= 2;
    }

    private static boolean esColorClaro(String color) {
        if (color == null) return false;
        String colorLower = color.toLowerCase();
        return COLORES_CLAROS.stream().anyMatch(colorLower::contains);
    }

    private static boolean esColorOscuro(String color) {
        if (color == null) return false;
        String colorLower = color.toLowerCase();
        return COLORES_OSCUROS.stream().anyMatch(colorLower::contains);
    }
}

