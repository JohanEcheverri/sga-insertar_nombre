package co.uniquindio.edu.sga.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** Resumen contable del folio asociado a una reserva.
 * @param id Identificador del folio.
 * @param reservaId Identificador de la reserva propietaria del folio.
 * @param estado Estado actual del folio.
 * @param cargos Cargos registrados en el folio.
 * @param cantidadPagos Cantidad de pagos registrados.
 * @param totalCargos Suma monetaria de los cargos.
 * @param totalPagos Suma monetaria de los pagos.
 * @param saldoPendiente Saldo con signo: positivo es deuda, negativo es saldo a favor.
 * @param autorizacionCierre Datos de la autorización de cierre, si existe.
 * @author Equipo SGA
 * @version 1.0
 */
public record FolioResponse(
        String id,
        String reservaId,
        String estado,
        List<CargoResponse> cargos,
        List<PagoResponse> pagos,
        int cantidadPagos,
        BigDecimal totalCargos,
        BigDecimal totalPagos,
        BigDecimal saldoPendiente,
        BigDecimal saldoAFavor,
        AutorizacionCierreResponse autorizacionCierre) { }
