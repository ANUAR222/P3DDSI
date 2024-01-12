import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Cliente {
    static void menuCliente(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Bienvenido al menu de cliente:");
        int opcion = -1;


        while (true) {

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
                case 6:
                    return;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }

    }
    //Fecha de baja tiene q swe null si o si
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

    //La funcion de comprobar cliente deberia estar aqui no en alquiler
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

    static void modificarCliente(Connection conn, Scanner sc) throws SQLException {
        conn.setAutoCommit(false);
        Savepoint saveUpdatePelicula=conn.setSavepoint();
        System.out.println("Introduce el correo electrónico del cliente a modificar:");
        String correo = sc.nextLine();
        while (!Alquiler.comprobarExisteCliente(conn, correo) || comprobarBajaCliente(conn, correo)) {
            System.out.println("El correo no existe o ya está dado de baja, introduce otro:");
            correo = sc.nextLine();
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
                    sqlUpdateCliente = "UPDATE DatosCliente SET Apellido = ? WHERE CorreoElectronico = ?";
                    updateCliente = conn.prepareStatement(sqlUpdateCliente);
                    updateCliente.setString(2, correo);
                    System.out.println("Introduce los apellidos");
                    updateCliente.setString(1, sc.nextLine());
                    updateCliente.execute();
                    break;
                case 3:
                    sc.nextLine();
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
        while (!Alquiler.comprobarExisteCliente(conn, correo) || comprobarBajaCliente(conn, correo)) {
            System.out.println("El correo no existe o ya está dado de baja, introduce otro:");
            correo = sc.nextLine();
        }
        String sql = "SELECT IDPelicula FROM DatosAlquiler WHERE CorreoElectronico='" + correo + "'";
        Statement pstmt = conn.createStatement();
        ResultSet rs = pstmt.executeQuery(sql);
        String peli;
        ResultSet rs2;
        while (rs.next()) {
            Pelicula.mostrarPelicula(conn, rs.getInt("IDPelicula"));
        }
    }
}
