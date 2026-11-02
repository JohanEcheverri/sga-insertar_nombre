package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.valueobject.Reporte;
import co.uniquindio.edu.sga.application.dto.response.IndicadoresConsolidadosResponse;
import co.uniquindio.edu.sga.application.dto.response.ReporteOcupacionResponse;

import java.util.Map;

/** Convierte el valor de dominio Reporte al formato JSON de la API. */
public interface ReporteMapper {
    static IndicadoresConsolidadosResponse aIndicadoresConsolidados(Reporte reporte) {
        return new IndicadoresConsolidadosResponse(reporte.periodo().fechaInicio(), reporte.periodo().fechaFin(),
                valor(reporte, "ingresosTotales"), valor(reporte, "ocupacionGlobal"),
                (long) valor(reporte, "reservasTotales"), (long) valor(reporte, "reservasActivas"),
                (long) valor(reporte, "reservasCanceladas"));
    }

    static ReporteOcupacionResponse aOcupacion(Reporte reporte) {
        return new ReporteOcupacionResponse(reporte.periodo().fechaInicio(), reporte.periodo().fechaFin(),
                valor(reporte, "porcentajeOcupacion"), (long) valor(reporte, "nochesVendidas"),
                (long) valor(reporte, "nochesDisponibles"), (int) valor(reporte, "apartamentosIncluidos"));
    }

    private static double valor(Reporte reporte, String indicador) {
        return reporte.indicadores().getOrDefault(indicador, java.math.BigDecimal.ZERO).doubleValue();
    }
}
