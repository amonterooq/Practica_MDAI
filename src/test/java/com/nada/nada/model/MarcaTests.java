package com.nada.nada.model;

import com.nada.nada.data.model.enums.Marca;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el enum Marca.
 */
public class MarcaTests {

    /**
     * Verifica que las marcas básicas tienen su etiqueta correcta.
     */
    @Test
    void testMarcaTieneEtiqueta() {
        assertEquals("Zara", Marca.ZARA.getEtiqueta());
        assertEquals("Nike", Marca.NIKE.getEtiqueta());
        assertEquals("Adidas", Marca.ADIDAS.getEtiqueta());
    }

    /**
     * Verifica que las marcas compuestas tienen etiquetas con espacios.
     */
    @Test
    void testMarcaTieneEtiquetaConEspacios() {
        assertEquals("Bimba y Lola", Marca.BIMBA_Y_LOLA.getEtiqueta());
        assertEquals("Calvin Klein", Marca.CALVIN_KLEIN.getEtiqueta());
        assertEquals("Massimo Dutti", Marca.MASSIMO_DUTTI.getEtiqueta());
    }

    /**
     * Verifica que las marcas con caracteres especiales mantienen su etiqueta.
     */
    @Test
    void testMarcaTieneEtiquetaConCaracteresEspeciales() {
        assertEquals("H&M", Marca.HM.getEtiqueta());
        assertEquals("Dolce & Gabbana", Marca.DOLCE_GABBANA.getEtiqueta());
        assertEquals("Women'Secret", Marca.WOMEN_SECRET.getEtiqueta());
    }

    /**
     * Verifica la validación de marcas usando la etiqueta legible.
     */
    @Test
    void testMarcaEsValidaConEtiquetaCorrecta() {
        assertTrue(Marca.esValida("Zara"));
        assertTrue(Marca.esValida("Nike"));
        assertTrue(Marca.esValida("Adidas"));
    }

    /**
     * Verifica la validación de marcas usando el nombre del enum (case insensitive).
     */
    @Test
    void testMarcaEsValidaConNombreEnum() {
        assertTrue(Marca.esValida("ZARA"));
        assertTrue(Marca.esValida("nike"));
        assertTrue(Marca.esValida("AdIdAs"));
    }

    /**
     * Verifica que valores incorrectos, vacíos o nulos no son válidos.
     */
    @Test
    void testMarcaNoEsValidaConValorIncorrecto() {
        assertFalse(Marca.esValida("MarcaInexistente"));
        assertFalse(Marca.esValida(""));
        assertFalse(Marca.esValida(null));
    }

    /**
     * Verifica que existe la opción "Otra" para marcas no predefinidas.
     */
    @Test
    void testMarcaContieneOtra() {
        assertEquals("Otra", Marca.OTRA.getEtiqueta());
        assertTrue(Marca.esValida("Otra"));
    }

    /**
     * Verifica que el enum contiene todas las marcas esperadas.
     */
    @Test
    void testMarcaTieneTodasLasMarcasEsperadas() {
        Marca[] marcas = Marca.values();
        assertTrue(marcas.length > 50, "Debe haber más de 50 marcas definidas");

        // Verificar algunas marcas específicas
        boolean tieneZara = false;
        boolean tieneNike = false;
        boolean tieneMango = false;
        boolean tieneOtra = false;

        for (Marca m : marcas) {
            if (m == Marca.ZARA) tieneZara = true;
            if (m == Marca.NIKE) tieneNike = true;
            if (m == Marca.MANGO) tieneMango = true;
            if (m == Marca.OTRA) tieneOtra = true;
        }

        assertTrue(tieneZara, "Debe contener la marca Zara");
        assertTrue(tieneNike, "Debe contener la marca Nike");
        assertTrue(tieneMango, "Debe contener la marca Mango");
        assertTrue(tieneOtra, "Debe contener la opción Otra");
    }

    /**
     * Verifica el método valueOf para obtener marcas por nombre de enum.
     */
    @Test
    void testMarcaValueOf() {
        assertEquals(Marca.ZARA, Marca.valueOf("ZARA"));
        assertEquals(Marca.NIKE, Marca.valueOf("NIKE"));
        assertEquals(Marca.ADIDAS, Marca.valueOf("ADIDAS"));
    }

    /**
     * Verifica que valueOf lanza excepción para valores inválidos.
     */
    @Test
    void testMarcaValueOfLanzaExcepcionParaValorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            Marca.valueOf("MARCA_INEXISTENTE");
        });
    }
}
