package com.inmo.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ACUERDO")
public class Acuerdo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INMUEBLE_ID", nullable = false)
    private Inmueble inmueble;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPRADOR_ID", nullable = false)
    private Comprador comprador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VENDEDOR_ID", nullable = false)
    private Vendedor vendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENTE_ID")
    private Agente agente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFERTA_ID")
    private Oferta oferta;

    @Column(name = "MONTO_FINAL", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoFinal;

    @Column(name = "FORMA_PAGO", length = 50)
    private String formaPago;

    @Column(name = "TIEMPO_PAGO")
    private Integer tiempoPago; // En meses

    @Column(name = "ESTADO", length = 30)
    private String estado = "PENDIENTE"; // PENDIENTE, EN_PROCESO, COMPLETADO, CANCELADO

    @Column(name = "FECHA_ACUERDO")
    private OffsetDateTime fechaAcuerdo;

    @Column(name = "FECHA_CIERRE")
    private OffsetDateTime fechaCierre;

    @Column(name = "NOTAS", length = 1000)
    private String notas;

    @PrePersist
    public void prePersist() {
        if (fechaAcuerdo == null) fechaAcuerdo = OffsetDateTime.now();
        if (estado == null) estado = "PENDIENTE";
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Inmueble getInmueble() { return inmueble; }
    public void setInmueble(Inmueble inmueble) { this.inmueble = inmueble; }

    public Comprador getComprador() { return comprador; }
    public void setComprador(Comprador comprador) { this.comprador = comprador; }

    public Vendedor getVendedor() { return vendedor; }
    public void setVendedor(Vendedor vendedor) { this.vendedor = vendedor; }

    public Agente getAgente() { return agente; }
    public void setAgente(Agente agente) { this.agente = agente; }

    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public BigDecimal getMontoFinal() { return montoFinal; }
    public void setMontoFinal(BigDecimal montoFinal) { this.montoFinal = montoFinal; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public Integer getTiempoPago() { return tiempoPago; }
    public void setTiempoPago(Integer tiempoPago) { this.tiempoPago = tiempoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public OffsetDateTime getFechaAcuerdo() { return fechaAcuerdo; }
    public void setFechaAcuerdo(OffsetDateTime fechaAcuerdo) { this.fechaAcuerdo = fechaAcuerdo; }

    public OffsetDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(OffsetDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
