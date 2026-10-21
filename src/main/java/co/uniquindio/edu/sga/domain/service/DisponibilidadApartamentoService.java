package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.repository.BloqueoRepository;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;

public class DisponibilidadApartamentoService {

    private final ReservaRepository reservaRepository;
    private final BloqueoRepository bloqueoRepository;

    public DisponibilidadApartamentoService(ReservaRepository reservaRepository, BloqueoRepository bloqueoRepository) {
        this.reservaRepository = reservaRepository;
        this.bloqueoRepository = bloqueoRepository;
    }

    ;
}
