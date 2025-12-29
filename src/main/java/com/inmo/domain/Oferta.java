package com.inmo.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "OFERTA")
public class Oferta {

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
    @JoinColumn(name = "AGENTE_ID")
    private Agente agente;

    @Column(name = "MONTO", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "ESTADO", length = 30)
    private String estado = "PENDIENTE"; // PENDIENTE, ACEPTADA, RECHAZADA, CONTRAOFERTADA, CANCELADA

    @Column(name = "PRIORIDAD")
    private Integer prioridad = 5; // 1=Mayor prioridad, 10=Menor

    @Column(name = "COMENTARIOS", length = 500)
    private String comentarios;

    @PrePersist
    public void prePersist() {
        if (estado == null) estado = "PENDIENTE";
        if (prioridad == null) prioridad = 5;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Inmueble getInmueble() { return inmueble; }
    public void setInmueble(Inmueble inmueble) { this.inmueble = inmueble; }

    public Comprador getComprador() { return comprador; }
    public void setComprador(Comprador comprador) { this.comprador = comprador; }

    public Agente getAgente() { return agente; }
    public void setAgente(Agente agente) { this.agente = agente; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getPrioridad() { return prioridad; }
    public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
}
