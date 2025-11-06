package com.nada.nada.data.services;

import com.nada.nada.data.repository.PrendaCalzadoRepository;
import com.nada.nada.data.repository.PrendaInferiorRepository;
import com.nada.nada.data.repository.PrendaRepository;
import com.nada.nada.data.repository.PrendaSuperiorRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class PrendaServiceImpl implements PrendaService {
    private final PrendaRepository prendaRepository;
    private final PrendaSuperiorRepository prendaSuperiorRepository;
    private final PrendaInferiorRepository prendaInferiorRepository;
    private final PrendaCalzadoRepository prendaCalzadoRepository;

    @Autowired
    public PrendaServiceImpl(PrendaRepository prendaRepository,
                             PrendaSuperiorRepository prendaSuperiorRepository,
                             PrendaInferiorRepository prendaInferiorRepository,
                             PrendaCalzadoRepository prendaCalzadoRepository) {
        this.prendaRepository = prendaRepository;
        this.prendaSuperiorRepository = prendaSuperiorRepository;
        this.prendaInferiorRepository = prendaInferiorRepository;
        this.prendaCalzadoRepository = prendaCalzadoRepository;
    }
}
