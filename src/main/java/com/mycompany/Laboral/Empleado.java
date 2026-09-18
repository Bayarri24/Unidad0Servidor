package com.mycompany.Laboral;

import com.mycompany.Laboral.Exceptions.DatosNoCorrectosException;

public class Empleado extends Persona {

    private int categoria;
    public int anyosTrabajados;

    public Empleado(int categoria, int anyosTrabajados, String nombre, String dni, char sexo) throws DatosNoCorrectosException {
        super(nombre, dni, sexo);

        //Imponemos la condicion 
        if (categoria < 1 || categoria > 10 || anyosTrabajados < 0) {
            throw new DatosNoCorrectosException();
        }
        this.categoria = categoria;
        this.anyosTrabajados = anyosTrabajados;
    }

    public Empleado(String nombre, String dni, char sexo) throws DatosNoCorrectosException {
        super(nombre, dni, sexo);
        categoria = 1;
        anyosTrabajados = 0;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public void incrAnyo() {
        anyosTrabajados++;
    }

    public void imprimeEmpleado() {
        System.out.println("El empleado " + nombre + " con DNI " + dni
                + " y de sexo " + sexo + " es de categoria " + categoria
                + " y lleva trabajando " + anyosTrabajados
                + " en la empresa");
    }
}
