package com.micanasta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="usuarios")
public class Usuario {

    @Id
    @Column(unique = true)
    private String dni;

    @NotNull
    private String nombre;

    @NotNull
    public String apellidoPaterno;


    @NotNull
    public String apellidoMaterno;

    @NotNull
    private String contrasena;

    @NotNull
    private String correoElectronico;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPat) {
        this.apellidoPaterno = apellidoPat;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMat) {
        this.apellidoMaterno = apellidoMat;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
}

