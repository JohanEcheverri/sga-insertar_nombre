package co.uniquindio.edu.sga.infraestructure.config;

import co.uniquindio.edu.sga.domain.repository.BloqueoRepository;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;
import co.uniquindio.edu.sga.domain.service.CancelacionReservaService;
import co.uniquindio.edu.sga.domain.service.CotizadorEstanciaService;
import co.uniquindio.edu.sga.domain.service.DisponibilidadApartamentoService;
import co.uniquindio.edu.sga.domain.service.EntregaApartamentoService;
import co.uniquindio.edu.sga.domain.service.GestionBloqueosMantenimientoService;
import co.uniquindio.edu.sga.domain.service.LiquidacionSalidaService;
import co.uniquindio.edu.sga.domain.service.NoShowProcesamientoServicio;
import co.uniquindio.edu.sga.domain.service.ValidacionAforoOcupantesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registra servicios de dominio compartidos por los casos de uso. */
@Configuration(proxyBeanMethods = false)
public class DomainConfig {
    @Bean public CancelacionReservaService cancelacionReservaService() { return new CancelacionReservaService(); }
    @Bean public CotizadorEstanciaService cotizadorEstanciaService() { return new CotizadorEstanciaService(); }
    @Bean public EntregaApartamentoService entregaApartamentoService() { return new EntregaApartamentoService(); }
    @Bean public GestionBloqueosMantenimientoService gestionBloqueosMantenimientoService(ReservaRepository reservas) { return new GestionBloqueosMantenimientoService(reservas); }
    @Bean public DisponibilidadApartamentoService disponibilidadApartamentoService(ReservaRepository reservas, BloqueoRepository bloqueos) { return new DisponibilidadApartamentoService(reservas, bloqueos); }
    @Bean public LiquidacionSalidaService liquidacionSalidaService() { return new LiquidacionSalidaService(); }
    @Bean public NoShowProcesamientoServicio noShowProcesamientoServicio() { return new NoShowProcesamientoServicio(); }
    @Bean public ValidacionAforoOcupantesService validacionAforoOcupantesService() { return new ValidacionAforoOcupantesService(); }
}
