/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.Laboral;

/**
 *
 * @author usuario26
 */
public class Nomina {

    private static final int SUELDO_BASE[] = {50000, 70000, 90000, 110000, 130000, 150000, 170000, 190000, 210000, 230000};

    public int sueldo(Empleado e) {
        int sueldoTotal;
        return sueldoTotal = SUELDO_BASE[e.getCategoria()] + 5000 * e.anyosTrabajados;
    }

}
