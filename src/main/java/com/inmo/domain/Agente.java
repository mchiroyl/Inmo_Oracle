package com.inmo.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "AGENTE",
       uniqueConstraints = {
           @UniqueConstraint(name = "UX_AGENTE_EMAIL", columnNames = {"EMAIL"})
       })
public class Agente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "TELEFONO", length = 30)
    private String telefono;

    @Column(name = "EMAIL", nullable = false, length = 120)
    private String email;

    @Column(name = "ANTIGUEDAD_EMPRESA")
    private Integer antiguedadEmpresa = 0;

    @Column(name = "CANTIDAD_VENDIDOS")
    private Integer cantidadVendidos = 0;

    @Column(name = "ACTIVO", nullable = false, length = 1)
    private String activo = "S";

    @Column(name = "CREADO_EN", nullable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "ACTUALIZADO_EN")
    private OffsetDateTime actualizadoEn;

    // Vinculación (opcional) a USUARIO con rol=AGENTE
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID",
            foreignKey = @ForeignKey(name = "FK_AGENTE_USUARIO"))
    private Usuario usuario;

    // Relación con vendedores
    @OneToMany(mappedBy = "agente", fetch = FetchType.LAZY)
    private List<Vendedor> vendedores;

    @PrePersist
    public void prePersist() {
        if (creadoEn == null) creadoEn = OffsetDateTime.now();
        if (activo == null) activo = "S";
    }

    @PreUpdate
    public void preUpdate() {
        actualizadoEn = OffsetDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getActivo() { return activo; }
    public void setActivo(String activo) { this.activo = activo; }

    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }

    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(OffsetDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Vendedor> getVendedores() { return vendedores; }
    public void setVendedores(List<Vendedor> vendedores) { this.vendedores = vendedores; }

    public Integer getAntiguedadEmpresa() { return antiguedadEmpresa; }
    public void setAntiguedadEmpresa(Integer antiguedadEmpresa) { this.antiguedadEmpresa = antiguedadEmpresa; }

    public Integer getCantidadVendidos() { return cantidadVendidos; }
    public void setCantidadVendidos(Integer cantidadVendidos) { this.cantidadVendidos = cantidadVendidos; }
}
