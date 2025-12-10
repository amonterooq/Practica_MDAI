package com.nada.nada;

import com.nada.nada.data.model.Marca;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MarcaTests {

    @Test
    void testMarcaTieneEtiqueta() {
        assertEquals("Zara", Marca.ZARA.getEtiqueta());
        assertEquals("Nike", Marca.NIKE.getEtiqueta());
        assertEquals("Adidas", Marca.ADIDAS.getEtiqueta());
    }

    @Test
    void testMarcaTieneEtiquetaConEspacios() {
        assertEquals("Bimba y Lola", Marca.BIMBA_Y_LOLA.getEtiqueta());
        assertEquals("Calvin Klein", Marca.CALVIN_KLEIN.getEtiqueta());
        assertEquals("Massimo Dutti", Marca.MASSIMO_DUTTI.getEtiqueta());
    }

    @Test
    void testMarcaTieneEtiquetaConCaracteresEspeciales() {
        assertEquals("H&M", Marca.HM.getEtiqueta());
        assertEquals("Dolce & Gabbana", Marca.DOLCE_GABBANA.getEtiqueta());
        assertEquals("Women'Secret", Marca.WOMEN_SECRET.getEtiqueta());
    }

    @Test
    void testMarcaEsValidaConEtiquetaCorrecta() {
        assertTrue(Marca.esValida("Zara"));
        assertTrue(Marca.esValida("Nike"));
        assertTrue(Marca.esValida("Adidas"));
    }

    @Test
    void testMarcaEsValidaConNombreEnum() {
        assertTrue(Marca.esValida("ZARA"));
        assertTrue(Marca.esValida("nike"));
        assertTrue(Marca.esValida("AdIdAs"));
    }

    @Test
    void testMarcaNoEsValidaConValorIncorrecto() {
        assertFalse(Marca.esValida("MarcaInexistente"));
        assertFalse(Marca.esValida(""));
        assertFalse(Marca.esValida(null));
    }

    @Test
    void testMarcaContieneOtra() {
        assertEquals("Otra", Marca.OTRA.getEtiqueta());
        assertTrue(Marca.esValida("Otra"));
    }

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

    @Test
    void testMarcaValueOf() {
        assertEquals(Marca.ZARA, Marca.valueOf("ZARA"));
        assertEquals(Marca.NIKE, Marca.valueOf("NIKE"));
        assertEquals(Marca.ADIDAS, Marca.valueOf("ADIDAS"));
    }

    @Test
    void testMarcaValueOfLanzaExcepcionParaValorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            Marca.valueOf("MARCA_INEXISTENTE");
        });
    }
}

