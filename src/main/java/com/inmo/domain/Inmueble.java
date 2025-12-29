package com.inmo.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "INMUEBLE")
public class Inmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VENDEDOR_ID", nullable = false)
    private Vendedor vendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENTE_ID")
    private Agente agente;

    @Column(name = "DIRECCION", nullable = false, length = 200)
    private String direccion;

    @Column(name = "TIPO", nullable = false, length = 50)
    private String tipo; // CASA, DEPARTAMENTO, TERRENO, LOCAL, OFICINA

    @Column(name = "PRECIO", nullable = false, precision = 15, scale = 2)
    private BigDecimal precio;

    @Column(name = "METRAJE_M2", precision = 10, scale = 2)
    private BigDecimal metraje;

    @Column(name = "ANTIGUEDAD")
    private Integer antiguedad;

    @Column(name = "MODELO", length = 100)
    private String modelo;

    @Column(name = "MATERIAL", length = 100)
    private String material;

    @Column(name = "CONDICION", length = 50)
    private String condicion; // NUEVO, SEMI_NUEVO, USADO, REMODELADO

    @Column(name = "HABITACIONES")
    private Integer habitaciones;

    @Column(name = "BANOS")
    private Integer banos;

    @Column(name = "ESTACIONAMIENTOS")
    private Integer estacionamientos;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    @Column(name = "ESTADO", length = 30)
    private String estado = "DISPONIBLE"; // DISPONIBLE, EN_NEGOCIACION, VENDIDO, RETIRADO

    @Column(name = "CREADO_EN")
    private OffsetDateTime creadoEn;

    @Column(name = "ACTUALIZADO_EN")
    private OffsetDateTime actualizadoEn;

    @PrePersist
    public void prePersist() {
        if (creadoEn == null) creadoEn = OffsetDateTime.now();
        if (estado == null) estado = "DISPONIBLE";
    }

    @PreUpdate
    public void preUpdate() {
        actualizadoEn = OffsetDateTime.now();
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Vendedor getVendedor() { return vendedor; }
    public void setVendedor(Vendedor vendedor) { this.vendedor = vendedor; }

    public Agente getAgente() { return agente; }
    public void setAgente(Agente agente) { this.agente = agente; }

    public BigDecimal getMetraje() { return metraje; }
    public void setMetraje(BigDecimal metraje) { this.metraje = metraje; }

    public Integer getAntiguedad() { return antiguedad; }
    public void setAntiguedad(Integer antiguedad) { this.antiguedad = antiguedad; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getCondicion() { return condicion; }
    public void setCondicion(String condicion) { this.condicion = condicion; }

    public Integer getHabitaciones() { return habitaciones; }
    public void setHabitaciones(Integer habitaciones) { this.habitaciones = habitaciones; }

    public Integer getBanos() { return banos; }
    public void setBanos(Integer banos) { this.banos = banos; }

    public Integer getEstacionamientos() { return estacionamientos; }
    public void setEstacionamientos(Integer estacionamientos) { this.estacionamientos = estacionamientos; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }

    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(OffsetDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
