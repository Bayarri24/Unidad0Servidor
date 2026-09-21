/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.Laboral;

import com.mycompany.Laboral.Exceptions.DatosNoCorrectosException;

/**
 *
 * @author usuario26
 */
public class CalculaNominas {

    //Objeto nomina
    Nomina n = new Nomina();
    
    private void escribe(Empleado e1, Empleado e2) {
        System.out.println("Los datos del primer empleado: ");
        e1.imprimeEmpleado();
        System.out.println("Y su sueldo es de " + n.sueldo(e1));
        System.out.println("Los datos del segundo empleado: ");
        e2.imprimeEmpleado();
        System.out.println("Y su sueldo es de " + n.sueldo(e2));
        
    }
    
    public static void main(String[] args) throws DatosNoCorrectosException {

        /**Creacion de objetos**/
        Empleado e1 = new Empleado(4, 7, "James Cosling", "32000032G", 'M');
        Empleado e2 = new Empleado("Ada Lovelace", "32000031R", 'F');
        CalculaNominas nn = new CalculaNominas();

        /**Imprimimos mediante escribe**/
        nn.escribe(e1, e2);
        /**Incremento de los años trabajados y cambio de categoria a 9**/
        e2.incrAnyo();
        e2.setCategoria(9);

        /**Imprimimos los empleados y su sueldo*/
        nn.escribe(e1, e2);
        
    }
}
