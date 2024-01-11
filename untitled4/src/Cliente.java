import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Cliente {
    static void menuCliente(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Bienvenido al menu de cliente:");
        int opcion = -1;


        while (opcion != 5) {

            System.out.println("Selecciona una de las siguientes opciones del cliente:");
            System.out.println("1. Dar alta a un cliente.");
            System.out.println("2. Dar baja a un cliente.");
            System.out.println("3. Modificar un cliente.");
            System.out.println("4. Mostrar datos de cliente.");
            System.out.println("5. Mostrar peliculas alquiladas.");

            opcion = sc.nextInt();
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
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }

    }
    static void darAltaCliente(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduce el correo electrónico del cliente:");
        String correoElectronico = sc.nextLine();
        while (Alquiler.comprobarExisteCliente(conn, correoElectronico) || correoElectronico.isEmpty()) {
            System.out.println("El correo ya existe, introduce otro:");
            correoElectronico = sc.nextLine();
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
        //pon la fecha actual como fecha de alta y la de baja dentro de un año
        LocalDate fechaAlta = LocalDate.now();
        LocalDate fechaBaja = fechaAlta.plusYears(1);

        // Insertar datos en la tabla DatosCliente
        String sql = "INSERT INTO DatosCliente (CorreoElectronico, Nombre, Apellidos, Telefono, FechaAlta, FechaBaja) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, correoElectronico);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellidos);
            pstmt.setString(4, telefono);
            pstmt.setDate(5, Date.valueOf(fechaAlta));
            pstmt.setDate(6, Date.valueOf(fechaBaja));
        pstmt.executeUpdate();

    }
    static void ejecutarSQL(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
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

        while (comprobarBajaCliente(conn, correo) || !Alquiler.comprobarExisteCliente(conn, correo)) {
            System.out.println("El correo no existe o ya está dado de baja, introduce otro:");
            correo = sc.nextLine();
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
        String sql = "SELECT * FROM DatosCliente WHERE CorreoElectronico='" + correo + "'";
        Statement pstmt = conn.createStatement();
        ResultSet rs = pstmt.executeQuery(sql);

        System.out.println("Datos de cliente:");
        System.out.println("CorreoElectronico\tNombre\tApellidos\tTelefono\tFechaAlta\tFecha Baja");
        System.out.println("-----------------------------------------------");
        rs.next();
        System.out.println(rs.getString(1) + "\t" +
                rs.getString(2) + "\t" +
                rs.getString(3) + "\t" +
                rs.getString(4) + "\t" +
                rs.getDate(5) + "\t" +
                rs.getDate(6));
    }
    //Comprobar correo y baja
    static void modificarCliente(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduce el correo electrónico del cliente a modificar:");
        String correo = sc.nextLine();
        while (!Alquiler.comprobarExisteCliente(conn, correo) || comprobarBajaCliente(conn, correo)) {
            System.out.println("El correo no existe o ya está dado de baja, introduce otro:");
            correo = sc.nextLine();
        }
        System.out.println("Introduce el nombre nuevo del cliente:");
        String nombre = sc.nextLine();
        System.out.println("Introduce los apellidos nuevos del cliente:");
        String apellidos = sc.nextLine();
        System.out.println("Introduce el teléfono nuevo del cliente:");
        String telefono = sc.nextLine();
        String sql = "UPDATE DatosCliente SET Nombre=?, Apellidos=?, Telefono=? WHERE CorreoElectronico=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nombre);
        pstmt.setString(2, apellidos);
        pstmt.setString(3, telefono);
        pstmt.setString(4, correo);
        pstmt.executeUpdate();


    }
    //Comprobar corrro baja y idpelicula
    public static void mostrarPeliculasAlquiladas(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduzca el correo electronico del cliente que quieras ver los alquileres:");
        String correo = sc.nextLine();
        while (!Alquiler.comprobarExisteCliente(conn, correo) || comprobarBajaCliente(conn, correo)) {
            System.out.println("El correo no existe o ya está dado de baja, introduce otro:");
            correo = sc.nextLine();
        }
        String sql = "SELECT IDPelicula FROM AlquilaV2 WHERE CorreoElectronico='" + correo + "'";
        Statement pstmt = conn.createStatement();
        ResultSet rs = pstmt.executeQuery(sql);
        String peli;
        ResultSet rs2;
        while (rs.next()) {
            peli = "SELECT * FROM DatosPelicula WHERE IDPelicula='" + rs.getString(1) + "'";
            pstmt = conn.createStatement();
            rs2 = pstmt.executeQuery(peli);
            rs2.next();
            System.out.println("ID: " + rs2.getString(1));
            System.out.println("Nombre: " + rs2.getString(2));
            System.out.println("Precio: " + rs2.getString(3));
            System.out.println("FechaEstreno: " + rs2.getString(4));
            System.out.println("FechaAlta: " + rs2.getString(5));
            System.out.println("FechaBaja: " + rs2.getString(6));
            System.out.println("Sinopsis: " + rs2.getString(7));
            System.out.println("Calificacion: 16" + rs2.getString(8));
            System.out.println("-------------------------------------------------");
        }
    }
}
