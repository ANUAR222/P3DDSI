import oracle.jdbc.proxy.annotation.Pre;

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
        if(rs.next()) {
            do {
                System.out.println("Correo electrónico: " + rs.getString("CorreoElectronico"));
                System.out.println("ID de la película: " + rs.getInt("IDPelicula"));
                System.out.println("Fecha de alquiler: " + rs.getDate("FechaAlquiler"));
                System.out.println("Fecha de vencimiento: " + rs.getDate("FechaVencimiento"));
                System.out.println("Fecha de acceso: " + rs.getDate("FechaAcceso"));
                System.out.println("Calificación: " + rs.getDouble("Calificacion"));
                System.out.println();
            }while (rs.next());
        } else{
            System.out.println("No hay alquileres");
        }
    }


    public static void registrarNuevoAlquiler(Connection conn, Scanner sc) throws SQLException {
        String correo;
        int idPelicula;

        do {
            do {
                System.out.println("Introduce el correo electrónico del cliente:");
                correo = sc.nextLine();
                if (!Cliente.comprobarExisteCliente(conn, correo)) {
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
            } while (Cliente.comprobarBajaCliente(conn, correo) || !Cliente.comprobarExisteCliente(conn, correo));

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
        Date fechaAlquiler = Date.valueOf(LocalDate.now());

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
        if (!comprobar_existe_precioalquiler(conn, idPelicula, fechaAlquiler, fechaVencimiento)) {
            String sqlPrecioAlquiler = "INSERT INTO PrecioAlquiler (IDPelicula, FechaAlquiler, FechaVencimiento, PrecioAlquiler) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmtPrecio = conn.prepareStatement(sqlPrecioAlquiler);
                pstmtPrecio.setInt(1, idPelicula);
                pstmtPrecio.setDate(2, fechaAlquiler);
                pstmtPrecio.setDate(3, fechaVencimiento);
                pstmtPrecio.setDouble(4, precioAlquiler);
                pstmtPrecio.executeUpdate();
        }


        // Insertar en la tabla DatosAlquiler
        String sqlDatosAlquiler = "INSERT INTO DatosAlquiler (CorreoElectronico, IDPelicula, FechaAlquiler, FechaVencimiento) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmtDatos = conn.prepareStatement(sqlDatosAlquiler);
            pstmtDatos.setString(1, correo);
            pstmtDatos.setInt(2, idPelicula);
            pstmtDatos.setDate(3, fechaAlquiler);
            pstmtDatos.setDate(4, fechaVencimiento);
            pstmtDatos.executeUpdate();
            System.out.println("Alquiler registrado con éxito.");

    }

    public static double calcular_precio_alquiler(Connection conn, Date fecha_inc, java.util.Date fecha_fin, int idPelicula) throws SQLException {
        String sql = "SELECT Precio FROM DatosPelicula WHERE IDPelicula = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            double precio = rs.getDouble("Precio")*0.2;
            int dias = (int) ((fecha_fin.getTime() - fecha_inc.getTime()) / (1000 * 60 * 60 * 24));
            return precio * dias;
    }

    public static boolean comprobarExisteAlquiler(Connection conn, String correo, int idPelicula) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DatosAlquiler WHERE CorreoElectronico = ? AND IDPelicula = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, correo);
            pstmt.setInt(2, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
    }



    public static void simularExtenderAlquiler(Connection conn, Scanner sc) throws SQLException {
        String correo;
        do {
            System.out.println("Introduce el correo electrónico del cliente:");
            correo = sc.nextLine();
            if (!Cliente.comprobarExisteCliente(conn, correo)) {
                System.out.println("Error: El cliente no existe. con espacio para salir");
                if (sc.nextLine().equals(" ")) {
                    return;
                }
            }
            if (Cliente.comprobarBajaCliente(conn, correo)) {
                System.out.println("Error: El cliente está de baja.pon espacio para salir");
                if (sc.nextLine().equals(" ")) {
                    return;
                }
            }
        }while (!Cliente.comprobarExisteCliente(conn, correo) || Cliente.comprobarBajaCliente(conn, correo));
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
                System.out.println("Error: La fecha de vencimiento debe ser posterior a la fecha actual. Introduce espacio para salir.");
                if (sc.nextLine().equals(" ")) {
                    return;
                }
            }
            if (comprobarFechaVencimiento(conn, correo, idPelicula, nuevaFechaVencimiento)) {
                System.out.println("Error: La fecha de vencimiento debe ser posterior a la fecha de vencimiento actual. Introduce espacio para salir.");
                if (sc.nextLine().equals(" ")) {
                    return;
                }
            }
        } while (nuevaFechaVencimiento.before(Date.valueOf(LocalDate.now())) || comprobarFechaVencimiento(conn, correo, idPelicula, nuevaFechaVencimiento));

        extenderFechaAlquiler(conn, correo, idPelicula, nuevaFechaVencimiento);
    }


    private static boolean comprobarFechaVencimiento(Connection conn, String correo, int idPelicula, Date nuevaFechaVencimiento) throws SQLException {
        String sql = "SELECT FechaVencimiento FROM DatosAlquiler WHERE CorreoElectronico = ? AND IDPelicula = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, correo);
            pstmt.setInt(2, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            Date fechaVencimiento = rs.getDate("FechaVencimiento");
            return fechaVencimiento.after(nuevaFechaVencimiento);

    }
    private static boolean comprobar_existe_precioalquiler(Connection conn, int idPelicula, Date fechaAlquiler, Date fechaVencimiento) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PrecioAlquiler WHERE IDPelicula = ? AND FechaAlquiler = ? AND FechaVencimiento = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idPelicula);
            pstmt.setDate(2, fechaAlquiler);
            pstmt.setDate(3, fechaVencimiento);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
    }
    private static boolean verificarAlquilerExistente(Connection conn, String correo, int idPelicula) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DatosAlquiler WHERE CorreoElectronico = ? AND IDPelicula = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, correo);
            pstmt.setInt(2, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;

    }

    private static void extenderFechaAlquiler(Connection conn, String correo, int idPelicula, Date nuevaFechaVencimiento) throws SQLException {
        String sql = "UPDATE DatosAlquiler SET FechaVencimiento = ? WHERE CorreoElectronico = ? AND IDPelicula = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, nuevaFechaVencimiento);
            pstmt.setString(2, correo);
            pstmt.setInt(3, idPelicula);
            pstmt.executeUpdate();
            System.out.println("Fecha de alquiler extendida con éxito.");
            //recalcula el precio del alquiler
            String sqlPrecio = "SELECT FechaAlquiler FROM DatosAlquiler WHERE CorreoElectronico = ? AND IDPelicula = ?";
            PreparedStatement pstmtPrecio = conn.prepareStatement(sqlPrecio);
                pstmtPrecio.setString(1, correo);
                pstmtPrecio.setInt(2, idPelicula);
                ResultSet rs = pstmtPrecio.executeQuery();
                rs.next();
                Date fechaAlquiler = rs.getDate("FechaAlquiler");
                double precioAlquiler = calcular_precio_alquiler(conn, fechaAlquiler, nuevaFechaVencimiento, idPelicula);
                if (!comprobar_existe_precioalquiler(conn, idPelicula, fechaAlquiler, nuevaFechaVencimiento)) {
                    String sqlPrecioAlquiler = "INSERT INTO PrecioAlquiler (IDPelicula, FechaAlquiler, FechaVencimiento, PrecioAlquiler) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmtPrecioAlquiler = conn.prepareStatement(sqlPrecioAlquiler);
                        pstmtPrecioAlquiler.setInt(1, idPelicula);
                        pstmtPrecioAlquiler.setDate(2, fechaAlquiler);
                        pstmtPrecioAlquiler.setDate(3, nuevaFechaVencimiento);
                        pstmtPrecioAlquiler.setDouble(4, precioAlquiler);
                        pstmtPrecioAlquiler.executeUpdate();
                }
    }

    // Subsistema 2: Acceder a película

     public static void simularAccesoPelicula(Connection conn, Scanner sc) throws SQLException {
        String correo;
        do {
            System.out.println("Introduce el correo electrónico del cliente:");
            correo = sc.nextLine();
            if (!Cliente.comprobarExisteCliente(conn, correo)) {
                System.out.println("Error: El cliente no existe.");
                return;
            }
        }while (!Cliente.comprobarExisteCliente(conn, correo) || Cliente.comprobarBajaCliente(conn, correo));

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
        double calificacion;

        if (!verificarAlquilerExistente(conn, correo, idPelicula)) {
            System.out.println("Error: El cliente no tiene esta película alquilada.");
            return;
        }
        do {
             System.out.println("Introduce la calificación entre 0 y 10 :");
             calificacion = sc.nextDouble();
             sc.nextLine();
         }while (calificacion < 0 || calificacion > 10 );
         modificarCalificacion(conn, correo, idPelicula, calificacion);
        registrarAccesoPelicula(conn, correo, idPelicula);
    }

     public static void modificarCalificacion(Connection conn, String correo, int idPelicula, double calificacion) throws SQLException {
        String sql = "UPDATE DatosAlquiler SET Calificacion = ? WHERE CorreoElectronico = ? AND IDPelicula = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, calificacion);
            pstmt.setString(2, correo);
            pstmt.setInt(3, idPelicula);
            pstmt.executeUpdate();
            System.out.println("Calificación modificada con éxito.");
    }
    private static void registrarAccesoPelicula(Connection conn, String correo, int idPelicula) throws SQLException {
        String sql = "UPDATE DatosAlquiler SET FechaAcceso = CURRENT_DATE WHERE CorreoElectronico = ? AND IDPelicula = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, correo);
            pstmt.setInt(2, idPelicula);
            pstmt.executeUpdate();
            System.out.println("Acceso a película registrado con éxito.");
    }

    // Subsistema 3: Precompra película
     static void simularPrecompraPelicula(Connection conn, Scanner sc) throws SQLException {
        String correo;
        int idPelicula;
        Date fechavenc;
        do {
            System.out.println("Introduce el correo electrónico del cliente:");
            correo = sc.nextLine();
            if (!Cliente.comprobarExisteCliente(conn, correo)) {
                System.out.println("Error: El cliente no existe.");
                return;
            }
        }while (!Cliente.comprobarExisteCliente(conn, correo) || Cliente.comprobarBajaCliente(conn, correo));
        do {

            System.out.println("Introduce el ID de la película:");
            idPelicula = sc.nextInt();
            sc.nextLine();  // Consumir el salto de línea pendiente
            if (!Pelicula.comprobarIdPelicula(conn, idPelicula)) {
                System.out.println("Error: La película no existe.");
                return;
            }
        }while (!Pelicula.comprobarIdPelicula(conn, idPelicula) || Pelicula.comprobarBajaPelicula(conn, idPelicula));
        if(verificarAlquilerExistente(conn, correo, idPelicula)){
            System.out.println("El alquiler ya existe se cancela la operacion");
            return;
        }
        String sqlComprobar = "SELECT FechaEstreno FROM DatosPelicula WHERE IDPelicula = ?";
        PreparedStatement comprobar = conn.prepareStatement(sqlComprobar);
        comprobar.setInt(1, idPelicula);
        ResultSet resultComprobar = comprobar.executeQuery();
        if(resultComprobar.next()){
            if (resultComprobar.getDate("FechaEstreno")==null||resultComprobar.getDate("FechaEstreno").before(Date.valueOf(LocalDate.now()))){
                System.out.println("La pelicula no puede ser precomprada");
                return;
            }
        }
        do {
            fechavenc = Main.obtenerFechaDesdeScanner(conn, sc);
            if (fechavenc.before(Date.valueOf(LocalDate.now()))) {
                System.out.println("Error: La fecha de vencimiento debe ser posterior a la fecha actual.");
            }
        } while (fechavenc.before(Date.valueOf(LocalDate.now())));

        precomprarPelicula(conn, idPelicula, correo, fechavenc);
    }

    static void precomprarPelicula(Connection conn, int id_pelicula, String idCliente, Date fechavenc) throws SQLException {
        String fecha_estreno= "SELECT FechaEstreno FROM DatosPelicula WHERE IDPelicula = ?";
        Date fechaEstreno=null;
        PreparedStatement pstmt = conn.prepareStatement(fecha_estreno);
        pstmt.setInt(1, id_pelicula);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        fechaEstreno = rs.getDate("FechaEstreno");
        String AniadirAlquiler = "INSERT INTO DatosAlquiler (CorreoElectronico, IDPelicula, FechaAlquiler, FechaVencimiento) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(AniadirAlquiler);
        stmt.setString(1, idCliente);
        stmt.setInt(2, id_pelicula);
        stmt.setDate(3, fechaEstreno);
        stmt.setDate(4, fechavenc);
        stmt.executeUpdate();

        double precioAlquiler = calcular_precio_alquiler(conn, fechavenc, fechavenc, id_pelicula);
        if (!comprobar_existe_precioalquiler(conn, id_pelicula, fechaEstreno, fechavenc)) {
            String sqlPrecioAlquiler = "INSERT INTO PrecioAlquiler (IDPelicula, FechaAlquiler, FechaVencimiento, PrecioAlquiler) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmtPrecio = conn.prepareStatement(sqlPrecioAlquiler);
                pstmtPrecio.setInt(1, id_pelicula);
                pstmtPrecio.setDate(2, fechaEstreno);
                pstmtPrecio.setDate(3, fechavenc);
                pstmtPrecio.setDouble(4, precioAlquiler);
                pstmtPrecio.executeUpdate();
        }
    }
}
