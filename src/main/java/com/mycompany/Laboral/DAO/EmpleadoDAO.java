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

    /**
     * métodos del apartado 5
     *
     */

    // 5.1. Mostrar información de todos los empleados
    public void mostrarEmpleados() throws SQLException {
        String sql = "SELECT nombre, dni, sexo, categoria, anyos FROM Empleados";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- LISTADO DE EMPLEADOS ---");
            while (rs.next()) {
                System.out.println("DNI: " + rs.getString("dni") +
                        " | Nombre: " + rs.getString("nombre") +
                        " | Sexo: " + rs.getString("sexo") +
                        " | Categoría: " + rs.getInt("categoria") +
                        " | Años: " + rs.getInt("anyos"));
            }
        }
    }

    // 5.2. Mostrar salario de un empleado por DNI
    public void mostrarSalarioEmpleado(String dni) throws SQLException {
        String sql = "SELECT sueldo FROM Nominas WHERE dni = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("El salario del empleado con DNI " + dni + " es: " + rs.getInt("sueldo") + "€");
                } else {
                    System.out.println("No se encontró ningún empleado con el DNI: " + dni);
                }
            }
        }
    }

    // 5.3. Modificar datos de un empleado (actualiza sueldo automáticamente)
    public void modificarEmpleado(Empleado e) throws SQLException {
        int nuevoSueldo = Nomina.sueldo(e);
        String sqlEmp = "UPDATE Empleados SET nombre = ?, sexo = ?, categoria = ?, anyos = ? WHERE dni = ?";
        String sqlNom = "UPDATE Nominas SET sueldo = ? WHERE dni = ?";

        boolean autoCommitOriginal = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement psEmp = conn.prepareStatement(sqlEmp)) {
                psEmp.setString(1, e.nombre);
                psEmp.setString(2, String.valueOf(e.sexo));
                psEmp.setInt(3, e.getCategoria());
                psEmp.setInt(4, e.anyosTrabajados);
                psEmp.setString(5, e.dni);
                psEmp.executeUpdate();
            }

            try (PreparedStatement psNom = conn.prepareStatement(sqlNom)) {
                psNom.setInt(1, nuevoSueldo);
                psNom.setString(2, e.dni);
                psNom.executeUpdate();
            }

            conn.commit();
            System.out.println("Empleado y sueldo actualizado correctamente.");
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(autoCommitOriginal);
        }
    }

    // 5.4. Recalcular y actualizar el sueldo de un empleado por su DNI
    public void recalcularSueldoEmpleado(String dni) throws SQLException, DatosNoCorrectosException {
        String sqlSelect = "SELECT nombre, sexo, categoria, anyos FROM Empleados WHERE dni = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Empleado e = new Empleado(
                            rs.getInt("categoria"),
                            rs.getInt("anyos"),
                            rs.getString("nombre"),
                            dni,
                            rs.getString("sexo").charAt(0)
                    );
                    int sueldoNuevo = Nomina.sueldo(e);

                    String sqlUpdate = "UPDATE Nominas SET sueldo = ? WHERE dni = ?";
                    try (PreparedStatement psUp = conn.prepareStatement(sqlUpdate)) {
                        psUp.setInt(1, sueldoNuevo);
                        psUp.setString(2, dni);
                        psUp.executeUpdate();
                    }
                    System.out.println("Sueldo recalculado y actualizado a: " + sueldoNuevo + "€");
                } else {
                    System.out.println("Empleado no encontrado.");
                }
            }
        }
    }

    // 5.5. Recalcular y actualizar sueldos de TODOS los empleados
    public void recalcularTodosLosSueldos() throws SQLException, DatosNoCorrectosException {
        String sql = "SELECT dni, categoria, anyos, nombre, sexo FROM Empleados";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Empleado e = new Empleado(
                        rs.getInt("categoria"),
                        rs.getInt("anyos"),
                        rs.getString("nombre"),
                        rs.getString("dni"),
                        rs.getString("sexo").charAt(0)
                );
                int nuevoSueldo = Nomina.sueldo(e);

                String sqlUpdate = "UPDATE Nominas SET sueldo = ? WHERE dni = ?";
                try (PreparedStatement psUp = conn.prepareStatement(sqlUpdate)) {
                    psUp.setInt(1, nuevoSueldo);
                    psUp.setString(2, e.dni);
                    psUp.executeUpdate();
                }
            }
            System.out.println("Se han recalculado todos los sueldos correctamente.");
        }
    }

    // 5.6. Realizar copia de seguridad completa de la BD a ficheros
    public void realizarBackupCompleto() throws SQLException {
        String sql = "SELECT e.categoria, e.anyos, e.nombre, e.dni, e.sexo, n.sueldo " +
                "FROM Empleados e JOIN Nominas n ON e.dni = n.dni";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql);
             PrintWriter pw = new PrintWriter(new FileWriter("empleados.txt", false))) { // Sobrescribe el backup previo

            while (rs.next()) {
                String linea = rs.getInt("categoria") + "," +
                        rs.getInt("anyos") + "," +
                        rs.getString("nombre") + "," +
                        rs.getString("dni") + "," +
                        rs.getString("sexo");
                pw.println(linea);
            }
            System.out.println("Copia de seguridad en 'empleados.txt' realizada con éxito.");
        } catch (IOException ex) {
            System.out.println("Error al escribir el backup: " + ex.getMessage());
        }
    }
}
