import oracle.jdbc.proxy.annotation.Pre;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Cliente {
    static void menuCliente(Connection conn, Scanner sc) throws SQLException {

        System.out.println("\nBienvenido al menu de cliente:");
        int opcion = -1;


        while (true) {

            System.out.println("\nSelecciona una de las siguientes opciones del cliente:");
            System.out.println("1. Dar alta a un cliente.");
            System.out.println("2. Dar baja a un cliente.");
            System.out.println("3. Modificar un cliente.");
            System.out.println("4. Mostrar datos de cliente.");
            System.out.println("5. Mostrar peliculas alquiladas.");
            System.out.println("6. Salir.");

            opcion = sc.nextInt();
            sc.nextLine();
            switch (opcion) {
                case 1:
                    darAltaCliente(conn, sc);
                    break;
                case 2:
                    darBajaCliente(conn, sc);
                    break;
                case 3:
                    modificarCliente(conn, sc);
                    break;
                case 4:
                    mostrarDatosCliente(conn, sc);
                    break;
                case 5:
                    mostrarPeliculasAlquiladas(conn, sc);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }

    }
    public static boolean comprobarExisteCliente(Connection conn, String correo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DatosCliente WHERE CorreoElectronico = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, correo);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }
    static void darAltaCliente(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduce el correo electrónico del cliente:");
        String correoElectronico = sc.nextLine();
        if (comprobarExisteCliente(conn, correoElectronico) || correoElectronico.isEmpty()) {
            System.out.println("El correo ya existe");
            return;
        }
        System.out.println("Introduce el nombre del cliente:");
        String nombre = sc.nextLine();
        System.out.println("Introduce los apellidos del cliente:");
        String apellidos = sc.nextLine();
        String telefono;
        System.out.println("¿Quiere introduci el teléfono del cliente? (S/N)");
        if (sc.nextLine().equalsIgnoreCase("S")) {
            System.out.println("Introduce el teléfono del cliente:");
            telefono = sc.nextLine();
        }
        else {
             telefono = null;
        }
        LocalDate fechaAlta = LocalDate.now();

        // Insertar datos en la tabla DatosCliente
        String sql = "INSERT INTO DatosCliente (CorreoElectronico, Nombre, Apellidos, Telefono, FechaAlta) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, correoElectronico);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellidos);
            pstmt.setString(4, telefono);
            pstmt.setDate(5, Date.valueOf(fechaAlta));
        pstmt.executeUpdate();

    }

    public static boolean comprobarBajaCliente(Connection conn, String correo) throws SQLException{
        String sqlComprobarBaja = "SELECT FechaBaja FROM DatosCliente WHERE CorreoElectronico = ?";
        PreparedStatement comprobarBaja = conn.prepareStatement(sqlComprobarBaja);
        comprobarBaja.setString(1, correo);
        ResultSet resultComprobarBaja = comprobarBaja.executeQuery();

        if (resultComprobarBaja.next()) {
            Date fechaBaja = resultComprobarBaja.getDate("FechaBaja");
            return fechaBaja != null;
        }
        return true;
    }


    public static void darBajaCliente(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduzca el correo electronico del cliente que quieras dar de baja:");
        String correo;
        correo = sc.nextLine();

        if (comprobarBajaCliente(conn, correo) || !comprobarExisteCliente(conn, correo)) {
            System.out.println("El correo no existe o ya está dado de baja");
            return;
        }
        LocalDate fecha = LocalDate.now();

        String sql = "UPDATE DatosCliente SET FechaBaja=? WHERE CorreoElectronico=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setDate(1, Date.valueOf(fecha));
        pstmt.setString(2, correo);
        pstmt.executeUpdate();
    }

    //Comprobar correo
    public static void mostrarDatosCliente(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduzca el correo electronico del cliente que quieras ver los datos:");
        String correo = sc.nextLine();
        if (!comprobarExisteCliente(conn, correo)) {
            System.out.println("El correo no existe");
            return;
        }
        String sql = "SELECT * FROM DatosCliente WHERE CorreoElectronico= ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, correo);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("Datos de cliente:");
        System.out.println("CorreoElectronico\tNombre\tApellidos\tTelefono\tFechaAlta\tFecha Baja");
        System.out.println("-----------------------------------------------");
        if(rs.next()) {
            System.out.println(rs.getString(1) + "\t" +
                    rs.getString(2) + "\t" +
                    rs.getString(3) + "\t" +
                    rs.getString(4) + "\t" +
                    rs.getDate(5) + "\t" +
                    rs.getDate(6));
        }
    }

    static void modificarCliente(Connection conn, Scanner sc) throws SQLException {
        conn.setAutoCommit(false);
        Savepoint saveUpdatePelicula=conn.setSavepoint();
        System.out.println("Introduce el correo electrónico del cliente a modificar:");
        String correo = sc.nextLine();
        if(!comprobarExisteCliente(conn, correo) || comprobarBajaCliente(conn, correo)) {
            System.out.println("El correo no existe o está dado de baja");
            conn.setAutoCommit(true);
            return;
        }
        System.out.println("¿Que campo desea modificar?:\n1. Nombre\n2. Apellidos\n3. Telefono\n4. Finalizar");
        int opcion = sc.nextInt();
        sc.nextLine();
        PreparedStatement updateCliente;
        String sqlUpdateCliente;
        while (opcion!=4) {
            switch (opcion) {
                case 1:
                    sqlUpdateCliente = "UPDATE DatosCliente SET Nombre = ? WHERE CorreoElectronico = ?";
                    updateCliente = conn.prepareStatement(sqlUpdateCliente);
                    updateCliente.setString(2, correo);
                    System.out.println("Introduce el nombre");
                    updateCliente.setString(1, sc.nextLine());
                    updateCliente.execute();
                    break;
                case 2:
                    sqlUpdateCliente = "UPDATE DatosCliente SET Apellidos = ? WHERE CorreoElectronico = ?";
                    updateCliente = conn.prepareStatement(sqlUpdateCliente);
                    updateCliente.setString(2, correo);
                    System.out.println("Introduce los apellidos");
                    updateCliente.setString(1, sc.nextLine());
                    updateCliente.execute();
                    break;
                case 3:
                    sqlUpdateCliente = "UPDATE DatosCliente SET Telefono = ? WHERE CorreoElectronico = ?";
                    updateCliente = conn.prepareStatement(sqlUpdateCliente);
                    updateCliente.setString(2, correo);
                    System.out.println("Introduce el telefono");
                    updateCliente.setString(1, sc.nextLine());
                    updateCliente.execute();
                    break;
            }
            System.out.println("¿Que campo desea modificar?:\n1. Nombre\n2. Apellidos\n3. Telefono\n4. Finalizar");
            opcion = sc.nextInt();
            sc.nextLine();

        }

        String sql = "SELECT * FROM DatosCliente WHERE CorreoElectronico= ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, correo);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("Datos de cliente:");
        System.out.println("CorreoElectronico\tNombre\tApellidos\tTelefono\tFechaAlta\tFecha Baja");
        System.out.println("-----------------------------------------------");
        if(rs.next()) {
            System.out.println(rs.getString(1) + "\t" +
                    rs.getString(2) + "\t" +
                    rs.getString(3) + "\t" +
                    rs.getString(4) + "\t" +
                    rs.getDate(5) + "\t" +
                    rs.getDate(6));
        }
        System.out.println("¿Quieres confirmar los cambios?\n1. Si\n2. No");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                System.out.println("Cambios aplicados");
                break;
            case 2:
                conn.rollback(saveUpdatePelicula);
                System.out.println("Cambios revertidos");
                break;
        }
        sc.nextLine();
        conn.setAutoCommit(true);
    }
    public static void mostrarPeliculasAlquiladas(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduzca el correo electronico del cliente que quieras ver los alquileres:");
        String correo = sc.nextLine();
        if (!comprobarExisteCliente(conn, correo) || comprobarBajaCliente(conn, correo)) {
            System.out.println("El correo no existe o está dado de baja");
            return;
        }
        String sql = "SELECT IDPelicula FROM DatosAlquiler WHERE CorreoElectronico= ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, correo);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()) {
            do {
                Pelicula.mostrarPelicula(conn, rs.getInt("IDPelicula"));
            }while (rs.next());
        } else {
            System.out.println("No tiene peliculas alquiladas");
        }
    }
}
