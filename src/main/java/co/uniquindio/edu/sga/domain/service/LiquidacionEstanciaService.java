package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.valueobject.Ocupante;
import co.uniquindio.edu.sga.domain.entity.Temporada;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.Dinero;
import co.uniquindio.edu.sga.domain.valueobject.Estancia;
import co.uniquindio.edu.sga.domain.valueobject.LiquidacionEstancia;
import co.uniquindio.edu.sga.domain.valueobject.NocheLiquidada;
import co.uniquindio.edu.sga.domain.valueobject.Tarifa;
import co.uniquindio.edu.sga.domain.valueobject.ConfiguracionAlojamiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Servicio de dominio (Guía 05) */
public class LiquidacionEstanciaService {

    // L-09: el umbral es configuración del alojamiento, llega por configuración
    private final ConfiguracionAlojamiento configuracion;

    public LiquidacionEstanciaService(ConfiguracionAlojamiento configuracion) {
        if (configuracion == null) {
            throw new ReglaDominioException("La configuración del alojamiento es obligatoria.");
        }
        this.configuracion = configuracion;
    }

    public LiquidacionEstancia liquidar(Apartamento apartamento, Estancia estancia,
                                        List<Ocupante> ocupantes, List<Temporada> temporadas) {
        int facturables = (int) ocupantes.stream()
                .filter(ocupante -> ocupante.esFacturableEn(estancia, configuracion.umbralEdadFacturable()))
                .count();

        List<NocheLiquidada> noches = new ArrayList<>();
        for (LocalDate noche : estancia.nochesOcupadas()) {
            Temporada temporada = temporadaDe(noche, temporadas);
            // 7.5 condición 3: el apartamento debe tener tarifa para cada temporada que toque
            Tarifa tarifa = apartamento.tarifaEn(temporada.getId())
                    .orElseThrow(() -> new ReglaDominioException(
                            "El apartamento no tiene tarifa definida para la temporada " + temporada.getNombre() + "."));
            Dinero subtotal = tarifa.precioPorNochePorOcupante().multiplicarPor(facturables);
            noches.add(new NocheLiquidada(noche, temporada.getId().toString(), tarifa.precioPorNochePorOcupante(),
                    facturables, subtotal));
        }
        return new LiquidacionEstancia(noches);
    }

    // F-05: una temporada específica prevalece; si ninguna cubre la noche, rige la temporada base
    private Temporada temporadaDe(LocalDate noche, List<Temporada> temporadas) {
        return temporadas.stream()
                .filter(temporada -> !temporada.esBase() && temporada.cubreFecha(noche))
                .findFirst()
                .or(() -> temporadas.stream().filter(Temporada::esBase).findFirst())
                .orElseThrow(() -> new ReglaDominioException(
                        "No existe una temporada que cubra la noche " + noche + "."));
    }
}