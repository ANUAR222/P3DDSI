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

            while (opcion!=5) {
                System.out.println("Selecciona una de las siguientes opciones:");
                System.out.println("1. Menu Pelicula.");
                System.out.println("2. Menu Alquiler.");
                System.out.println("3. Menu Empleado.");
                System.out.println("4. Menu Cliente.");
                System.out.println("5. Salir.");


                opcion = sc.nextInt();
                sc.nextLine();
                switch (opcion) {
                    case 1:
                        Pelicula.menuPelicula(conn);
                        break;
                    case 2:
                        Alquiler.simularInsercionAlquiler(conn, sc);
                        break;
                    case 3:
                        Empleado.menuEmpleados(conn,sc);
                        break;
                    case 4:
                        Cliente.menuCliente(conn, sc);
                        break;
                    case 5:
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
        String usuario = sc.nextLine();
        System.out.println("Introduce la contraseña:");
        String contrasenia = sc.nextLine();
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
                System.out.println("Fallo al iniciar sesion: usuario o contraseña incorrectos\n");
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
