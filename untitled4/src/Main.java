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
                        Cliente.menuCliente(conn, sc);
                        break;
                    case 6:
                        Empleado.menuEmpleados(conn,sc);
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

    public static Date obtenerFechaDesdeScanner(Connection conn, Scanner scanner)  {


        java.util.Date utilDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fechaString;
        do {
            scanner.nextLine();
            System.out.print("Ingrese la fecha (formato dd/MM/yyyy): ");
            fechaString = scanner.nextLine();
            try {
                utilDate = dateFormat.parse(fechaString);
            } catch (ParseException e) {
                System.out.println("Fecha en formato incorrecto.\nSi quieres salir del programa introduce 1, si no introduce otro numero:");
                if (scanner.nextInt() == 1) {
                    salir(conn);
                    System.out.println("Cierre aplicacion fecha incorrecta");
                    System.exit(-3);
                }
            }
        }while (utilDate==null);
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
    static void ejecutarSQL(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
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
