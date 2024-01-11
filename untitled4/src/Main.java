import java.nio.channels.SelectableChannel;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.time.LocalDate;
import java.sql.Date;



public class Main {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;

        try {
            conn = conectarBaseDeDatos(sc);
            int opcion = -1;

            while (opcion != 7) {
                System.out.println("Selecciona una de las siguientes opciones:");
                System.out.println("1. Borrar y crear tablas.");
                System.out.println("2. Menu Pelicula.");
                System.out.println("3. Mostrar películas.");
                System.out.println("4. Simular inserción de alquiler.");
                System.out.println("5. Dar alta a un cliente.");
                 System.out.println("6. Menu de empleado.");
                System.out.println("7. Salir.");

                opcion = sc.nextInt();
                sc.nextLine();
                switch (opcion) {
                    case 1:
                        insertarDatosEjemplo(conn);
                        break;
                    case 2:
                        Pelicula.menuPelicula(conn);
                        break;
                    case 3:
                        //mostrarPeliculas(conn);
                        break;
                    case 4:
                        Alquiler.simularInsercionAlquiler(conn, sc);
                        break;
                    case 5:
                        menuCliente(conn, sc);
                        break;
                    case 6:
                        menuEmpleados(conn,sc);
                        break;
                    case 7:
                        salir(conn);
                        break;
                    default:
                        System.out.println("Opción no válida.");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            if (!conn.isClosed()) {
                try {
                    conn.close();
                    System.out.println("Conexion cerrada por error en la aplicacion");
                } catch (SQLException e) {
                    System.out.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    public static Date obtenerFechaDesdeScanner(Scanner scanner) throws ParseException {
        System.out.print("Ingrese la fecha (formato dd/MM/yyyy): ");
        String fechaString = scanner.nextLine();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date utilDate = dateFormat.parse(fechaString);
        return new Date(utilDate.getTime());
    }

    public static Connection conectarBaseDeDatos(Scanner sc) throws SQLException {
        System.out.println("Introduce el usuario:");
        String usuario = sc.next();
        System.out.println("Introduce la contraseña:");
        String contrasenia = sc.next();
        String url = "jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        Connection conn=null;
        try {
            conn = DriverManager.getConnection
                    (url, usuario, contrasenia);
        }
        catch (SQLException e){
            //Si el fallo es un usuario o contraseña incorrecto se da un intento mas
            if(e.getErrorCode()==1017){
                sc.nextLine();
                System.out.println("Fallo al iniciar sesion: usuario o contraseña incorrectos");
                System.out.println("Introduce el usuario");
                usuario= sc.nextLine();
                System.out.println("Introduce la contraseña");
                contrasenia= sc.nextLine();
                try {
                    conn = DriverManager.getConnection
                            ("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", usuario, contrasenia);
                }
                catch(SQLException e1){
                    System.err.println(e1.getMessage());
                    System.exit(-1);
                }
            }
            else{
                System.err.println(e.getMessage());
                System.exit(-2);
            }
        }

        System.out.println("Conexion realizada");
        return conn;
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
    //No hace falta el telefono si o si y hay que comprobar que no exista el correo y quitar el try
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


    static void menuEmpleados(Connection conn, Scanner sc) throws SQLException{

        System.out.println("Bienvenido al menu de empleado:");
        int opcion = -1;


        while (opcion != 5) {

            System.out.println("Selecciona una de las siguientes opciones del empleado:");
            System.out.println("1. Dar alta a un empleado.");
            System.out.println("2. Dar baja a un empleado.");
            System.out.println("3. Modificar un empleado.");
            System.out.println("4. Mostrar empleados.");
            System.out.println("5. Buscar un empleado(Por Nombre, Apellidos o DNI).");

            opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    darAltaEmpleado(conn,sc);
                    break;
                case 2:
                    darBajaEmpleado(conn,sc);
                    break;
                case 3:
                    modificarEmpleado(conn,sc);
                    break;
                case 4:
                    mostrarEmpleado(conn,sc);
                    break;
                case 5:
                    buscarEmpleado(conn,sc);
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
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
    //No hace falta pedir telefono y se deberia comprobar que no exista un empleado con ese dni ya, el while del turno esta mal
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

    //Esta mal entero segun esto se debe modificar todos los campos siempre
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
        String turno=null;
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
        
        String sql = "UPDATE DatosEmpleado SET Nombre=?, Apellidos=?, Telefono=?, Direccion=?, NombreTurno=? WHERE DNI=?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nombre);
        pstmt.setString(2, apellidos);
        pstmt.setString(3, telefono);
        pstmt.setString(4, direccion);
        pstmt.setString(5, turno);
        pstmt.setString(6, dni);
        pstmt.executeUpdate();
        
     }

    //Esta mal la obtencion de la fecha y deberia comprobarse si existe el empleado y si esta dado de baja
    public static void darBajaEmpleado(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduzca el DNI del empleado que quieras dar de baja:");
        String dni = sc.nextLine();

        Date fecha = null;
        fecha.getTime();

        String sql = "UPDATE DatosEmpleado SET FechaBaja=CURRENT_DATE WHERE DNI=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, dni);
        pstmt.executeUpdate();
        
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
    //El while esta mal
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
                    String sql = "SELECT * FROM DatosEmpleado WHERE Nombre=?";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nombre);
                    ResultSet rs = pstmt.executeQuery(sql);

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
                    String sql1 = "SELECT * FROM DatosEmpleado WHERE Apellidos=?";

                    PreparedStatement pstmt1 = conn.prepareStatement(sql1);
                    pstmt1.setString(1, apellidos);
                    ResultSet rs1 = pstmt1.executeQuery(sql1);

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
                    String sql2 = "SELECT * FROM DatosEmpleado WHERE DNI=?";

                    PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                    pstmt2.setString(1, dni);
                    ResultSet rs2 = pstmt2.executeQuery(sql2);

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

    //Comprobar baja y correo existente
    public static void darBajaCliente(Connection conn, Scanner sc) throws SQLException {                               
                                                                                                                   
    System.out.println("Introduzca el correo electronico del cliente que quieras dar de baja:");                   
    String correo;                                                                                                 
    correo = sc.nextLine();                                                                                        
    correo = sc.nextLine();                                                                                        
                                                                                                                   
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
    public static void salir(Connection conn) {
        System.out.println("Saliendo...");
        try {
            if (!conn.isClosed()) {
                conn.close();
                System.out.println("Conexion cerrada");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
