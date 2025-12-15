package com.nada.nada.data.services;

import com.nada.nada.data.model.Conjunto;
import com.nada.nada.data.model.Prenda;
import com.nada.nada.data.model.PrendaCalzado;
import com.nada.nada.data.model.PrendaInferior;
import com.nada.nada.data.model.PrendaSuperior;
import com.nada.nada.dto.chat.RecomendacionConjuntoRequestDto;
import com.nada.nada.dto.chat.RecomendacionConjuntoResponseDto;
import com.nada.nada.dto.chat.RecomendacionPrendaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecomendadorConjuntosServiceImpl implements RecomendadorConjuntosService {

    private final PrendaService prendaService;
    private final ConjuntoService conjuntoService;
    private final Random random = new Random();

    @Autowired
    public RecomendadorConjuntosServiceImpl(PrendaService prendaService,
                                            ConjuntoService conjuntoService) {
        this.prendaService = prendaService;
        this.conjuntoService = conjuntoService;
    }

    @Override
    public RecomendacionConjuntoResponseDto recomendarConjunto(Long usuarioId,
                                                                Long superiorFijoId,
                                                                Long inferiorFijoId,
                                                                Long calzadoFijoId) {
        RecomendacionConjuntoRequestDto prefs = new RecomendacionConjuntoRequestDto();
        prefs.setSuperiorFijoId(superiorFijoId);
        prefs.setInferiorFijoId(inferiorFijoId);
        prefs.setCalzadoFijoId(calzadoFijoId);
        return recomendarConjunto(usuarioId, prefs);
    }

    @Override
    public RecomendacionConjuntoResponseDto recomendarConjunto(Long usuarioId,
                                                                RecomendacionConjuntoRequestDto preferencias) {
        Long superiorFijoId = preferencias != null ? preferencias.getSuperiorFijoId() : null;
        Long inferiorFijoId = preferencias != null ? preferencias.getInferiorFijoId() : null;
        Long calzadoFijoId = preferencias != null ? preferencias.getCalzadoFijoId() : null;
        String modo = preferencias != null ? preferencias.getModo() : null;
        String colorFiltro = preferencias != null ? preferencias.getColorFiltro() : null;
        String marcaFiltro = preferencias != null ? preferencias.getMarcaFiltro() : null;
        List<Long> prendasEvitarIds = preferencias != null ? preferencias.getPrendasEvitarIds() : null;
        List<String> conjuntosUsados = preferencias != null ? preferencias.getConjuntosUsados() : null;

        RecomendacionConjuntoResponseDto dto = new RecomendacionConjuntoResponseDto();

        if (usuarioId == null) {
            dto.setMensaje("No se ha podido identificar al usuario para generar recomendaciones.");
            dto.setFaltantes(Collections.singletonList("usuario"));
            return dto;
        }

        List<Prenda> prendasUsuario = prendaService.buscarPrendasPorUsuarioId(usuarioId);

        // Filtro por color/marca cuando aplique el modo
        if (modo != null && modo.equalsIgnoreCase("COLOR") && colorFiltro != null && !colorFiltro.isBlank()) {
            prendasUsuario = prendasUsuario.stream()
                    .filter(p -> colorFiltro.equalsIgnoreCase(p.getColor()))
                    .collect(Collectors.toList());
        }
        if (modo != null && modo.equalsIgnoreCase("MARCA") && marcaFiltro != null && !marcaFiltro.isBlank()) {
            String mf = marcaFiltro.toLowerCase();
            prendasUsuario = prendasUsuario.stream()
                    .filter(p -> p.getMarca() != null && p.getMarca().toLowerCase().contains(mf))
                    .collect(Collectors.toList());
        }

        // Para "SIN_REPETIR": evitar prendas que el chat ya ha usado en esta sesión
        if (modo != null && modo.equalsIgnoreCase("SIN_REPETIR") && prendasEvitarIds != null && !prendasEvitarIds.isEmpty()) {
            Set<Long> evitar = new HashSet<>(prendasEvitarIds);
            prendasUsuario = prendasUsuario.stream()
                    .filter(p -> p.getId() == null || !evitar.contains(p.getId()))
                    .collect(Collectors.toList());
        }

        List<PrendaSuperior> superiores = prendasUsuario.stream()
                .filter(p -> p instanceof PrendaSuperior)
                .map(p -> (PrendaSuperior) p)
                .collect(Collectors.toList());

        List<PrendaInferior> inferiores = prendasUsuario.stream()
                .filter(p -> p instanceof PrendaInferior)
                .map(p -> (PrendaInferior) p)
                .collect(Collectors.toList());

        List<PrendaCalzado> calzados = prendasUsuario.stream()
                .filter(p -> p instanceof PrendaCalzado)
                .map(p -> (PrendaCalzado) p)
                .collect(Collectors.toList());

        List<String> faltantes = new ArrayList<>();
        if (superiores.isEmpty()) faltantes.add("superior");
        if (inferiores.isEmpty()) faltantes.add("inferior");
        if (calzados.isEmpty()) faltantes.add("calzado");

        dto.setFaltantes(faltantes);

        if (!faltantes.isEmpty()) {
            if (faltantes.size() == 3) {
                dto.setMensaje("Necesitas al menos una prenda superior, una inferior y calzado para que pueda recomendarte conjuntos.");
            } else {
                dto.setMensaje("Aún te faltan prendas para crear conjuntos completos: " + String.join(", ", faltantes) + ".");
            }
            return dto;
        }

        // Evitar recomendar conjuntos ya existentes
        Set<String> combinacionesExistentes = cargarCombinacionesExistentes(usuarioId);

        // En modo SIN_REPETIR, también evitamos las combinaciones ya propuestas en esta sesión
        if (modo != null && modo.equalsIgnoreCase("SIN_REPETIR") && conjuntosUsados != null && !conjuntosUsados.isEmpty()) {
            combinacionesExistentes = new HashSet<>(combinacionesExistentes); // copiar para no modificar el original
            combinacionesExistentes.addAll(conjuntosUsados);
        }

        PrendaSuperior superior = null;
        PrendaInferior inferior = null;
        PrendaCalzado calzado = null;

        final int MAX_INTENTOS = 30;
        int intentos = 0;
        boolean encontradaNueva = false;

        while (intentos < MAX_INTENTOS && !encontradaNueva) {
            intentos++;

            PrendaSuperior candidatoSup = elegirSuperior(superiores, superiorFijoId);
            PrendaInferior candidatoInf = elegirInferior(inferiores, inferiorFijoId);
            PrendaCalzado candidatoCal = elegirCalzado(calzados, calzadoFijoId);

            if (candidatoSup == null || candidatoInf == null || candidatoCal == null) {
                break;
            }

            String clave = construirClave(candidatoSup.getId(), candidatoInf.getId(), candidatoCal.getId());
            if (!combinacionesExistentes.contains(clave)) {
                superior = candidatoSup;
                inferior = candidatoInf;
                calzado = candidatoCal;
                encontradaNueva = true;
            }
        }

        if (!encontradaNueva) {
            if (modo != null && modo.equalsIgnoreCase("SIN_REPETIR")) {
                dto.setMensaje("Ya no quedan más combinaciones diferentes con tus prendas y los filtros actuales.");
            } else {
                dto.setMensaje("Ya tienes guardados todos los conjuntos posibles con tu armario actual. Prueba a añadir nuevas prendas para más combinaciones.");
            }
            return dto;
        }

        dto.setSuperior(mapearPrendaSuperior(superior));
        dto.setInferior(mapearPrendaInferior(inferior));
        dto.setCalzado(mapearPrendaCalzado(calzado));

        StringBuilder explicacion = new StringBuilder();
        explicacion.append("He combinado una parte superior, una inferior y un calzado de tu armario.");

        if (superior != null && inferior != null && Objects.equals(superior.getColor(), inferior.getColor())) {
            explicacion.append(" Los tonos de la parte superior y la inferior combinan entre sí.");
        }
        if (calzado != null && inferior != null && Objects.equals(calzado.getColor(), inferior.getColor())) {
            explicacion.append(" El calzado también mantiene la gama de colores de la parte inferior.");
        }

        dto.setMensaje(explicacion.toString());
        return dto;
    }

    private Set<String> cargarCombinacionesExistentes(Long usuarioId) {
        List<Conjunto> conjuntosUsuario = conjuntoService.buscarConjuntosPorUsuarioId(usuarioId);
        Set<String> claves = new HashSet<>();
        for (Conjunto c : conjuntosUsuario) {
            if (c.getPrendaSuperior() != null && c.getPrendaInferior() != null && c.getPrendaCalzado() != null) {
                Long supId = c.getPrendaSuperior().getId();
                Long infId = c.getPrendaInferior().getId();
                Long calId = c.getPrendaCalzado().getId();
                if (supId != null && infId != null && calId != null) {
                    claves.add(construirClave(supId, infId, calId));
                }
            }
        }
        return claves;
    }

    private String construirClave(Long supId, Long infId, Long calId) {
        return supId + "-" + infId + "-" + calId;
    }

    private PrendaSuperior elegirSuperior(List<PrendaSuperior> superiores, Long fijoId) {
        if (superiores.isEmpty()) return null;
        if (fijoId != null) {
            return superiores.stream().filter(p -> fijoId.equals(p.getId())).findFirst().orElseGet(() -> elegirAleatorio(superiores));
        }
        return elegirAleatorio(superiores);
    }

    private PrendaInferior elegirInferior(List<PrendaInferior> inferiores, Long fijoId) {
        if (inferiores.isEmpty()) return null;
        if (fijoId != null) {
            return inferiores.stream().filter(p -> fijoId.equals(p.getId())).findFirst().orElseGet(() -> elegirAleatorio(inferiores));
        }
        return elegirAleatorio(inferiores);
    }

    private PrendaCalzado elegirCalzado(List<PrendaCalzado> calzados, Long fijoId) {
        if (calzados.isEmpty()) return null;
        if (fijoId != null) {
            return calzados.stream().filter(p -> fijoId.equals(p.getId())).findFirst().orElseGet(() -> elegirAleatorio(calzados));
        }
        return elegirAleatorio(calzados);
    }

    private <T> T elegirAleatorio(List<T> lista) {
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        int index = random.nextInt(lista.size());
        return lista.get(index);
    }

    private RecomendacionPrendaDto mapearPrendaSuperior(PrendaSuperior prenda) {
        if (prenda == null) return null;
        String categoria = prenda.getCategoria() != null ? prenda.getCategoria().getEtiqueta() : null;
        return new RecomendacionPrendaDto(
                prenda.getId(),
                "SUPERIOR",
                prenda.getNombre(),
                prenda.getMarca(),
                prenda.getColor(),
                categoria,
                prenda.getDirImagen()
        );
    }

    private RecomendacionPrendaDto mapearPrendaInferior(PrendaInferior prenda) {
        if (prenda == null) return null;
        String categoria = prenda.getCategoriaInferior() != null ? prenda.getCategoriaInferior().getEtiqueta() : null;
        return new RecomendacionPrendaDto(
                prenda.getId(),
                "INFERIOR",
                prenda.getNombre(),
                prenda.getMarca(),
                prenda.getColor(),
                categoria,
                prenda.getDirImagen()
        );
    }

    private RecomendacionPrendaDto mapearPrendaCalzado(PrendaCalzado prenda) {
        if (prenda == null) return null;
        String categoria = prenda.getCategoria() != null ? prenda.getCategoria().getEtiqueta() : null;
        return new RecomendacionPrendaDto(
                prenda.getId(),
                "CALZADO",
                prenda.getNombre(),
                prenda.getMarca(),
                prenda.getColor(),
                categoria,
                prenda.getDirImagen()
        );
    }
}
