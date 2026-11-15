package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.entity.Alojamiento;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.Dinero;
import co.uniquindio.edu.sga.domain.valueobject.Estancia;
import co.uniquindio.edu.sga.domain.valueobject.FechaNacimiento;
import co.uniquindio.edu.sga.domain.valueobject.Tarifa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * RN-05 / RN-06 - Cotiza una estancia noche por noche: tarifa vigente del apartamento en la
 * temporada de cada noche × ocupantes facturables. No necesita repositorios: recibe el agregado
 * Alojamiento (que ya contiene temporadas y tarifas).
 */
public class CotizadorEstanciaService {

    /** Una noche del desglose (7.4): fecha, temporada, tarifa, ocupantes facturables y subtotal. */
    public record Linea(LocalDate noche, String temporada, Dinero tarifa,
                        int ocupantesFacturables, BigDecimal subtotal) { }

    public record Cotizacion(List<Linea> lineas, int ocupantesFacturables, Dinero total) { }

    public Cotizacion cotizar(Alojamiento alojamiento, ApartamentoId apartamentoId, Estancia estancia,
                              List<FechaNacimiento> fechasNacimiento) {
        if (alojamiento == null || apartamentoId == null || estancia == null || fechasNacimiento == null) {
            throw new ReglaDominioException("Para cotizar se requiere alojamiento, apartamento, estancia y ocupantes.");
        }
        int facturables = contarFacturables(fechasNacimiento, estancia.fechaEntrada(),
                alojamiento.getConfiguracion().umbralEdadFacturable());
        if (facturables == 0) {
            throw new ReglaDominioException("La estancia debe tener al menos un ocupante facturable.");
        }

        List<Linea> lineas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (LocalDate noche = estancia.fechaEntrada(); noche.isBefore(estancia.fechaSalida()); noche = noche.plusDays(1)) {
            Tarifa tarifa = alojamiento.tarifaAplicable(apartamentoId, noche);
            BigDecimal subtotal = tarifa.calcularValorNoche(facturables);
            lineas.add(new Linea(noche, alojamiento.temporadaAplicable(noche).getNombre(),
                    tarifa.precioPorNochePorOcupante(), facturables, subtotal));
            total = total.add(subtotal);
        }
        // 3.4 - el redondeo al peso se aplica una sola vez, al final, nunca noche por noche.
        BigDecimal redondeado = total.setScale(0, RoundingMode.HALF_UP);
        return new Cotizacion(List.copyOf(lineas), facturables, new Dinero(redondeado));
    }

    /** RN-06 - Facturable si a la fecha de entrada alcanza el umbral. */
    public int contarFacturables(List<FechaNacimiento> fechasNacimiento, LocalDate fechaEntrada, int umbral) {
        return (int) fechasNacimiento.stream().filter(f -> f.edadEn(fechaEntrada) >= umbral).count();
    }
}
