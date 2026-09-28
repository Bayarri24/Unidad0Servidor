package com.mycompany.Laboral.DAO;
import com.mycompany.Laboral.Empleado;
import com.mycompany.Laboral.Nomina;
import com.mycompany.Laboral.Exceptions.DatosNoCorrectosException;

import java.io.*;
import java.sql.*;
public class EmpleadoDAO {
    Connection conn;

    public EmpleadoDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Método para dar de alta al empleado
     */

    public void altaEmpleado(Empleado e) throws SQLException {
        // Calculamos el sueldo automáticamente usando la clase Nomina
        int sueldoCalculado = Nomina.sueldo(e);


        // Preparamos las sentencias SQL para insertar en ambas tablas
        String insertEmpleado = "INSERT INTO Empleados (dni, nombre, sexo, categoria, anyos)" +
                " VALUES (?, ?, ?, ?, ?)";
        String insertNomina = "INSERT INTO Nominas (dni, sueldo) " +
                "VALUES (?, ?) ";


        boolean autoCommitOriginal = conn.getAutoCommit();

        try {
            /**
             * Iniciamos la transacción deshabilitando autocommit
             */

            conn.setAutoCommit(false);

            // Insertar datos en la tabla Empleados
            try (PreparedStatement psEmp = conn.prepareStatement(insertEmpleado)) {
                psEmp.setString(1, e.dni);
                psEmp.setString(2, e.nombre);
                psEmp.setString(3, String.valueOf(e.sexo));
                psEmp.setInt(4, e.getCategoria());
                psEmp.setInt(5, e.anyosTrabajados);

                psEmp.executeUpdate();
            }

            // Insertar sueldo calculado en la tabla Nominas
            try (PreparedStatement psNom = conn.prepareStatement(insertNomina)) {
                psNom.setString(1, e.dni);
                psNom.setInt(2, sueldoCalculado);

                psNom.executeUpdate();
            }

            // Confirmamos la transacción
            conn.commit();
            System.out.println("Empleado dado de alta");

            //Llamamos al método creado más abajo para hacer el backup de los ficheros
            hacerBackupFicheros(e);

        } catch (SQLException ex) {
            /**
             * Si ocurre algún error, deshacemos los cambios
             */
            conn.rollback();
            throw ex;
        } finally {
            /**
             * Restauramos el autocommit a su estado previo
             */
            conn.setAutoCommit(autoCommitOriginal);
        }
    }

    /**
     * Método para dar de alta a varios empleados desde un archivo de texto
     */
    public void altaEmpleado(String empleadosNuevosTxt) {
        String linea;

        try (BufferedReader br = new BufferedReader(new FileReader(empleadosNuevosTxt))) {

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(",");

                if (datos.length == 5) {
                    String dni = datos[0].trim();
                    String nombre = datos[1].trim();
                    char sexo = datos[2].trim().charAt(0);
                    int categoria = Integer.parseInt(datos[3].trim());
                    int anyosTrabajados = Integer.parseInt(datos[4].trim());

                    try {
                        /**
                         Creamos el objeto Empleado usando tu constructor: (categoria, anyos, nombre, dni, sexo)
                         */
                        Empleado e = new Empleado(categoria, anyosTrabajados, nombre, dni, sexo);

                        // Llamamos al método individual para darlo de alta en la BD
                        altaEmpleado(e);

                    } catch (DatosNoCorrectosException ex) {
                        System.out.println("Error en los datos del empleado (" + dni + "): " + ex.getMessage());
                    } catch (SQLException ex) {
                        System.out.println("Error de SQL al insertar el empleado con DNI " + dni + ": " + ex.getMessage());
                    } catch (NumberFormatException ex) {
                        System.out.println("Error numérico en los datos de la línea: " + linea);
                    }
                } else {
                    System.out.println("Línea con número de campos incorrecto: " + linea);
                }
            }

        } catch (IOException ex) {
            System.out.println("Error al leer el archivo " + empleadosNuevosTxt + ": " + ex.getMessage());
        }
    }

    /**
     * Método para hacer backup del fichero empleados.txt
     */

    private void hacerBackupFicheros(Empleado e) {
        // Backup de texto en 'empleados.txt' (append = true para añadir al final)
        try (FileWriter fw = new FileWriter("empleados.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {

            String registro = e.getCategoria() + "," + e.anyosTrabajados + "," + e.nombre + "," + e.dni + "," + e.sexo;
            pw.println(registro);
            System.out.println("Backup actualizado en 'empleados.txt'");

        } catch (IOException ex) {
            System.out.println("Error al realizar el backup en empleados.txt: " + ex.getMessage());
        }


    }

}
