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
        String tipoCombinacion = preferencias != null ? preferencias.getTipoCombinacion() : null;
        String intensidad = preferencias != null ? preferencias.getIntensidad() : null;

        RecomendacionConjuntoResponseDto dto = new RecomendacionConjuntoResponseDto();

        if (usuarioId == null) {
            dto.setMensaje("No se ha podido identificar al usuario para generar recomendaciones.");
            dto.setFaltantes(Collections.singletonList("usuario"));
            return dto;
        }

        List<Prenda> prendasUsuario = prendaService.buscarPrendasPorUsuarioId(usuarioId);

        // Lógica para COLOR y MARCA con tipoCombinacion
        boolean esModoColor = modo != null && modo.equalsIgnoreCase("COLOR");
        boolean esModoMarca = modo != null && modo.equalsIgnoreCase("MARCA");
        boolean esTodo = tipoCombinacion != null && tipoCombinacion.equalsIgnoreCase("TODO");
        boolean esCombinado = tipoCombinacion != null && tipoCombinacion.equalsIgnoreCase("COMBINADO");

        // Si es modo COLOR o MARCA con tipoCombinacion = TODO, filtrar todas las prendas
        if (esModoColor && esTodo && colorFiltro != null && !colorFiltro.isBlank()) {
            prendasUsuario = prendasUsuario.stream()
                    .filter(p -> colorFiltro.equalsIgnoreCase(p.getColor()))
                    .collect(Collectors.toList());
        } else if (esModoMarca && esTodo && marcaFiltro != null && !marcaFiltro.isBlank()) {
            String mf = marcaFiltro.toLowerCase();
            prendasUsuario = prendasUsuario.stream()
                    .filter(p -> p.getMarca() != null && p.getMarca().toLowerCase().contains(mf))
                    .collect(Collectors.toList());
        }
        // Si es modo COLOR/MARCA antiguo sin tipoCombinacion (compatibilidad)
        else if (esModoColor && colorFiltro != null && !colorFiltro.isBlank() && tipoCombinacion == null) {
            prendasUsuario = prendasUsuario.stream()
                    .filter(p -> colorFiltro.equalsIgnoreCase(p.getColor()))
                    .collect(Collectors.toList());
        } else if (esModoMarca && marcaFiltro != null && !marcaFiltro.isBlank() && tipoCombinacion == null) {
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
                // Mensaje genérico para todos los modos cuando falta algún tipo de prenda básica
                dto.setMensaje("Necesitas al menos una prenda superior, una inferior y calzado.");
            } else {
                dto.setMensaje("Aún te faltan prendas para crear conjuntos completos: " + String.join(", ", faltantes) + ".");
            }
            return dto;
        }

        // Evitar recomendar conjuntos ya existentes
        Set<String> combinacionesExistentes = cargarCombinacionesExistentes(usuarioId);

        // En modo SIN_REPETIR y SORPRESA, también evitamos las combinaciones ya propuestas en esta sesión
        if (modo != null && (modo.equalsIgnoreCase("SIN_REPETIR") || modo.equalsIgnoreCase("SORPRESA"))
                && conjuntosUsados != null && !conjuntosUsados.isEmpty()) {
            combinacionesExistentes = new HashSet<>(combinacionesExistentes);
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

            PrendaSuperior candidatoSup;
            PrendaInferior candidatoInf;
            PrendaCalzado candidatoCal;

            // Lógica especial para modo COMBINADO (COLOR o MARCA)
            if ((esModoColor || esModoMarca) && esCombinado && intensidad != null) {
                boolean esProtagonista = intensidad.equalsIgnoreCase("PROTAGONISTA");
                boolean esToque = intensidad.equalsIgnoreCase("TOQUE");

                List<PrendaSuperior> superioresDelCriterio = new ArrayList<>();
                List<PrendaInferior> inferioresDelCriterio = new ArrayList<>();
                List<PrendaCalzado> calzadosDelCriterio = new ArrayList<>();

                List<PrendaSuperior> superioresOtros = new ArrayList<>();
                List<PrendaInferior> inferioresOtros = new ArrayList<>();
                List<PrendaCalzado> calzadosOtros = new ArrayList<>();

                // Separar prendas del criterio vs otras
                for (PrendaSuperior s : superiores) {
                    boolean cumpleCriterio = false;
                    if (esModoColor && colorFiltro != null) {
                        cumpleCriterio = colorFiltro.equalsIgnoreCase(s.getColor());
                    } else if (esModoMarca && marcaFiltro != null) {
                        cumpleCriterio = s.getMarca() != null && s.getMarca().toLowerCase().contains(marcaFiltro.toLowerCase());
                    }
                    if (cumpleCriterio) {
                        superioresDelCriterio.add(s);
                    } else {
                        superioresOtros.add(s);
                    }
                }

                for (PrendaInferior i : inferiores) {
                    boolean cumpleCriterio = false;
                    if (esModoColor && colorFiltro != null) {
                        cumpleCriterio = colorFiltro.equalsIgnoreCase(i.getColor());
                    } else if (esModoMarca && marcaFiltro != null) {
                        cumpleCriterio = i.getMarca() != null && i.getMarca().toLowerCase().contains(marcaFiltro.toLowerCase());
                    }
                    if (cumpleCriterio) {
                        inferioresDelCriterio.add(i);
                    } else {
                        inferioresOtros.add(i);
                    }
                }

                for (PrendaCalzado c : calzados) {
                    boolean cumpleCriterio = false;
                    if (esModoColor && colorFiltro != null) {
                        cumpleCriterio = colorFiltro.equalsIgnoreCase(c.getColor());
                    } else if (esModoMarca && marcaFiltro != null) {
                        cumpleCriterio = c.getMarca() != null && c.getMarca().toLowerCase().contains(marcaFiltro.toLowerCase());
                    }
                    if (cumpleCriterio) {
                        calzadosDelCriterio.add(c);
                    } else {
                        calzadosOtros.add(c);
                    }
                }

                if (esProtagonista) {
                    // PROTAGONISTA: intentar usar 2 prendas del criterio
                    int prendasDelCriterio = 0;
                    candidatoSup = elegirSuperior(superioresDelCriterio.isEmpty() ? superioresOtros : superioresDelCriterio, superiorFijoId);
                    if (candidatoSup != null && !superioresDelCriterio.isEmpty()) prendasDelCriterio++;

                    candidatoInf = elegirInferior(inferioresDelCriterio.isEmpty() ? inferioresOtros : inferioresDelCriterio, inferiorFijoId);
                    if (candidatoInf != null && !inferioresDelCriterio.isEmpty()) prendasDelCriterio++;

                    // Si ya tenemos 2, el calzado es de otros; si no, intentar que sea del criterio
                    if (prendasDelCriterio >= 2) {
                        candidatoCal = elegirCalzado(calzadosOtros.isEmpty() ? calzadosDelCriterio : calzadosOtros, calzadoFijoId);
                    } else {
                        candidatoCal = elegirCalzado(calzadosDelCriterio.isEmpty() ? calzadosOtros : calzadosDelCriterio, calzadoFijoId);
                    }
                } else if (esToque) {
                    // TOQUE: usar exactamente 1 prenda del criterio
                    // Elegir aleatoriamente qué tipo será del criterio
                    int tipoDelCriterio = random.nextInt(3); // 0=superior, 1=inferior, 2=calzado

                    if (tipoDelCriterio == 0 && !superioresDelCriterio.isEmpty()) {
                        candidatoSup = elegirSuperior(superioresDelCriterio, superiorFijoId);
                        candidatoInf = elegirInferior(inferioresOtros.isEmpty() ? inferioresDelCriterio : inferioresOtros, inferiorFijoId);
                        candidatoCal = elegirCalzado(calzadosOtros.isEmpty() ? calzadosDelCriterio : calzadosOtros, calzadoFijoId);
                    } else if (tipoDelCriterio == 1 && !inferioresDelCriterio.isEmpty()) {
                        candidatoSup = elegirSuperior(superioresOtros.isEmpty() ? superioresDelCriterio : superioresOtros, superiorFijoId);
                        candidatoInf = elegirInferior(inferioresDelCriterio, inferiorFijoId);
                        candidatoCal = elegirCalzado(calzadosOtros.isEmpty() ? calzadosDelCriterio : calzadosOtros, calzadoFijoId);
                    } else if (!calzadosDelCriterio.isEmpty()) {
                        candidatoSup = elegirSuperior(superioresOtros.isEmpty() ? superioresDelCriterio : superioresOtros, superiorFijoId);
                        candidatoInf = elegirInferior(inferioresOtros.isEmpty() ? inferioresDelCriterio : inferioresOtros, inferiorFijoId);
                        candidatoCal = elegirCalzado(calzadosDelCriterio, calzadoFijoId);
                    } else {
                        // Fallback: si no hay suficientes prendas del criterio en la categoría elegida
                        candidatoSup = elegirSuperior(superiores, superiorFijoId);
                        candidatoInf = elegirInferior(inferiores, inferiorFijoId);
                        candidatoCal = elegirCalzado(calzados, calzadoFijoId);
                    }
                } else {
                    // Fallback por si intensidad no está definida correctamente
                    candidatoSup = elegirSuperior(superiores, superiorFijoId);
                    candidatoInf = elegirInferior(inferiores, inferiorFijoId);
                    candidatoCal = elegirCalzado(calzados, calzadoFijoId);
                }
            } else {
                // Lógica normal para otros modos
                candidatoSup = elegirSuperior(superiores, superiorFijoId);
                candidatoInf = elegirInferior(inferiores, inferiorFijoId);
                candidatoCal = elegirCalzado(calzados, calzadoFijoId);
            }

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
            // Caso especial: modo COLOR o MARCA con tipoCombinacion TODO
            if ((esModoColor || esModoMarca) && esTodo) {
                dto.setMensaje("No he podido generar un conjunto completo con ese criterio.");
            } else if (modo != null && modo.equalsIgnoreCase("SIN_REPETIR")) {
                dto.setMensaje("Ya no quedan más combinaciones diferentes con tus prendas y los filtros actuales.");
            } else if (modo != null && modo.equalsIgnoreCase("SORPRESA")) {
                dto.setMensaje("No existen más combinaciones posibles, creo que ya es hora de ir de compras.");
            } else {
                dto.setMensaje("Ya tienes guardados todos los conjuntos posibles con tu armario actual. Prueba a añadir nuevas prendas para más combinaciones.");
            }
            return dto;
        }

        dto.setSuperior(mapearPrendaSuperior(superior));
        dto.setInferior(mapearPrendaInferior(inferior));
        dto.setCalzado(mapearPrendaCalzado(calzado));

        StringBuilder explicacion = new StringBuilder();
        if (modo != null && modo.equalsIgnoreCase("SORPRESA")) {
            explicacion.append("Sorpresa inteligente: combinación equilibrada y variada");
        } else {
            explicacion.append("He combinado una parte superior, una inferior y un calzado de tu armario.");
            if (superior != null && inferior != null && Objects.equals(superior.getColor(), inferior.getColor())) {
                explicacion.append(" Los tonos de la parte superior y la inferior combinan entre sí.");
            }
            if (calzado != null && inferior != null && Objects.equals(calzado.getColor(), inferior.getColor())) {
                explicacion.append(" El calzado también mantiene la gama de colores de la parte inferior.");
            }
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
