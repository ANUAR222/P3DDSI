


import java.nio.channels.SelectableChannel;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.time.LocalDate;

import java.sql.Date;



public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;

        try {
            conn = conectarBaseDeDatos(sc);
            int opcion = -1;

            while (opcion != 11) {
                System.out.println("Selecciona una de las siguientes opciones:");
                System.out.println("1. Borrar y crear tablas.");
                System.out.println("2. Dar alta película.");
                System.out.println("3. Mostrar películas.");
                System.out.println("4. Simular inserción de alquiler.");
                System.out.println("5. Dar alta a un cliente.");
                System.out.println("6. Dar alta a un empleado.");
                System.out.println("7. Dar baja a un empleado.");
                System.out.println("8. Modificar un empleado.");
                System.out.println("9. Mostrar empleados.");
                System.out.println("10. Buscar a un empleado.");
                System.out.println("11. Salir.");

                opcion = sc.nextInt();
                switch (opcion) {
                    case 1:
                        insertarDatosEjemplo(conn);
                        break;
                    case 2:
                        //darAltaPelicula(conn, sc);
                        break;
                    case 3:
                        //mostrarPeliculas(conn);
                        break;
                    case 4:
                        simularInsercionAlquiler(conn, sc);
                        break;
                    case 5:
                        darAltaCliente(conn, sc);
                        break;
                    case 6:
                        darAltaEmpleado(conn,sc);
                    case 7:
                        darBajaEmpleado(conn,sc);
                    case 8:
                        modificarEmpleado(conn,sc);
                    case 9:
                        mostrarEmpleado(conn,sc);
                    case 10:
                        buscarEmpleado(conn,sc);
                    case 11:
                        salir(conn);
                        break;
                    default:
                        System.out.println("Opción no válida.");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    public static Connection conectarBaseDeDatos(Scanner sc) throws SQLException {
        System.out.println("Introduce el usuario:");
        String usuario = sc.next();
        System.out.println("Introduce la contraseña:");
        String contrasenia = sc.next();
        String url = "jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
        return DriverManager.getConnection(url, usuario, contrasenia);
    }

    public static void crearTablas(Connection conn) throws SQLException {
        try {
            // Inserción de datos de ejemplo
            insertarDatosEjemplo(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static void insertarDatosEjemplo(Connection conn) throws SQLException {
        // Insertar datos en la tabla DatosPelicula
        String insertDatosPelicula = "INSERT INTO DatosPelicula (IDPelicula, Nombre, Precio, FechaEstreno, FechaAlta, FechaBaja, Sinopsis, Calificacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertDatosPelicula)) {
            stmt.setInt(1, 1);
            stmt.setString(2, "Interstellar");
            stmt.setDouble(3, 12.99);
            stmt.setDate(4, Date.valueOf("2014-11-07"));
            stmt.setDate(5, Date.valueOf("2014-11-01"));
            stmt.setDate(6, Date.valueOf("2015-04-30"));
            stmt.setString(7, "Un grupo de exploradores hacen uso de un agujero de gusano recién descubierto para superar las limitaciones de los viajes espaciales tripulados y conquistar las vastas distancias involucradas en un viaje interestelar.");
            stmt.setDouble(8, 4.7);
            stmt.executeUpdate();
        }

        // Insertar datos en la tabla DatosGenero
        String insertDatosGenero = "INSERT INTO DatosGenero (IDGenero, Nombre) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertDatosGenero)) {
            stmt.setInt(1, 1);
            stmt.setString(2, "Ciencia Ficción");
            stmt.executeUpdate();
        }

        // Insertar datos en la tabla PerteneceA
        String insertPerteneceA = "INSERT INTO PerteneceA (IDPelicula, IDGenero) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertPerteneceA)) {
            stmt.setInt(1, 1);
            stmt.setInt(2, 1);
            stmt.executeUpdate();
        }

        // Insertar datos en la tabla Actores
        String insertActores = "INSERT INTO Actores (NombreActor) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertActores)) {
            stmt.setString(1, "Matthew McConaughey");
            stmt.executeUpdate();
        }

        // Insertar datos en la tabla Actua
        String insertActua = "INSERT INTO Actua (IDPelicula, NombreActor) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertActua)) {
            stmt.setInt(1, 1);
            stmt.setString(2, "Matthew McConaughey");
            stmt.executeUpdate();
        }

        // Insertar datos en la tabla DatosCliente
        String insertDatosCliente = "INSERT INTO DatosCliente (CorreoElectronico, Nombre, Apellidos, Telefono, FechaAlta, FechaBaja) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertDatosCliente)) {
            stmt.setString(1, "cliente1@example.com");
            stmt.setString(2, "Juan");
            stmt.setString(3, "Pérez");
            stmt.setString(4, "123456789");
            stmt.setDate(5, Date.valueOf("2022-01-01"));
            stmt.setDate(6, Date.valueOf("2022-12-31"));
            stmt.executeUpdate();
        }

        // Insertar datos en la tabla DatosTurno
        String insertDatosTurno = "INSERT INTO DatosTurno (NombreTurno, HoraEntrada, HoraSalida, SueldoHora, SueldoTotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertDatosTurno)) {
            stmt.setString(1, "Turno Matutino");
            stmt.setTime(2, Time.valueOf("08:00:00"));
            stmt.setTime(3, Time.valueOf("16:00:00"));
            stmt.setFloat(4, 5.0f);
            stmt.setFloat(5, 1200.0f);
            stmt.executeUpdate();
        }

        // Insertar datos en la tabla DatosEmpleado
        String insertDatosEmpleado = "INSERT INTO DatosEmpleado (DNI, Nombre, Apellidos, Telefono, Sueldo, Direccion, NombreTurno) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertDatosEmpleado)) {
            stmt.setString(1, "12345678A");
            stmt.setString(2, "Ana");
            stmt.setString(3, "López");
            stmt.setString(4, "987654321");
            stmt.setFloat(5, 1200.0f);
            stmt.setString(6, "Calle Mayor, 123");
            stmt.setString(7, "Turno Matutino");
            stmt.executeUpdate();
        }

        // Insertar datos en la tabla DatosAlquiler
        String insertDatosAlquiler = "INSERT INTO DatosAlquiler (CorreoElectronico, IDPelicula, FechaAlquiler, FechaVencimiento, FechaAcceso, Calificacion) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertDatosAlquiler)) {
            stmt.setString(1, "cliente1@example.com");
            stmt.setInt(2, 1);
            stmt.setDate(3, Date.valueOf("2022-05-01"));
            stmt.setDate(4, Date.valueOf("2022-05-15"));
            stmt.setDate(5, Date.valueOf("2022-05-02"));
            stmt.setDouble(6, 4.5);
            stmt.executeUpdate();
        }

        // Insertar datos en la tabla PrecioAlquiler
        String insertPrecioAlquiler = "INSERT INTO PrecioAlquiler (IDPelicula, FechaAlquiler, FechaVencimiento, PrecioAlquiler) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertPrecioAlquiler)) {
            stmt.setInt(1, 1);
            stmt.setDate(2, Date.valueOf("2022-05-01"));
            stmt.setDate(3, Date.valueOf("2022-05-15"));
            stmt.setDouble(4, 3.5);
            stmt.executeUpdate();
        }
    }
    static void darAltaCliente(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduce el correo electrónico del cliente:");
        String correoElectronico = sc.nextLine();
        System.out.println("Introduce el nombre del cliente:");
        String nombre = sc.nextLine();
        System.out.println("Introduce los apellidos del cliente:");
        String apellidos = sc.nextLine();
        System.out.println("Introduce el teléfono del cliente:");
        String telefono = sc.nextLine();
        //pon la fecha actual como fecha de alta y la de baja dentro de un año
        LocalDate fechaAlta = LocalDate.now();
        LocalDate fechaBaja = fechaAlta.plusYears(1);

        // Insertar datos en la tabla DatosCliente
        String sql = "INSERT INTO DatosCliente (CorreoElectronico, Nombre, Apellidos, Telefono, FechaAlta, FechaBaja) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correoElectronico);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellidos);
            pstmt.setString(4, telefono);
            pstmt.setDate(5, Date.valueOf(fechaAlta));
            pstmt.setDate(6, Date.valueOf(fechaBaja));
            pstmt.executeUpdate();
        }
    }
    static void ejecutarSQL(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }





    public static void simularInsercionAlquiler(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduce el correo electrónico del cliente:");
        String correo = sc.nextLine();
        System.out.println("Introduce el ID de la película:");
        int idPelicula = sc.nextInt();
        sc.nextLine();  // Consumir el salto de línea pendiente
        System.out.println("Introduce la fecha de alquiler (YYYY-MM-DD):");
        String fechaAlquiler = sc.nextLine();
        System.out.println("Introduce la fecha de vencimiento (YYYY-MM-DD):");
        String fechaVencimiento = sc.nextLine();
        System.out.println("Introduce el precio del alquiler:");
        double precioAlquiler = sc.nextDouble();

        String sql = "INSERT INTO DatosAlquila (CorreoElectronico, IDPelicula, FechaAlquiler, FechaVencimiento, PrecioAlquiler) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            pstmt.setInt(2, idPelicula);
            pstmt.setDate(3, Date.valueOf(fechaAlquiler));
            pstmt.setDate(4, Date.valueOf(fechaVencimiento));
            pstmt.setDouble(5, precioAlquiler);
            pstmt.executeUpdate();
            System.out.println("Alquiler registrado con éxito.");
        }
    }

    static void insertarDatosTurno(Connection conn) throws SQLException {

        String insertDatosTurno = "INSERT INTO DatosTurno (NombreTurno, HoraEntrada, HoraSalida, SueldoHora, SueldoTotal) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(insertDatosTurno);
        stmt.setString(1, "Turno Matutino");
        stmt.setTime(2, Time.valueOf("08:00:00"));
        stmt.setTime(3, Time.valueOf("16:00:00"));
        stmt.setFloat(4, 5.0f);
        stmt.setFloat(5, 1200.0f);
        stmt.executeUpdate();


        String insertDatosTurno2 = "INSERT INTO DatosTurno (NombreTurno, HoraEntrada, HoraSalida, SueldoHora, SueldoTotal) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt1 = conn.prepareStatement(insertDatosTurno2);
        stmt1.setString(1, "Turno Vespertino");
        stmt1.setTime(2, Time.valueOf("16:00:00"));
        stmt1.setTime(3, Time.valueOf("00:00:00"));
        stmt1.setFloat(4, 6.5f);
        stmt1.setFloat(5, 1560.0f);
        stmt1.executeUpdate();


        String insertDatosTurno3 = "INSERT INTO DatosTurno (NombreTurno, HoraEntrada, HoraSalida, SueldoHora, SueldoTotal) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt2 = conn.prepareStatement(insertDatosTurno3);
        stmt2.setString(1, "Turno Nocturno");
        stmt2.setTime(2, Time.valueOf("00:00:00"));
        stmt2.setTime(3, Time.valueOf("08:00:00"));
        stmt2.setFloat(4, 8.0f);
        stmt2.setFloat(5, 1920.0f);
        stmt2.executeUpdate();

        
    }
    
    public static void darAltaEmpleado(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduzca su DNI:");
        String dni = sc.nextLine();
        System.out.println("Introduzca su nombre:");
        String nombre = sc.nextLine();
        System.out.println("Introduzca sus apellidos:");
        String apellidos = sc.nextLine();
        System.out.println("Introduzca su telefono:");
        String telefono = sc.nextLine();
        System.out.println("Introduzca su direccion:");
        String direccion = sc.nextLine();
        int opcion = -1;
        String turno = null;

        while (opcion != 3) {
            
            System.out.println("Selecciona una de las siguientes opciones para su turno:");
            System.out.println("1. Turno Matutino.");
            System.out.println("2. Turno Vespertino.");
            System.out.println("3. Turno Nocturno.");

            opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    turno="Turno Matutino";
                    break;
                case 2:
                    turno="Turno Vespertino";
                    break;
                case 3:
                    turno="Turno Nocturno";
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }

        String sql = "INSERT INTO DatosEmpleado (DNI, Nombre, Apellidos, Telefono, Direccion, NombreTurno) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, dni);
        pstmt.setString(2, nombre);
        pstmt.setString(3, apellidos);
        pstmt.setString(4, telefono);
        pstmt.setString(5, direccion);
        pstmt.setString(6, turno);
        pstmt.executeUpdate();

    }

    public static void modificarEmpleado(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduzca el DNI del empleado que quieras modificar:");
        String dni = sc.nextLine();

        System.out.println("Introduzca su nombre:");
        String nombre = sc.nextLine();
        System.out.println("Introduzca sus apellidos:");
        String apellidos = sc.nextLine();
        System.out.println("Introduzca su telefono:");
        String telefono = sc.nextLine();
        System.out.println("Introduzca su direccion:");
        String direccion = sc.nextLine();
        int opcion = -1;
        String turno;
        while (opcion != 3) {
            
            System.out.println("Seleccione una de las siguientes opciones para su turno:");
            System.out.println("1. Turno Matutino.");
            System.out.println("2. Turno Vespertino.");
            System.out.println("3. Turno Nocturno.");

            opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    turno="Turno Matutino";
                    break;
                case 2:
                    turno="Turno Vespertino";
                    break;
                case 3:
                    turno="Turno Nocturno";
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }

        String sql = "UPDATE DatosEmpleado SET Nombre=nombre, Apellidos=apellidos, Telefono=telefono, Direccion=direccion, NombreTurno=turno WHERE DNI=dni";
    }

    public static void darBajaEmpleado(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduzca el DNI del empleado que quieras dar de baja:");
        String dni = sc.nextLine();

        Date fecha = null;
        fecha.getTime();

        String sql = "UPDATE DatosEmpleado SET FechaBaja=fecha WHERE DNI=dni";
    }

    public static void mostrarEmpleado(Connection conn, Scanner sc) throws SQLException {

        String sql = "SELECT * FROM DatosEmpleado";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("Listado de empleados:");
        System.out.println("DNI\tNombre\tApellidos\tTelefono\tSueldo\tDireccion\tTurno\tFecha Baja");
        System.out.println("-----------------------------------------------");
        while (rs.next()) {
            System.out.println(rs.getString("DNI") + "\t" +
                    rs.getString("Nombre") + "\t" +
                    rs.getString("Apellidos") + "\t" +
                    rs.getString("Telefono") + "\t" +
                    rs.getDouble("Sueldo") + "\t" +
                    rs.getString("Direccion") + "\t" +
                    rs.getString("NombreTurno") + "\t" +
                    rs.getDate("FechaBaja"));
        }
    }

    public static void buscarEmpleado(Connection conn, Scanner sc) throws SQLException {

        int opcion = -1;
        while (opcion != 3) {

            System.out.println("Seleccione una de las siguientes opciones para su busqueda:");
            System.out.println("1. Busqueda por nombre.");
            System.out.println("2. Busqueda por apellidoS.");
            System.out.println("3. Busqueda por DNI.");

            opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    System.out.println("Introduzca el nombre del empleado:");
                    String nombre = sc.nextLine();
                    String sql = "SELECT * FROM DatosEmpleado WHERE Nombre=nombre";

                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    System.out.println("Resultados de empleados buscados por nombre:");
                    System.out.println("DNI\tNombre\tApellidos\tTelefono\tSueldo\tDireccion\tTurno\tFecha Baja");
                    System.out.println("-----------------------------------------------");
                    while (rs.next()) {
                        System.out.println(rs.getString("DNI") + "\t" +
                                rs.getString("Nombre") + "\t" +
                                rs.getString("Apellidos") + "\t" +
                                rs.getString("Telefono") + "\t" +
                                rs.getDouble("Sueldo") + "\t" +
                                rs.getString("Direccion") + "\t" +
                                rs.getString("NombreTurno") + "\t" +
                                rs.getDate("FechaBaja"));
                    }
                    break;
                case 2:
                    System.out.println("Introduzca los apellidos del empleado:");
                    String apellidos = sc.nextLine();
                    String sql1 = "SELECT * FROM DatosEmpleado WHERE Apellidos=apellidos";

                    Statement stmt1 = conn.createStatement();
                    ResultSet rs1 = stmt1.executeQuery(sql1);

                    System.out.println("Resultados de empleados buscados por apellidos:");
                    System.out.println("DNI\tNombre\tApellidos\tTelefono\tSueldo\tDireccion\tTurno\tFecha Baja");
                    System.out.println("-----------------------------------------------");
                    while (rs1.next()) {
                        System.out.println(rs1.getString("DNI") + "\t" +
                                rs1.getString("Nombre") + "\t" +
                                rs1.getString("Apellidos") + "\t" +
                                rs1.getString("Telefono") + "\t" +
                                rs1.getDouble("Sueldo") + "\t" +
                                rs1.getString("Direccion") + "\t" +
                                rs1.getString("NombreTurno") + "\t" +
                                rs1.getDate("FechaBaja"));
                    }
                    break;
                case 3:
                    System.out.println("Introduzca el DNI del empleado:");
                    String dni = sc.nextLine();
                    String sql2 = "SELECT * FROM DatosEmpleado WHERE DNI=dni";

                    Statement stmt2 = conn.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(sql2);

                    System.out.println("Resultados de empleados buscados por DNI:");
                    System.out.println("DNI\tNombre\tApellidos\tTelefono\tSueldo\tDireccion\tTurno\tFecha Baja");
                    System.out.println("-----------------------------------------------");
                    while (rs2.next()) {
                        System.out.println(rs2.getString("DNI") + "\t" +
                                rs2.getString("Nombre") + "\t" +
                                rs2.getString("Apellidos") + "\t" +
                                rs2.getString("Telefono") + "\t" +
                                rs2.getDouble("Sueldo") + "\t" +
                                rs2.getString("Direccion") + "\t" +
                                rs2.getString("NombreTurno") + "\t" +
                                rs2.getDate("FechaBaja"));
                    }
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }
    }

    public static void insertarPelicula(Connection conn) throws SQLException, ParseException {
        conn.setAutoCommit(false);
        Savepoint saveInsertPelicula= conn.setSavepoint();
        String sqlInsertPelicula= "INSERT INTO DatosPelicula (Nombre, Precio, FechaEstreno, FechaAlta, Sinopsis) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement insertPelicula= conn.prepareStatement(sqlInsertPelicula);
        Scanner sc= new Scanner(System.in);
        System.out.println("Introduce el nombre de la pelicula");
        insertPelicula.setString(1, sc.nextLine());
        System.out.println("Intoduce el precio");
        double precio=sc.nextDouble();
        while (precio<0){
            System.out.println("Intoduce el precio correctamente");
            precio=sc.nextDouble();
        }
        insertPelicula.setDouble(2, precio);
        LocalDate fechaAlta = LocalDate.now();
        insertPelicula.setDate(4, Date.valueOf(fechaAlta));
        System.out.println("Selecciona el dato a introducir o finaliza:\n1.Fecha Estreno\n2.Sinopsis\n3.Finalizar");
        int opcion=sc.nextInt();
        insertPelicula.setDate(3, null);
        insertPelicula.setString(5, null);
        while (opcion!=3){
            if(opcion==1) {
                System.out.println("Introduce la fecha");
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                String fechaString = sc.nextLine();
                // Convertir el String a Date
                Date fechaEstreno = (Date) formato.parse(fechaString);
                insertPelicula.setDate(3, fechaEstreno);
            }
            else if(opcion==2){
                System.out.println("Introduce la sinopsis");
                insertPelicula.setString(5, sc.nextLine());
            }
            System.out.println("Selecciona el dato a introducir o finaliza:\n1.Fecha Estreno\n2.Sinopsis\n3.Finalizar");
            opcion = sc.nextInt();
        }
        insertPelicula.execute();
        mostrarPelicula(conn, -1);
        System.out.println("¿Quieres confirmar los cambios?\n1. Si\n2. No");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                break;
            case 2:
                conn.rollback(saveInsertPelicula);
                break;
        }
        conn.setAutoCommit(true);
    }

    public static boolean comprobarIdPelicula(Connection conn, int idPelicula) throws SQLException{
        String sqlComprobar = "SELECT COUNT(*) FROM DatosPelicula WHERE IDPelicula = ?";
        PreparedStatement comprobar = conn.prepareStatement(sqlComprobar);
        comprobar.setInt(1, idPelicula);
        ResultSet resultComprobar = comprobar.executeQuery();
        if(resultComprobar.next()){
            if (resultComprobar.getInt(1)!=1){
                System.out.println("Id incorrecto. Se cancela la operacion");
                return false;
            }
            else return true;
        }
        else return false;
    }

    public static boolean comprobarBajaPelicula(Connection conn, int idpelicula) throws SQLException{
        String sqlComprobarBaja = "SELECT FechaBaja FROM DatosPelicula WHERE IDPelicula = ?";
        PreparedStatement comprobarBaja = conn.prepareStatement(sqlComprobarBaja);
        comprobarBaja.setInt(1, idpelicula);
        ResultSet resultComprobarBaja = comprobarBaja.executeQuery();

        if (resultComprobarBaja.next()) {
            Date fechaBaja = resultComprobarBaja.getDate("FechaBaja");
            return fechaBaja != null;
        }
        return true;
    }
    public static void bajaPelicula(Connection conn) throws SQLException{
        conn.setAutoCommit(false);
        Savepoint saveBajaPelicula=conn.setSavepoint();
        Scanner sc= new Scanner(System.in);
        System.out.println("Introduce la id de la pelicula a dar de baja");
        int idPelicula= sc.nextInt();
        if(!comprobarIdPelicula(conn, idPelicula)){
            conn.rollback(saveBajaPelicula);
            conn.setAutoCommit(true);
            return;
        }
        String sqlBaja = "UPDATE DatosPelicula SET FechaBaja = ? WHERE IDPelicula = ?";
        PreparedStatement baja = conn.prepareStatement(sqlBaja);
        LocalDate fechaBaja = LocalDate.now();
        baja.setDate(1, Date.valueOf(fechaBaja));
        baja.setInt(2, idPelicula);
        baja.execute();
        System.out.println("¿Quieres confirmar la baja de la pelicula con id: " + idPelicula + " ?\n1. Si\n2. No");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                break;
            case 2:
                conn.rollback(saveBajaPelicula);
                break;
        }
        conn.setAutoCommit(true);
    }

    public static void modificarPelicula(Connection conn) throws SQLException, ParseException {
        conn.setAutoCommit(false);
        Savepoint saveUpdatePelicula=conn.setSavepoint();
        Scanner sc= new Scanner(System.in);
        System.out.println("Introduce la id de la pelicula a dar de baja");
        int idPelicula= sc.nextInt();
        if(!comprobarIdPelicula(conn, idPelicula)) {
            conn.rollback(saveUpdatePelicula);
            conn.setAutoCommit(true);
            return;
        }
        if(comprobarBajaPelicula(conn, idPelicula)){
            System.out.println("La pelicula esta dada de baja.");
            return;
        }
        System.out.println("¿Que campo desea modificar?:\n1. Nombre\n2. Precio\n3. Fecha Estreno\n4. Sinopsis\n5. Finalizar");
        int opcion = sc.nextInt();
        PreparedStatement updatePelicula;
        String sqlUpdatePelicula;
        while (opcion!=5) {
            switch (opcion) {
                case 1:
                    sqlUpdatePelicula = "UPDATE DatosPelicula SET Nombre = ? WHERE IDPelicula = ?";
                    updatePelicula = conn.prepareStatement(sqlUpdatePelicula);
                    updatePelicula.setInt(2, idPelicula);
                    System.out.println("Introduce el nombre");
                    updatePelicula.setString(1, sc.nextLine());
                    updatePelicula.execute();
                    break;
                case 2:
                    sqlUpdatePelicula = "UPDATE DatosPelicula SET Precio = ? WHERE IDPelicula = ?";
                    updatePelicula = conn.prepareStatement(sqlUpdatePelicula);
                    updatePelicula.setInt(2, idPelicula);
                    System.out.println("Introduce el precio");
                    double precio = sc.nextDouble();
                    while (precio < 0) {
                        System.out.println("Intoduce el precio correctamente");
                        precio = sc.nextDouble();
                    }
                    updatePelicula.setDouble(1, precio);
                    updatePelicula.execute();
                    break;
                case 3:
                    sqlUpdatePelicula = "UPDATE DatosPelicula SET FechaEstreno = ? WHERE IDPelicula = ?";
                    updatePelicula = conn.prepareStatement(sqlUpdatePelicula);
                    updatePelicula.setInt(2, idPelicula);
                    System.out.println("Introduce la Fecha de Estreno");
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaString = sc.nextLine();
                    // Convertir el String a Date
                    Date fechaEstreno = (Date) formato.parse(fechaString);
                    updatePelicula.setDate(1, fechaEstreno);
                    updatePelicula.execute();
                    break;
                case 4:
                    sqlUpdatePelicula = "UPDATE DatosPelicula SET Sinopsis = ? WHERE IDPelicula = ?";
                    updatePelicula = conn.prepareStatement(sqlUpdatePelicula);
                    updatePelicula.setInt(2, idPelicula);
                    System.out.println("Introduce la sinopsis");
                    updatePelicula.setString(1, sc.nextLine());
                    updatePelicula.execute();
                    break;
            }
            System.out.println("¿Que campo desea modificar?:\n1. Nombre\n2. Precio\n3. Fecha Estreno\n4. Sinopsis\n5. Finalizar");
            opcion = sc.nextInt();
        }

        String sqlSelect = "SELECT * FROM DatosPelicula WHERE IDPelicula = ?";
        PreparedStatement select = conn.prepareStatement(sqlSelect);
        select.setInt(1, idPelicula);
        ResultSet resultSet = select.executeQuery();
        if (resultSet.next()) {

            String nombre = resultSet.getString("Nombre");
            double precioSelect = resultSet.getDouble("Precio");
            Date fechaEstreno = resultSet.getDate("FechaEstreno");
            Date fechaAltaSelect = resultSet.getDate("FechaAlta");
            Date fechaBaja = resultSet.getDate("FechaBaja");
            String sinopsisSelect = resultSet.getString("Sinopsis");
            double calificacion = resultSet.getDouble("Calificacion");
            int reseñas = resultSet.getInt("Reseñas");


            // Mostrar la información de la última película
            System.out.println("IDPelicula: " + idPelicula);
            System.out.println("Nombre: " + nombre);
            System.out.println("Precio: " + precioSelect);
            System.out.println("Fecha Estreno: " + fechaEstreno);
            System.out.println("Fecha Alta: " + fechaAltaSelect);
            System.out.println("Fecha Baja" + fechaBaja);
            System.out.println("Sinopsis: " + sinopsisSelect);
            System.out.println("Calificacion: " + calificacion);
            System.out.println("Reseñas: " + reseñas);

        }
        System.out.println("¿Quieres confirmar los cambios?\n1. Si\n2. No");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                break;
            case 2:
                conn.rollback(saveUpdatePelicula);
                break;
        }
        conn.setAutoCommit(true);

    }
    public static void insertarGenero(Connection conn) throws SQLException {
        System.out.println("Introduce el nombre del genero");
        String sqlInsertGenero = "INSERT INTO DatosGenero (Nombre) Values (?)";
        PreparedStatement insertGenero = conn.prepareStatement(sqlInsertGenero);
        Scanner sc = new Scanner(System.in);
        insertGenero.setString(1, sc.nextLine());
        insertGenero.execute();
    }
    public static void mostrarGenero(Connection conn) throws SQLException{
        String sqlSelect = "SELECT * FROM DatosGenero";
        PreparedStatement select = conn.prepareStatement(sqlSelect);
        ResultSet resultSelect = select.executeQuery();
        while(resultSelect.next()){
            System.out.println("ID: " + resultSelect.getInt("IDGenero") + " Nombre: " + resultSelect.getString("Nombre"));
        }
    }
    public static void insertarActor(Connection conn) throws SQLException{
        System.out.println("Introduce el nombre del Actor");
        String sqlInsertActor = "INSERT INTO Actores (NombreActor) Values (?)";
        PreparedStatement insertActor = conn.prepareStatement(sqlInsertActor);
        Scanner sc = new Scanner(System.in);
        insertActor.setString(1, sc.nextLine());
        insertActor.execute();
    }
    public static void mostrarActor(Connection conn) throws SQLException{
        String sqlSelect = "SELECT * FROM Actores";
        PreparedStatement select = conn.prepareStatement(sqlSelect);
        ResultSet resultSelect = select.executeQuery();
        while(resultSelect.next()){
            System.out.println("Nombre: " + resultSelect.getString("NombreActor"));
        }
    }

    public static void mostrarGenerosPelicula(Connection conn, int idPelicula) throws SQLException{
        String sqlSelect = "SELECT IDGenero FROM PerteneceA WHERE IDPelicula = ?";
        PreparedStatement select = conn.prepareStatement(sqlSelect);
        ResultSet resultSelect = select.executeQuery();
        sqlSelect = "SELECT Nombre FROM DatosGenero WHERE IDGenero = ?";
        select = conn.prepareStatement(sqlSelect);
        ResultSet resultSelectGenero;
        System.out.println("Generos a los que pertenece la pelicula con id: " + idPelicula);
        while (resultSelect.next()){
            select.setInt(1, resultSelect.getInt("IDGenero"));
            resultSelectGenero = select.executeQuery();
            if(resultSelectGenero.next()){
                System.out.println(resultSelectGenero.getString("Nombre"));
            }
        }
    }
    public static void mostrarActoresPelicula(Connection conn, int idPelicula) throws SQLException{
        String sqlSelect = "SELECT NombreActor FROM Actua WHERE IDPelicula = ?";
        PreparedStatement select = conn.prepareStatement(sqlSelect);
        ResultSet resultSelect = select.executeQuery();
        System.out.println("Los actores que participan en esta pelicula con id: " + idPelicula + " son:");
        while (resultSelect.next()){
            System.out.println(resultSelect.getString("NombreActor"));
        }
    }
    public static void añadirGeneroActor(Connection conn) throws SQLException{
        conn.setAutoCommit(false);
        Savepoint saveAñadir = conn.setSavepoint();
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce el id de la pelicula");
        int idPelicula = sc.nextInt();
        if(!comprobarIdPelicula(conn, idPelicula)||comprobarBajaPelicula(conn, idPelicula)){
            System.out.println("El id es incorrecto o esta dada de baja");
            return;
        }
        System.out.println("Selecciona una opcion:\n1. Añadir genero\n2. Añadir Actor\n3. Mostrar Generos\n4. Mostrar Actores\n5. Finalizar");
        int opcion = sc.nextInt();
        String sqlInsert;
        PreparedStatement insert;
        String sqlComprobar;
        PreparedStatement comprobar;
        ResultSet resultComprobar;
        while (opcion!=5){
            switch (opcion){
                case 1:
                    sqlInsert = "INSERT INTO PerteneceA (IDPelicula, IDGenero) VALUES (?, ?)";
                    insert = conn.prepareStatement(sqlInsert);
                    insert.setInt(1, idPelicula);
                    System.out.println("Introduce el genero");
                    int idGenero = sc.nextInt();
                    sqlComprobar = "SELECT COUNT(*) FROM DatosGenero WHERE IDGenero = ?";
                    comprobar = conn.prepareStatement(sqlComprobar);
                    comprobar.setInt(1, idGenero);
                    resultComprobar = comprobar.executeQuery();
                    if(resultComprobar.next()){
                        if (resultComprobar.getInt(1)!=1){
                            System.out.println("Id incorrecto. Se cancela la operacion");
                            break;
                        }
                    }
                    insert.setInt(2, idGenero);
                    insert.execute();
                    break;
                case 2:
                    sqlInsert = "INSERT INTO Actua (IDPelicula, NombreActor) VALUES (?, ?)";
                    insert = conn.prepareStatement(sqlInsert);
                    insert.setInt(1, idPelicula);
                    System.out.println("Introduce el nombre del actor");
                    String nombreActor = sc.nextLine();
                    sqlComprobar = "SELECT COUNT(*) FROM Actores WHERE NombreActor = ?";
                    comprobar = conn.prepareStatement(sqlComprobar);
                    comprobar.setString(1, nombreActor);
                    resultComprobar = comprobar.executeQuery();
                    if(resultComprobar.next()){
                        if (resultComprobar.getInt(1)!=1){
                            System.out.println("Nombre incorrecto. Se cancela la operacion");
                            break;
                        }
                    }
                    insert.setString(2, nombreActor);
                    insert.execute();
                    break;
                case 3:
                    mostrarGenero(conn);
                    break;
                case 4:
                    mostrarActor(conn);
                    break;
            }
            System.out.println("Selecciona una opcion:\n1. Añadir genero\n2. Añadir Actor\n3. Mostrar Generos\n4. Mostrar Actores\n5. Finalizar");
            opcion = sc.nextInt();
        }
        mostrarActoresPelicula(conn, idPelicula);
        mostrarGenerosPelicula(conn, idPelicula);
        System.out.println("¿Quieres confirmar los cambios?\n1. Si\n2. No");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                break;
            case 2:
                conn.rollback(saveAñadir);
                break;
        }
        conn.setAutoCommit(true);
    }

    public static void mostrarPelicula(Connection conn, int idPelicula) throws SQLException{
        String sqlSelect;
        PreparedStatement select;
        if(idPelicula==-1){
            sqlSelect = "SELECT * FROM DatosPelicula ORDER BY IDPelicula DESC FETCH FIRST 1 ROW ONLY";
            select = conn.prepareStatement(sqlSelect);
        }else {
            sqlSelect = "SELECT * FROM DatosPelicula WHERE IDPelicula = ?";
            select = conn.prepareStatement(sqlSelect);
            select.setInt(1, idPelicula);
        }
        ResultSet resultSet = select.executeQuery();
        if (resultSet.next()) {

            System.out.println("IDPelicula: " + resultSet.getInt("IDPelicula"));
            System.out.println("Nombre: " + resultSet.getString("Nombre"));
            System.out.println("Precio: " + resultSet.getDouble("Precio"));
            System.out.println("Fecha Estreno: " + resultSet.getDate("FechaEstreno"));
            System.out.println("Fecha Alta: " + resultSet.getDate("FechaAlta"));
            System.out.println("Fecha Baja" + resultSet.getDate("FechaBaja"));
            System.out.println("Sinopsis: " + resultSet.getString("Sinopsis"));
            System.out.println("Calificacion: " + resultSet.getDouble("Calificacion"));
            System.out.println("Reseñas: " + resultSet.getInt("Reseñas"));

        }
    }
    public static void salir(Connection conn) {
        System.out.println("Saliendo...");
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}