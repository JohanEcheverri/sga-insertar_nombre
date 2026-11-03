package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Folio")
@Getter
@Setter
@NoArgsConstructor
public class FolioEntity {

    @Id
    private String id;

    @Column(name = "reserva_id", nullable = false, length = 15)
    private String reservaId;

    @Column(name = "saldo_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoTotal;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    @jakarta.persistence.ElementCollection
    @jakarta.persistence.CollectionTable(name="folio_cargos", joinColumns=@jakarta.persistence.JoinColumn(name="folio_id"))
    private java.util.List<CargoEmbeddable> cargos = new java.util.ArrayList<>();
    @jakarta.persistence.ElementCollection
    @jakarta.persistence.CollectionTable(name="folio_pagos", joinColumns=@jakarta.persistence.JoinColumn(name="folio_id"))
    private java.util.List<PagoEmbeddable> pagos = new java.util.ArrayList<>();
    private String autorizacionAutor;
    private String autorizacionMotivo;
    private LocalDateTime autorizacionFecha;
}
