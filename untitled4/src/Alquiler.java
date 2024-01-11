import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;
@SuppressWarnings("SqlResolve")
public class Alquiler {
    public static void simularInsercionAlquiler(Connection conn, Scanner sc) throws SQLException {
        while (true) {
            System.out.println("Seleccione una acción:");
            System.out.println("1. Registrar nuevo alquiler");
            System.out.println("2. Extender alquiler");
            System.out.println("3. Acceder a película alquilada");
            System.out.println("4. Precomprar película");
            System.out.println("5. Mostrar alquileres");
            System.out.println("6. Volver al menú principal");
            System.out.print("Opción: ");

            int opcion = sc.nextInt();
            sc.nextLine();  // Consumir el salto de línea pendiente

            switch (opcion) {
                case 1:
                    registrarNuevoAlquiler(conn, sc);
                    break;
                case 2:
                    simularExtenderAlquiler(conn, sc);
                    break;
                case 3:
                    simularAccesoPelicula(conn, sc);
                    break;
                case 4:
                    simularPrecompraPelicula(conn, sc);
                    break;
                case 5:
                    mostrarAlquileres(conn);
                case 6:
                    return;  // Salir de la función y volver al menú principal
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                    break;
            }
        }
    }

    //Quitar try
    public static void mostrarAlquileres(Connection conn) throws SQLException {
        String sql = "SELECT * FROM DatosAlquila";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println("Correo electrónico: " + rs.getString("CorreoElectronico"));
            System.out.println("ID de la película: " + rs.getInt("IDPelicula"));
            System.out.println("Fecha de alquiler: " + rs.getDate("FechaAlquiler"));
            System.out.println("Fecha de vencimiento: " + rs.getDate("FechaVencimiento"));
            System.out.println("Fecha de acceso: " + rs.getDate("FechaAcceso"));
            System.out.println("Calificación: " + rs.getDouble("Calificacion"));
            System.out.println();
        }
    }
    //Si el cliente esta de baja el usuario no lo sabe tienes que decirlo, tienes que comprobar si pelicula esta de baja
    //La comparacion de fechas como string no funcionan asi con Date se pueden comparar y usa la funcion para pedir la fecha de Main
    //Quita el try del final si no vas a hacer nada con el catch
    //El precio de alquiler es otra tabla distinta no DatosAlquiler
    public static void registrarNuevoAlquiler(Connection conn, Scanner sc) throws SQLException {
        String correo;
        int idPelicula;
        do {
            do {
                System.out.println("Introduce el correo electrónico del cliente:");
                correo = sc.nextLine();
                if (!comprobarExisteCliente(conn, correo)) {
                    System.out.println("Error: El cliente no existe. introduzca espacio para salir");
                    String salir = sc.nextLine();
                    if (salir.equals(" ")) {
                        return;
                    }
                }
            } while (Cliente.comprobarBajaCliente(conn, correo) || !comprobarExisteCliente(conn, correo));

            do {
                System.out.println("Introduce el ID de la película:");
                idPelicula = sc.nextInt();
                sc.nextLine();  // Consumir el salto de línea pendiente
                if (!comprobarexistePelicula(conn, idPelicula)) {
                    System.out.println("Error: La película no existe. introduzca espacio para salir");
                    String salir = sc.nextLine();
                    if (salir.equals(" ")) {
                        return;
                    }
                }
            } while (!comprobarexistePelicula(conn, idPelicula));
            if (comprobarExisteAlquiler(conn, correo, idPelicula)) {
                System.out.println("Error: El cliente ya tiene esta película alquilada. introduzca espacio para salir");
                String salir = sc.nextLine();
                if (salir.equals(" ")) {
                    return;
                }
            }
        }while (comprobarExisteAlquiler(conn, correo, idPelicula));
        //pon la fecha actual
        String fechaAlquiler = LocalDate.now().toString();
        String fechaVencimiento;
        do {
            System.out.println("Introduce la fecha de vencimiento (YYYY-MM-DD):");
            fechaVencimiento = sc.nextLine();
            if (fechaVencimiento.compareTo(fechaAlquiler) < 0) {
                System.out.println("Error: La fecha de vencimiento debe ser posterior a la fecha de alquiler. introduzca espacio para salir");
                String salir = sc.nextLine();
                if (salir.equals(" ")) {
                    return;
                }
            }
        }while (fechaVencimiento.compareTo(fechaAlquiler) < 0);

        double precioAlquiler = calcular_precio_alquiler(conn, fechaAlquiler, fechaVencimiento, idPelicula);

        String sql = "INSERT INTO DatosAlquiler (CorreoElectronico, IDPelicula, FechaAlquiler, FechaVencimiento, PrecioAlquiler) VALUES (?, ?, ?, ?, ?)";
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
    //Quita el try si no vas a hacer nada con el catch
    public static double calcular_precio_alquiler(Connection conn,String fecha_inc, String fecha_fin, int idPelicula) throws SQLException {
        String sql = "SELECT Precio FROM DatosPelicula WHERE IDPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            double precio = rs.getDouble("Precio")*0.2;
            int dias = (int) ((Date.valueOf(fecha_fin).getTime() - Date.valueOf(fecha_inc).getTime()) / (1000 * 60 * 60 * 24));
            return precio * dias;
        }
    }
    //Quita el try si no vas a hacer nada con el catch
    public static boolean comprobarExisteAlquiler(Connection conn, String correo, int idPelicula) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DatosAlquiler WHERE CorreoElectronico = ? AND IDPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            pstmt.setInt(2, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }
    //Usa mi metodo para no tener varios metodos que hagan lo mismo
    public static boolean comprobarexistePelicula(Connection conn, int idPelicula) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DatosPelicula WHERE IDPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }
    //Lo mismo aqui usa el de cliente no hagas uno nuevo
    public static boolean comprobarExisteCliente(Connection conn, String correo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DatosCliente WHERE CorreoElectronico = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }
    //Comprueba la baja de la pelicula
    //Usa la funcion para pedir la fecha
    public static void simularExtenderAlquiler(Connection conn, Scanner sc) throws SQLException {
        String correo;
        do {
            System.out.println("Introduce el correo electrónico del cliente:");
            correo = sc.nextLine();
            if (!comprobarExisteCliente(conn, correo)) {
                System.out.println("Error: El cliente no existe.");
                return;
            }
        }while (!comprobarExisteCliente(conn, correo) || Cliente.comprobarBajaCliente(conn, correo));
        int idPelicula;
        do {
            System.out.println("Introduce el ID de la película:");
            idPelicula = sc.nextInt();
            sc.nextLine();  // Consumir el salto de línea pendiente
            if (!comprobarexistePelicula(conn, idPelicula)) {
                System.out.println("Error: La película no existe.");
                return;
            }
        }while (!comprobarexistePelicula(conn, idPelicula));

        if (!verificarAlquilerExistente(conn, correo, idPelicula)) {
            System.out.println("Error: El cliente no tiene esta película alquilada.");
            return;
        }
        String nuevaFechaVencimiento;
        do {
            System.out.println("Introduce la nueva fecha de vencimiento (YYYY-MM-DD):");
            nuevaFechaVencimiento = sc.nextLine();
        }while (comprobarFechaVencimiento(conn, correo, idPelicula, nuevaFechaVencimiento));
        extenderFechaAlquiler(conn, correo, idPelicula, nuevaFechaVencimiento);
    }
    //Si usas la funcion de pedir fecha tienes que cambiar la comparacion
    //Quita el try si no vas a hacer nada con el catch
    private static boolean comprobarFechaVencimiento(Connection conn, String correo, int idPelicula, String nuevaFechaVencimiento) throws SQLException {
        String sql = "SELECT FechaVencimiento FROM DatosAlquiler WHERE CorreoElectronico = ? AND IDPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            pstmt.setInt(2, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            Date fechaVencimiento = rs.getDate("FechaVencimiento");
            return fechaVencimiento.after(Date.valueOf(nuevaFechaVencimiento));
        }
    }
    //Quita el try si no vas a hacer nada con el catch
    private static boolean verificarAlquilerExistente(Connection conn, String correo, int idPelicula) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DatosAlquiler WHERE CorreoElectronico = ? AND IDPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            pstmt.setInt(2, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }
    //Si usas la funcion de pedir fecha tienes que cambiar la comparacion
    //Quita el try si no vas a hacer nada con el catch
    private static void extenderFechaAlquiler(Connection conn, String correo, int idPelicula, String nuevaFechaVencimiento) throws SQLException {
        String sql = "UPDATE DatosAlquiler SET FechaVencimiento = ? WHERE CorreoElectronico = ? AND IDPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(nuevaFechaVencimiento));
            pstmt.setString(2, correo);
            pstmt.setInt(3, idPelicula);
            pstmt.executeUpdate();
            System.out.println("Fecha de alquiler extendida con éxito.");
        }
    }

    // Subsistema 2: Acceder a película

    //O verificas aqui o verificas en el verificarAlquiler pero tienes q comprobar el correo e idpelicula
    public static void simularAccesoPelicula(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduce el ID de la película:");
        int idPelicula = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente
        System.out.println("Introduce el correo electrónico del cliente:");
        String correo = sc.nextLine();

        if (!verificarAlquilerExistente(conn, correo, idPelicula)) {
            System.out.println("Error: El cliente no tiene esta película alquilada.");
            return;
        }

        registrarAccesoPelicula(conn, correo, idPelicula);
    }
    //Quita el try si no vas a hacer nada con el catch
    private static void registrarAccesoPelicula(Connection conn, String correo, int idPelicula) throws SQLException {
        String sql = "UPDATE DatosAlquiler SET FechaAcceso = CURRENT_DATE WHERE CorreoElectronico = ? AND IDPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            pstmt.setInt(2, idPelicula);
            pstmt.executeUpdate();
            System.out.println("Acceso a película registrado con éxito.");
        }
    }

    // Subsistema 3: Precompra película
    //O verificas aqui o verificas en el precomprarPelicula pero tienes q comprobar el correo e idpelicula
    static void simularPrecompraPelicula(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduce el id de la película:");
        String id = sc.nextLine();
        System.out.println("Introduce el ID del cliente:");
        String idCliente = sc.nextLine();

        precomprarPelicula(conn, id, idCliente);
    }

    //Si vas a usar el preparedStatement no pongas el valor de la variable en el strind del tiron ponlo con pstm.setInt.....
    //Quita el try si no vas a hacer nada con el catch
    //El precio esta en otra tabla no en esta y no pides fecha vencimiento
    static void precomprarPelicula(Connection conn, String id_pelicula, String idCliente) throws SQLException {
        String fecha_estreno="select FechaEstreno from DatosPelicula where IDPelicula="+id_pelicula;
        try (PreparedStatement pstmt = conn.prepareStatement(fecha_estreno)) {
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            Date fechaEstreno = rs.getDate("FechaEstreno");
            String AniadirAlquiler = "INSERT INTO DatosAlquiler (CorreoElectronico, IDPelicula, FechaAlquiler, FechaVencimiento, PrecioAlquiler) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(AniadirAlquiler)) {
                stmt.setString(1, idCliente);
                stmt.setString(2, id_pelicula);
                stmt.setDate(3, fechaEstreno);
                stmt.setDate(4, Date.valueOf(fechaEstreno.toLocalDate().plusYears(1)));
                stmt.setDouble(5, 0.0);
                stmt.executeUpdate();
            }
        }
    }

}