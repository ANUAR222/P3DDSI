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
                    break;
                case 6:
                    return;  // Salir de la función y volver al menú principal
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                    break;
            }
        }
    }


    public static void mostrarAlquileres(Connection conn) throws SQLException {
        String sql = "SELECT * FROM DatosAlquiler";
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
    //La fecha alquiler tienes que sacarla tu con date.now o algo asi en mi insert puedes copiar mi fechaAlta
    //Datos alquiler no tiene precio va a dar excepcion ese insert
    //Quita los try del final si no vas a hacer nada con el catch

    public static void registrarNuevoAlquiler(Connection conn, Scanner sc) throws SQLException {
        String correo;
        int idPelicula;

        do {
            do {
                System.out.println("Introduce el correo electrónico del cliente:");
                correo = sc.nextLine();
                if (!comprobarExisteCliente(conn, correo)) {
                    System.out.println("Error: El cliente no existe. Introduzca espacio para salir");
                    String salir = sc.nextLine();
                    if (salir.equals(" ")) {
                        return;
                    }
                }
                if (Cliente.comprobarBajaCliente(conn, correo)) {
                    System.out.println("Error: El cliente está de baja. Introduzca espacio para salir");
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
                if (!Pelicula.comprobarIdPelicula(conn, idPelicula)) {
                    System.out.println("Error: La película no existe. Introduzca espacio para salir");
                    String salir = sc.nextLine();
                    if (salir.equals(" ")) {
                        return;
                    }
                }
                if (Pelicula.comprobarBajaPelicula(conn, idPelicula)) {
                    System.out.println("Error: La película está de baja. Introduzca espacio para salir");
                    String salir = sc.nextLine();
                    if (salir.equals(" ")) {
                        return;
                    }
                }
            } while (!Pelicula.comprobarIdPelicula(conn, idPelicula) || Pelicula.comprobarBajaPelicula(conn, idPelicula));

            if (comprobarExisteAlquiler(conn, correo, idPelicula)) {
                System.out.println("Error: El cliente ya tiene esta película alquilada. Introduzca espacio para salir");
                String salir = sc.nextLine();
                if (salir.equals(" ")) {
                    return;
                }
            }

        } while (comprobarExisteAlquiler(conn, correo, idPelicula));

        // Obtener la fecha actual
        Date fechaAlquiler = Main.obtenerFechaDesdeScanner(conn, sc);

        Date fechaVencimiento;
        do {
            fechaVencimiento = Main.obtenerFechaDesdeScanner(conn, sc);
            if (fechaVencimiento.before(fechaAlquiler)) {
                System.out.println("Error: La fecha de vencimiento debe ser posterior a la fecha de alquiler. Introduzca espacio para salir");
                String salir = sc.nextLine();
                if (salir.equals(" ")) {
                    return;
                }
            }
        } while (fechaVencimiento.before(fechaAlquiler));

        double precioAlquiler = calcular_precio_alquiler(conn, fechaAlquiler, fechaVencimiento, idPelicula);

        // Insertar en la tabla PrecioAlquiler
        String sqlPrecioAlquiler = "INSERT INTO PrecioAlquiler (IDPelicula, FechaAlquiler, FechaVencimiento, PrecioAlquiler) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmtPrecio = conn.prepareStatement(sqlPrecioAlquiler)) {
            pstmtPrecio.setInt(1, idPelicula);
            pstmtPrecio.setDate(2, fechaAlquiler);
            pstmtPrecio.setDate(3, fechaVencimiento);
            pstmtPrecio.setDouble(4, precioAlquiler);
            pstmtPrecio.executeUpdate();
        }

        // Insertar en la tabla DatosAlquiler
        String sqlDatosAlquiler = "INSERT INTO DatosAlquiler (CorreoElectronico, IDPelicula, FechaAlquiler, FechaVencimiento, PrecioAlquiler) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmtDatos = conn.prepareStatement(sqlDatosAlquiler)) {
            pstmtDatos.setString(1, correo);
            pstmtDatos.setInt(2, idPelicula);
            pstmtDatos.setDate(3, fechaAlquiler);
            pstmtDatos.setDate(4, fechaVencimiento);
            pstmtDatos.setDouble(5, precioAlquiler);
            pstmtDatos.executeUpdate();
            System.out.println("Alquiler registrado con éxito.");
        }
    }

    //Quita el try si no vas a hacer nada con el catch
    public static double calcular_precio_alquiler(Connection conn, Date fecha_inc, java.util.Date fecha_fin, int idPelicula) throws SQLException {
        String sql = "SELECT Precio FROM DatosPelicula WHERE IDPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            double precio = rs.getDouble("Precio")*0.2;
            int dias = (int) ((fecha_fin.getTime() - fecha_inc.getTime()) / (1000 * 60 * 60 * 24));
            return precio * dias;
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return 0;
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
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    //Esto deberia ir en cliente
    public static boolean comprobarExisteCliente(Connection conn, String correo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DatosCliente WHERE CorreoElectronico = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
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
            if (!Pelicula.comprobarIdPelicula(conn, idPelicula)) {
                System.out.println("Error: La película no existe.");
                return;
            }
        }while (!Pelicula.comprobarIdPelicula(conn, idPelicula) || Pelicula.comprobarBajaPelicula(conn, idPelicula));

        if (!verificarAlquilerExistente(conn, correo, idPelicula)) {
            System.out.println("Error: El cliente no tiene esta película alquilada.");
            return;
        }
        Date nuevaFechaVencimiento;
        do {
            nuevaFechaVencimiento = Main.obtenerFechaDesdeScanner(conn, sc);
            if (nuevaFechaVencimiento.before(Date.valueOf(LocalDate.now()))) {
                System.out.println("Error: La fecha de vencimiento debe ser posterior a la fecha actual.");
            }
            if (!comprobarFechaVencimiento(conn, correo, idPelicula, nuevaFechaVencimiento)) {
                System.out.println("Error: La fecha de vencimiento debe ser posterior a la fecha de vencimiento actual.");
            }
        } while (nuevaFechaVencimiento.before(Date.valueOf(LocalDate.now())) || !comprobarFechaVencimiento(conn, correo, idPelicula, nuevaFechaVencimiento));

        extenderFechaAlquiler(conn, correo, idPelicula, nuevaFechaVencimiento);
    }

    //Quita el try si no vas a hacer nada con el catch
    private static boolean comprobarFechaVencimiento(Connection conn, String correo, int idPelicula, Date nuevaFechaVencimiento) throws SQLException {
        String sql = "SELECT FechaVencimiento FROM DatosAlquiler WHERE CorreoElectronico = ? AND IDPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            pstmt.setInt(2, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            Date fechaVencimiento = rs.getDate("FechaVencimiento");
            return fechaVencimiento.after(nuevaFechaVencimiento);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
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
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    //Quita el try si no vas a hacer nada con el catch
    private static void extenderFechaAlquiler(Connection conn, String correo, int idPelicula, Date nuevaFechaVencimiento) throws SQLException {
        String sql = "UPDATE DatosAlquiler SET FechaVencimiento = ? WHERE CorreoElectronico = ? AND IDPelicula = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, nuevaFechaVencimiento);
            pstmt.setString(2, correo);
            pstmt.setInt(3, idPelicula);
            pstmt.executeUpdate();
            System.out.println("Fecha de alquiler extendida con éxito.");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    // Subsistema 2: Acceder a película

    //O verificas aqui o verificas en el verificarAlquiler pero tienes q comprobar el correo e idpelicula
    public static void simularAccesoPelicula(Connection conn, Scanner sc) throws SQLException {
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
            if (!Pelicula.comprobarIdPelicula(conn, idPelicula)) {
                System.out.println("Error: La película no existe.");
                return;
            }
        }while (!Pelicula.comprobarIdPelicula(conn, idPelicula) || Pelicula.comprobarBajaPelicula(conn, idPelicula));
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
    //La fecha vencimiento tiene que ser mayor a la fecha de estreno si el estreno es en un mes y pones que la fecha vencimiento es mañana te deja insertar el alquiler y estaria mal
    static void simularPrecompraPelicula(Connection conn, Scanner sc) throws SQLException {
        String correo;
        int idPelicula;
        Date fechavenc;
        do {
            System.out.println("Introduce el correo electrónico del cliente:");
            correo = sc.nextLine();
            if (!comprobarExisteCliente(conn, correo)) {
                System.out.println("Error: El cliente no existe.");
                return;
            }
        }while (!comprobarExisteCliente(conn, correo) || Cliente.comprobarBajaCliente(conn, correo));
        do {

            System.out.println("Introduce el ID de la película:");
            idPelicula = sc.nextInt();
            sc.nextLine();  // Consumir el salto de línea pendiente
            if (!Pelicula.comprobarIdPelicula(conn, idPelicula)) {
                System.out.println("Error: La película no existe.");
                return;
            }
        }while (!Pelicula.comprobarIdPelicula(conn, idPelicula) || Pelicula.comprobarBajaPelicula(conn, idPelicula));

        do {
            fechavenc = Main.obtenerFechaDesdeScanner(conn, sc);
            if (fechavenc.before(Date.valueOf(LocalDate.now()))) {
                System.out.println("Error: La fecha de vencimiento debe ser posterior a la fecha actual.");
            }
        } while (fechavenc.before(Date.valueOf(LocalDate.now())));
        precomprarPelicula(conn, idPelicula, correo, fechavenc);
    }

    //El primer preparedStatement no tiene su setInt va a dar excepcion
    //Quita el try si no vas a hacer nada con el catch
    //El precio esta en otra tabla no en esta y no pides fecha vencimiento
    static void precomprarPelicula(Connection conn, int id_pelicula, String idCliente, Date fechavenc) throws SQLException {
        String fecha_estreno= "SELECT FechaEstreno FROM DatosPelicula WHERE IDPelicula = ?";
        Date fechaEstreno=null;
        try (PreparedStatement pstmt = conn.prepareStatement(fecha_estreno)) {
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            fechaEstreno = rs.getDate("FechaEstreno");
            String AniadirAlquiler = "INSERT INTO DatosAlquiler (CorreoElectronico, IDPelicula, FechaAlquiler, FechaVencimiento) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(AniadirAlquiler)) {
                stmt.setString(1, idCliente);
                stmt.setInt(2, id_pelicula);
                stmt.setDate(3, fechaEstreno);
                stmt.setDate(4, fechavenc);
                stmt.executeUpdate();
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        double precioAlquiler = calcular_precio_alquiler(conn, fechavenc, fechavenc, id_pelicula);
        // Insertar en la tabla PrecioAlquiler
        String sqlPrecioAlquiler = "INSERT INTO PrecioAlquiler (IDPelicula, FechaAlquiler, FechaVencimiento, PrecioAlquiler) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmtPrecio = conn.prepareStatement(sqlPrecioAlquiler)) {
            pstmtPrecio.setInt(1, id_pelicula);
            pstmtPrecio.setDate(2, fechaEstreno);
            pstmtPrecio.setDate(3, fechavenc);
            pstmtPrecio.setDouble(4, precioAlquiler);
            pstmtPrecio.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
}
