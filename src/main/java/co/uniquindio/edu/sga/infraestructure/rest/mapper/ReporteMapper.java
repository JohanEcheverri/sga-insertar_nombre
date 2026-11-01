package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.valueobject.Reporte;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/** Convierte el valor de dominio Reporte al formato JSON de la API. */
public interface ReporteMapper {
    static ReporteResponse aRespuesta(Reporte reporte) {
        return new ReporteResponse(reporte.periodo(), reporte.indicadores(), reporte.fechaGeneracion());
    }

    record ReporteResponse(RangoFechas periodo, Map<String, BigDecimal> indicadores,
                           LocalDateTime fechaGeneracion) { }
}
