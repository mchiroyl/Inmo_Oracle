package com.inmo.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "CONTRAOFERTA")
public class Contraoferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFERTA_ID", nullable = false)
    private Oferta oferta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VENDEDOR_ID", nullable = false)
    private Vendedor vendedor;

    @Column(name = "MONTO", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "FORMA_PAGO", length = 50)
    private String formaPago; // EFECTIVO, FINANCIAMIENTO, MIXTO, CREDITO

    @Column(name = "TIEMPO_PAGO")
    private Integer tiempoPago; // En meses

    @Column(name = "ESTADO", length = 30)
    private String estado = "PENDIENTE"; // PENDIENTE, ACEPTADA, RECHAZADA, CANCELADA

    @Column(name = "COMENTARIOS", length = 500)
    private String comentarios;

    @Column(name = "FECHA_CONTRAOFERTA")
    private OffsetDateTime fechaContraoferta;

    @Column(name = "FECHA_RESPUESTA")
    private OffsetDateTime fechaRespuesta;

    @Column(name = "CREADO_EN")
    private OffsetDateTime creadoEn;

    @Column(name = "ACTUALIZADO_EN")
    private OffsetDateTime actualizadoEn;

    @PrePersist
    public void prePersist() {
        if (creadoEn == null) creadoEn = OffsetDateTime.now();
        if (fechaContraoferta == null) fechaContraoferta = OffsetDateTime.now();
        if (estado == null) estado = "PENDIENTE";
    }

    @PreUpdate
    public void preUpdate() {
        actualizadoEn = OffsetDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public Vendedor getVendedor() { return vendedor; }
    public void setVendedor(Vendedor vendedor) { this.vendedor = vendedor; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public Integer getTiempoPago() { return tiempoPago; }
    public void setTiempoPago(Integer tiempoPago) { this.tiempoPago = tiempoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public OffsetDateTime getFechaContraoferta() { return fechaContraoferta; }
    public void setFechaContraoferta(OffsetDateTime fechaContraoferta) { this.fechaContraoferta = fechaContraoferta; }

    public OffsetDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(OffsetDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }

    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }

    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(OffsetDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
