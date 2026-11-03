package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;

import co.uniquindio.edu.sga.domain.valueobject.Reporte;
import co.uniquindio.edu.sga.application.dto.response.IndicadoresConsolidadosResponse;
import co.uniquindio.edu.sga.application.dto.response.ReporteOcupacionResponse;


/** Convierte el valor de dominio Reporte al formato JSON de la API. */
@Mapper(componentModel = "spring")
public interface ReporteMapper {
    default IndicadoresConsolidadosResponse aIndicadoresConsolidados(Reporte reporte) {
        return new IndicadoresConsolidadosResponse(reporte.periodo().fechaInicio(), reporte.periodo().fechaFin(),
                valorMonetario(reporte, "ingresosTotales"), valor(reporte, "ocupacionGlobal"),
                valor(reporte, "reservasTotales").longValue(), valor(reporte, "reservasActivas").longValue(),
                valor(reporte, "reservasCanceladas").longValue());
    }

    default ReporteOcupacionResponse aOcupacion(Reporte reporte) {
        return new ReporteOcupacionResponse(reporte.periodo().fechaInicio(), reporte.periodo().fechaFin(),
                valor(reporte, "porcentajeOcupacion"), valor(reporte, "nochesVendidas").longValue(),
                valor(reporte, "nochesDisponibles").longValue(), valor(reporte, "apartamentosIncluidos").intValue());
    }

    private static java.math.BigDecimal valor(Reporte reporte, String indicador) {
        return reporte.indicadores().getOrDefault(indicador, java.math.BigDecimal.ZERO);
    }

    private static java.math.BigDecimal valorMonetario(Reporte reporte, String indicador) {
        return reporte.indicadores().getOrDefault(indicador, java.math.BigDecimal.ZERO);
    }
}
