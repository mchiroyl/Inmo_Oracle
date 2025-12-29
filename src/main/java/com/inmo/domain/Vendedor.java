package com.inmo.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "VENDEDOR")
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "EMAIL", length = 120)
    private String email;

    @Column(name = "TELEFONO", length = 30)
    private String telefono;

    @Column(name = "ACTIVO", nullable = false, length = 1)
    private String activo = "S";

    // (opcional) vínculo a USUARIO con rol=VENDEDOR
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID",
            foreignKey = @ForeignKey(name = "FK_VENDEDOR_USUARIO"))
    private Usuario usuario;

    // NUEVO: vínculo al AGENTE responsable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENTE_ID",
            foreignKey = @ForeignKey(name = "FK_VENDEDOR_AGENTE"))
    private Agente agente;

    @PrePersist
    public void prePersist() {
        if (activo == null) activo = "S";
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getActivo() { return activo; }
    public void setActivo(String activo) { this.activo = activo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Agente getAgente() { return agente; }
    public void setAgente(Agente agente) { this.agente = agente; }
}
