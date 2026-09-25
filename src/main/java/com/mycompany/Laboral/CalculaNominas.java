/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.Laboral;

import com.mycompany.Laboral.Exceptions.DatosNoCorrectosException;

import java.io.*;

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
        e1.setCategoria(9);

        /**Imprimimos los empleados y su sueldo*/
        nn.escribe(e1, e2);


        //Llamamos a la función y leemos el txt
        leerTxt("empleados.txt");

    }

    /**
     * Creamos una función a la que le introducimos un String y recorremos el txt
     *
     */
    public static void leerTxt(String empleado) {
        //Con el Reader leemos linea  a linea
        try (BufferedReader br = new BufferedReader(new FileReader(empleado))) {
            String line;

            /**
             * Con este while le indicamos que mientras != null(va leyendo linea a linea), es decir mientras tenga linea para leer la imprime*/
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void actualizarTxt() {
        try {
            FileWriter fw = new FileWriter("empleados.txt");
            fw.write("categoría: 4,años trabajados: 9, nombre: James Cosling, dni: 32000032G, sexo:´M´\n");
            fw.close();  // must close manually
            System.out.println("Documento actualizado");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

