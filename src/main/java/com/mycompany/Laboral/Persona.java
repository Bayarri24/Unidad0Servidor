/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.Laboral;

/**
 *
 * @author usuario26
 */
public class Persona {

    /**Atributos*/
    public String nombre, dni;
    public char sexo;

    /**Constructor 1*/
    public Persona(String nombre, String dni, char sexo) {
        this.nombre = nombre;
        this.dni = dni;
        this.sexo = sexo;
    }

    /**Constructor 2*/
    public Persona(String nombre, char sexo) {
        this.nombre = nombre;
        this.sexo = sexo;
    }

    /**SetDni*/
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**Imprime*/
    public String imprimePersona() {
        return "Persona{" + "nombre=" + nombre + ", dni=" + dni + '}';
    }

}
