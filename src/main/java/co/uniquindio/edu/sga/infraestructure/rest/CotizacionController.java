package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.usecase.CotizarEstanciaUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Consulta de tarifas sin crear una reserva. */
@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {
    private final CotizarEstanciaUseCase cotizarEstancia;

    public CotizacionController(CotizarEstanciaUseCase cotizarEstancia) {
        this.cotizarEstancia = cotizarEstancia;
    }

    @GetMapping
    public CotizarEstanciaUseCase.Result cotizar(
            @RequestParam UUID apartamentoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> fechasNacimiento) {
        return cotizarEstancia.ejecutar(new CotizarEstanciaUseCase.Command(apartamentoId, fechaEntrada, fechaSalida, fechasNacimiento));
    }
}
