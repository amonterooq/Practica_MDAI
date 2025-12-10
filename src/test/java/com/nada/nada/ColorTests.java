package com.nada.nada;

import com.nada.nada.data.model.Color;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ColorTests {

    @Test
    void testColorTieneEtiqueta() {
        assertEquals("Negro", Color.NEGRO.getEtiqueta());
        assertEquals("Blanco", Color.BLANCO.getEtiqueta());
        assertEquals("Rojo", Color.ROJO.getEtiqueta());
    }

    @Test
    void testColorTieneEtiquetaConEspacios() {
        assertEquals("Azul marino", Color.AZUL_MARINO.getEtiqueta());
        assertEquals("Gris claro", Color.GRIS_CLARO.getEtiqueta());
        assertEquals("Verde militar", Color.VERDE_MILITAR.getEtiqueta());
    }

    @Test
    void testColorTieneEtiquetaConAcentos() {
        assertEquals("Azul eléctrico", Color.AZUL_ELECTRICO.getEtiqueta());
        assertEquals("Marrón", Color.MARRON.getEtiqueta());
    }

    @Test
    void testColorEsValidoConEtiquetaCorrecta() {
        assertTrue(Color.esValida("Negro"));
        assertTrue(Color.esValida("Blanco"));
        assertTrue(Color.esValida("Rojo"));
        assertTrue(Color.esValida("Azul marino"));
    }

    @Test
    void testColorEsValidoConNombreEnum() {
        assertTrue(Color.esValida("NEGRO"));
        assertTrue(Color.esValida("blanco"));
        assertTrue(Color.esValida("RoJo"));
    }

    @Test
    void testColorNoEsValidoConValorIncorrecto() {
        assertFalse(Color.esValida("ColorInexistente"));
        assertFalse(Color.esValida(""));
        assertFalse(Color.esValida(null));
    }

    @Test
    void testColorContieneOtro() {
        assertEquals("Otro", Color.OTRO.getEtiqueta());
        assertTrue(Color.esValida("Otro"));
    }

    @Test
    void testColorContienePatrones() {
        assertEquals("A cuadros", Color.A_CUADROS.getEtiqueta());
        assertEquals("A rayas", Color.A_RAYAS.getEtiqueta());
        assertEquals("Estampado", Color.ESTAMPADO.getEtiqueta());
        assertEquals("Floral", Color.FLORAL.getEtiqueta());
        assertEquals("Animal print", Color.ANIMAL_PRINT.getEtiqueta());
    }

    @Test
    void testColorTieneTodosLosColoresEsperados() {
        Color[] colores = Color.values();
        assertTrue(colores.length > 45, "Debe haber más de 45 colores definidos");

        // Verificar algunos colores específicos
        boolean tieneNegro = false;
        boolean tieneBlanco = false;
        boolean tieneAzulMarino = false;
        boolean tieneOtro = false;

        for (Color c : colores) {
            if (c == Color.NEGRO) tieneNegro = true;
            if (c == Color.BLANCO) tieneBlanco = true;
            if (c == Color.AZUL_MARINO) tieneAzulMarino = true;
            if (c == Color.OTRO) tieneOtro = true;
        }

        assertTrue(tieneNegro, "Debe contener el color Negro");
        assertTrue(tieneBlanco, "Debe contener el color Blanco");
        assertTrue(tieneAzulMarino, "Debe contener el color Azul marino");
        assertTrue(tieneOtro, "Debe contener la opción Otro");
    }

    @Test
    void testColorValueOf() {
        assertEquals(Color.NEGRO, Color.valueOf("NEGRO"));
        assertEquals(Color.BLANCO, Color.valueOf("BLANCO"));
        assertEquals(Color.AZUL_MARINO, Color.valueOf("AZUL_MARINO"));
    }

    @Test
    void testColorValueOfLanzaExcepcionParaValorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Color.valueOf("COLOR_INEXISTENTE"));
    }

    @Test
    void testColorTieneVariedadDeAzules() {
        assertTrue(Color.esValida("Azul cielo"));
        assertTrue(Color.esValida("Azul claro"));
        assertTrue(Color.esValida("Azul eléctrico"));
        assertTrue(Color.esValida("Azul marino"));
        assertTrue(Color.esValida("Azul oscuro"));
    }

    @Test
    void testColorTieneVariedadDeVerdes() {
        assertTrue(Color.esValida("Verde botella"));
        assertTrue(Color.esValida("Verde claro"));
        assertTrue(Color.esValida("Verde menta"));
        assertTrue(Color.esValida("Verde militar"));
        assertTrue(Color.esValida("Verde oscuro"));
    }

    @Test
    void testColorTieneVariedadDeGrises() {
        assertTrue(Color.esValida("Gris"));
        assertTrue(Color.esValida("Gris claro"));
        assertTrue(Color.esValida("Gris oscuro"));
    }

    @Test
    void testColorTieneTonalidades() {
        assertTrue(Color.esValida("Beige"));
        assertTrue(Color.esValida("Beige rosado"));
        assertTrue(Color.esValida("Camel"));
        assertTrue(Color.esValida("Crema"));
        assertTrue(Color.esValida("Crudo"));
        assertTrue(Color.esValida("Marfil"));
    }
}

