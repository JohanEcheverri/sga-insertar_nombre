package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;

import co.uniquindio.edu.sga.domain.valueobject.Reporte;
import co.uniquindio.edu.sga.application.dto.response.IndicadoresConsolidadosResponse;
import co.uniquindio.edu.sga.application.dto.response.ReporteOcupacionResponse;

import java.util.Map;

/** Convierte el valor de dominio Reporte al formato JSON de la API. */
@Mapper(componentModel = "spring")
public interface ReporteMapper {
    default IndicadoresConsolidadosResponse aIndicadoresConsolidados(Reporte reporte) {
        return new IndicadoresConsolidadosResponse(reporte.periodo().fechaInicio(), reporte.periodo().fechaFin(),
                valorMonetario(reporte, "ingresosTotales"), valor(reporte, "ocupacionGlobal"),
                (long) valor(reporte, "reservasTotales"), (long) valor(reporte, "reservasActivas"),
                (long) valor(reporte, "reservasCanceladas"));
    }

    default ReporteOcupacionResponse aOcupacion(Reporte reporte) {
        return new ReporteOcupacionResponse(reporte.periodo().fechaInicio(), reporte.periodo().fechaFin(),
                valor(reporte, "porcentajeOcupacion"), (long) valor(reporte, "nochesVendidas"),
                (long) valor(reporte, "nochesDisponibles"), (int) valor(reporte, "apartamentosIncluidos"));
    }

    private static double valor(Reporte reporte, String indicador) {
        return reporte.indicadores().getOrDefault(indicador, java.math.BigDecimal.ZERO).doubleValue();
    }

    private static java.math.BigDecimal valorMonetario(Reporte reporte, String indicador) {
        return reporte.indicadores().getOrDefault(indicador, java.math.BigDecimal.ZERO);
    }
}
