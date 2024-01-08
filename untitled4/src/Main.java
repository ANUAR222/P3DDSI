


import java.sql.*;

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

            while (opcion != 5) {
                System.out.println("Selecciona una de las siguientes opciones:");
                System.out.println("1. Borrar y crear tablas.");
                System.out.println("2. Dar alta película.");
                System.out.println("3. Mostrar películas.");
                System.out.println("4. Simular inserción de alquiler.");
                System.out.println("5. Salir.");

                opcion = sc.nextInt();
                switch (opcion) {
                    case 1:
                        insertarDatosEjemplo(conn);
                        break;
                    case 2:
                        darAltaPelicula(conn, sc);
                        break;
                    case 3:
                        mostrarPeliculas(conn);
                        break;
                    case 4:
                        simularInsercionAlquiler(conn, sc);
                        break;
                    case 5:
                        darAltaCliente(conn, sc);
                        break;
                    case 6:
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

    public static void darAltaPelicula(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduce el nombre de la película:");
        String nombre = sc.nextLine();
        System.out.println("Introduce el precio de la película:");
        double precio = sc.nextDouble();
        sc.nextLine();  // Consumir el salto de línea pendiente
        //fecha de estreno a la actual y la de vencimiento dentro de un año
        LocalDate fechaEstreno = LocalDate.now();
        System.out.println("Introduce la sinopsis de la película:");
        String sinopsis = sc.nextLine();

        String sql = "INSERT INTO DatosPelicula (Nombre, Precio, FechaEstreno, Sinopsis) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setDouble(2, precio);
            pstmt.setDate(3, Date.valueOf(fechaEstreno));
            pstmt.setString(4, sinopsis);
            pstmt.executeUpdate();
            System.out.println("Película añadida con éxito.");
        }
    }

    public static void mostrarPeliculas(Connection conn) throws SQLException {
        String sql = "SELECT IDPelicula, Nombre, Precio, FechaEstreno FROM DatosPelicula";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("Listado de películas:");
            System.out.println("ID\tNombre\t\tPrecio\tFechaEstreno");
            System.out.println("-----------------------------------------------");
            while (rs.next()) {
                System.out.println(rs.getInt("IDPelicula") + "\t" +
                        rs.getString("Nombre") + "\t\t" +
                        rs.getDouble("Precio") + "\t" +
                        rs.getDate("FechaEstreno"));
            }
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
        String turno;
        while (opcion != 3) {
            
            System.out.println("Selecciona una de las siguientes opciones para su turno:");
            System.out.println("1. Turno Matutino.");
            System.out.println("2. Turno de Tarde.");
            System.out.println("3. Turno Nocturno.");

            opcion = sc.nextInt();
                switch (opcion) {
                    case 1:
                        turno="Turno Matutino";
                        break;
                    case 2:
                        turno="Turno de Tarde";
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

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dni);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellidos);
            pstmt.setString(4, telefono);
            pstmt.setDate(5, direccion);
            pstmt.setDate(6, turno);
            pstmt.executeUpdate();
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
