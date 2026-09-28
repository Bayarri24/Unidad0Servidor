/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.Laboral;

import DBUtils.DBUtils; // NUEVO: Import para la gestión de conexión con BD
import com.mycompany.Laboral.DAO.EmpleadoDAO; // NUEVO: Import para usar la clase DAO
import com.mycompany.Laboral.Exceptions.DatosNoCorrectosException;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException; // NUEVO: Import para capturar excepciones de SQL
import java.util.Scanner; // NUEVO: Import para leer la entrada por teclado en el menú

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
        Connection conn = null;
        Scanner sc = new Scanner(System.in);


        /**Imprimimos mediante escribe**/
        nn.escribe(e1, e2);
        /**Incremento de los años trabajados y cambio de categoria a 9**/
        e2.incrAnyo();
        e1.setCategoria(9);

        /**Imprimimos los empleados y su sueldo*/
        nn.escribe(e1, e2);

        //Llamamos a la función y leemos el txt
        leerTxt("empleados.txt");

        //Actualizamos el txt con un nuevo empleado y lo leemos de nuevo
        actualizarTxt();


        // Apertura de la conexión a la base de datos y ejecutar el menú interactivo
        try {
            // Abrimos conexión mediante DBUtils
            conn = DBUtils.getConnection();
            System.out.println("\nConexión a la base de datos establecida correctamente.");

            EmpleadoDAO dao = new EmpleadoDAO(conn);


            int opcion = 0;

            //  Menú interactivo
            do {
                System.out.println("MENÚ DE OPCIONES");
                System.out.println("1. Mostrar información de todos los empleados");
                System.out.println("2. Mostrar salario de un empleado por DNI");
                System.out.println("3. Modificar datos de un empleado");
                System.out.println("4. Recalcular y actualizar sueldo de un empleado");
                System.out.println("5. Recalcular y actualizar todos los sueldos ");
                System.out.println("6. Realizar copia de seguridad de la BD a ficheros");
                System.out.println("7. Alta individual de empleado en BD");
                System.out.println("8. Alta masiva de empleados desde fichero .txt a BD");
                System.out.println("9. Salir");
                System.out.print("Seleccione una opción: ");

                try {
                    opcion = sc.nextInt();

                    switch (opcion) {
                        case 1:
                            dao.mostrarEmpleados();
                            break;
                        case 2:
                            System.out.print("Introduce el DNI del empleado: ");
                            String dniSalario = sc.nextLine();
                            dao.mostrarSalarioEmpleado(dniSalario);
                            break;
                        case 3:
                            menuModificarEmpleado(sc, dao);
                            break;
                        case 4:
                            System.out.print("Introduce el DNI del empleado: ");
                            String dniRecalc = sc.nextLine();
                            dao.recalcularSueldoEmpleado(dniRecalc);
                            break;
                        case 5:
                            dao.recalcularTodosLosSueldos();
                            break;
                        case 6:
                            dao.realizarBackupCompleto();
                            break;
                        case 7:
                            dao.altaEmpleado(e1);
                            break;
                        case 8:
                            System.out.print("Introduce el nombre del fichero (ej. empleadosNuevos.txt): ");
                            String rutaFichero = sc.nextLine();
                            dao.altaEmpleado(rutaFichero);
                            break;
                        case 9:
                            System.out.println("Saliendo de la aplicación...");
                            break;
                        default:
                            System.out.println("Opción no válida.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, introduce un número válido.");
                } catch (SQLException e) {
                    System.out.println("Error de Base de Datos: " + e.getMessage());
                }

            } while (opcion != 9);

        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        } finally {
            // Cerramos la conexión de forma segura
            if (conn != null) {
                try {
                    DBUtils.close(conn);
                    System.out.println("Conexión a la base de datos cerrada.");
                } catch (SQLException e) {
                    System.out.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Método para el submenú de modificación de empleados
     */
    private static void menuModificarEmpleado(Scanner sc, EmpleadoDAO dao) {
        try {
            System.out.print("Introduce el DNI del empleado a modificar: ");
            String dni = sc.nextLine();

            System.out.print("Nuevo Nombre: ");
            String nombre = sc.nextLine();

            System.out.print("Nuevo Sexo (M/F): ");
            char sexo = sc.nextLine().charAt(0);

            System.out.print("Nueva Categoría (1-10): ");
            int categoria = Integer.parseInt(sc.nextLine());

            System.out.print("Nuevos Años Trabajados: ");
            int anyos = Integer.parseInt(sc.nextLine());

            Empleado empModificado = new Empleado(categoria, anyos, nombre, dni, sexo);

            // Se actualiza en la BD
            dao.modificarEmpleado(empModificado);

        } catch (DatosNoCorrectosException e) {
            System.out.println("Error en la validación de los datos: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de SQL al modificar: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Formato de entrada no válido.");
        }
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
            fw.close();  // Se debe cerrar el FileWriter para que los cambios se guarden correctamente
            System.out.println("Documento actualizado");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}