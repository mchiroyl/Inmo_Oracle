package com.inmo.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "COMPRADOR")
public class Comprador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENTE_ID")
    private Agente agente;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "APELLIDO", nullable = false, length = 100)
    private String apellido;

    @Column(name = "DIRECCION", length = 200)
    private String direccion;

    @Column(name = "TELEFONO", length = 30)
    private String telefono;

    @Column(name = "ESTADO_CIVIL", length = 30)
    private String estadoCivil; // SOLTERO, CASADO, DIVORCIADO, VIUDO

    @Column(name = "NACIONALIDAD", length = 50)
    private String nacionalidad;

    @Column(name = "EDAD")
    private Integer edad;

    @Column(name = "ACTIVO", nullable = false, length = 1)
    private String activo = "S";

    @PrePersist
    public void prePersist() {
        if (activo == null) activo = "S";
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Agente getAgente() { return agente; }
    public void setAgente(Agente agente) { this.agente = agente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getActivo() { return activo; }
    public void setActivo(String activo) { this.activo = activo; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
