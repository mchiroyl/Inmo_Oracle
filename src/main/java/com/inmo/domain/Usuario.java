package com.inmo.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "USUARIO")
public class Usuario {

    public static final String ROL_ADMIN     = "ADMIN";
    public static final String ROL_AGENTE    = "AGENTE";
    public static final String ROL_VENDEDOR  = "VENDEDOR";
    public static final String ROL_COMPRADOR = "COMPRADOR";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "HASH_PASSWORD", length = 255, nullable = false)
    private String hashPassword;

    @Column(name = "ROL", length = 20, nullable = false)
    private String rol;

    @Column(name = "ACTIVO", nullable = false)
    private Character activo = 'S'; // 'S' o 'N'

    @Column(name = "CREADO_EN")
    private OffsetDateTime creadoEn;

    @Column(name = "ACTUALIZADO_EN")
    private OffsetDateTime actualizadoEn;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim(); }

    public String getHashPassword() { return hashPassword; }
    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Character getActivo() { return activo; }
    public void setActivo(Character activo) { this.activo = activo; }

    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }

    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(OffsetDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }

    // Helpers
    public boolean isAdmin()     { return ROL_ADMIN.equalsIgnoreCase(rol); }
    public boolean isAgente()    { return ROL_AGENTE.equalsIgnoreCase(rol); }
    public boolean isVendedor()  { return ROL_VENDEDOR.equalsIgnoreCase(rol); }
    public boolean isComprador() { return ROL_COMPRADOR.equalsIgnoreCase(rol); }
    public String getActivoSN()  { return (activo != null && activo == 'S') ? "S" : "N"; }
}
