import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Scanner;

@SuppressWarnings("SqlResolve")

public class Empleado {

    static void menuEmpleados(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Bienvenido al menú de empleado:");
        int opcion = -1;

        while (true) {

            System.out.println("Selecciona una de las siguientes opciones del empleado:");
            System.out.println("1. Dar alta a un empleado.");
            System.out.println("2. Dar baja a un empleado.");
            System.out.println("3. Modificar un empleado.");
            System.out.println("4. Mostrar empleados.");
            System.out.println("5. Buscar un empleado(Por Nombre, Apellidos o DNI).");
            System.out.println("6. Salir");

            opcion = sc.nextInt();
            sc.nextLine();
            switch (opcion) {
                case 1:
                    darAltaEmpleado(conn, sc);
                    break;
                case 2:
                    darBajaEmpleado(conn, sc);
                    break;
                case 3:
                    modificarEmpleado(conn, sc);
                    break;
                case 4:
                    mostrarEmpleado(conn, sc);
                    break;
                case 5:
                    buscarEmpleado(conn, sc);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }
    }

    public static boolean existeEmpleado(Connection conn, String dni) throws SQLException {

        boolean existe;

        String sql="SELECT * FROM DatosEmpleado Where DNI=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, dni);
        ResultSet rs = pstmt.executeQuery();

        if(rs.next()){

            existe=true;

        }else {

            existe=false;

        }

        return existe;
    }

    public static boolean comprobarBajaEmpleado(Connection conn, String dni) throws SQLException{

        String sqlComprobarBaja = "SELECT FechaBaja FROM DatosEmpleado WHERE DNI = ?";
        PreparedStatement comprobarBaja = conn.prepareStatement(sqlComprobarBaja);
        comprobarBaja.setString(1, dni);
        ResultSet resultComprobarBaja = comprobarBaja.executeQuery();

        if (resultComprobarBaja.next()) {

            Date fechaBaja = resultComprobarBaja.getDate("FechaBaja");

            return fechaBaja != null;
        }

        return true;
    }

    public static void darAltaEmpleado(Connection conn, Scanner sc) throws SQLException {

        conn.setAutoCommit(false);
        Savepoint saveUpdateEmpleado=conn.setSavepoint();
        System.out.println("Introduzca su DNI:");
        String dni=sc.nextLine();

        if(existeEmpleado(conn, dni)){
            System.out.println("El empleado ya existe");
            conn.rollback(saveUpdateEmpleado);
            conn.setAutoCommit(true);
            return;
        }

        System.out.println("Introduzca su nombre:");
        String nombre = sc.nextLine();
        System.out.println("Introduzca sus apellidos:");
        String apellidos = sc.nextLine();
        String telefono=null;
        int opcionTelefono=-1;
        while(opcionTelefono<1 || opcionTelefono>2){

            System.out.println("¿Quiere introducir su teléfono?");
            System.out.println("1. Sí.");
            System.out.println("2. No.");
            opcionTelefono=sc.nextInt();
            sc.nextLine();
            switch (opcionTelefono){

                case 1:
                    System.out.println("Introduzca su teléfono:");
                    telefono = sc.nextLine();
                    break;
                case 2:
                    break;
                default:
                    System.out.println("Opcion no válida.");
                    break;
            }
        }

        System.out.println("Introduzca su dirección:");
        String direccion = sc.nextLine();
        int opcionturno = -1;
        String turno = null;

        while (opcionturno < 1 || opcionturno > 3) {

            System.out.println("Selecciona una de las siguientes opciones para su turno:");
            System.out.println("1. Turno Matutino.");
            System.out.println("2. Turno Vespertino.");
            System.out.println("3. Turno Nocturno.");

            opcionturno = sc.nextInt();
            sc.nextLine();
            switch (opcionturno) {
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

        String sql1 = "INSERT INTO DatosEmpleado (DNI, Nombre, Apellidos, Telefono, Direccion, NombreTurno) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        pstmt1.setString(1, dni);
        pstmt1.setString(2, nombre);
        pstmt1.setString(3, apellidos);
        pstmt1.setString(4, telefono);
        pstmt1.setString(5, direccion);
        pstmt1.setString(6, turno);
        pstmt1.executeUpdate();

    }

    public static void modificarEmpleado(Connection conn, Scanner sc) throws SQLException {

        conn.setAutoCommit(false);
        Savepoint saveUpdateEmpleado=conn.setSavepoint();
        System.out.println("Introduzca el DNI del empleado que quieras modificar:");
        String dni = sc.nextLine();

        if(!existeEmpleado(conn, dni)){
            System.out.println("El empleado no existe");
            conn.rollback(saveUpdateEmpleado);
            conn.setAutoCommit(true);
            return;
        }

        if(comprobarBajaEmpleado(conn, dni)){
            System.out.println("El empleado esta dado de baja.");
            conn.setAutoCommit(true);
            return;
        }

        int opcion=-1;
        int opcionturno = -1;
        String turno = null;
        PreparedStatement updateEmpleado;
        String sqlupdateEmpleado;
        while(opcion!=6){

            System.out.println("¿Qué dato quiere modificar?");
            System.out.println("1. Nombre.");
            System.out.println("2. Apellidos.");
            System.out.println("3. Teléfono.");
            System.out.println("4. Dirección.");
            System.out.println("5. Turno.");
            System.out.println("6. Finalizar.");
            opcion=sc.nextInt();
            sc.nextLine();
            switch (opcion){

                case 1:
                    sqlupdateEmpleado="UPDATE DatosEmpleado SET Nombre=? WHERE DNI=?";
                    updateEmpleado=conn.prepareStatement(sqlupdateEmpleado);
                    updateEmpleado.setString(2, dni);
                    System.out.println("Introduzca su nombre:");
                    updateEmpleado.setString(1,sc.nextLine());
                    break;
                case 2:
                    sqlupdateEmpleado="UPDATE DatosEmpleado SET Apellidos=? WHERE DNI=?";
                    updateEmpleado=conn.prepareStatement(sqlupdateEmpleado);
                    updateEmpleado.setString(2, dni);
                    System.out.println("Introduzca sus apellidos:");
                    updateEmpleado.setString(1,sc.nextLine());
                    break;
                case 3:
                    sqlupdateEmpleado="UPDATE DatosEmpleado SET Telefono=? WHERE DNI=?";
                    updateEmpleado=conn.prepareStatement(sqlupdateEmpleado);
                    updateEmpleado.setString(2, dni);
                    System.out.println("Introduzca su teléfono:");
                    updateEmpleado.setString(1,sc.nextLine());
                    break;
                case 4:
                    sqlupdateEmpleado="UPDATE DatosEmpleado SET Direccion=? WHERE DNI=?";
                    updateEmpleado=conn.prepareStatement(sqlupdateEmpleado);
                    updateEmpleado.setString(2, dni);
                    System.out.println("Introduzca su dirección:");
                    updateEmpleado.setString(1,sc.nextLine());
                    break;
                case 5:
                    sqlupdateEmpleado="UPDATE DatosEmpleado SET NombreTurno=? WHERE DNI=?";
                    updateEmpleado=conn.prepareStatement(sqlupdateEmpleado);
                    updateEmpleado.setString(2, dni);
                    while (opcionturno < 1 || opcionturno > 3) {
                        System.out.println("Seleccione una de las siguientes opciones para su turno:");
                        System.out.println("1. Turno Matutino.");
                        System.out.println("2. Turno Vespertino.");
                        System.out.println("3. Turno Nocturno.");
                        opcionturno = sc.nextInt();
                        sc.nextLine();
                        switch (opcionturno) {
                            case 1:
                                turno = "Turno Matutino";
                                break;
                            case 2:
                                turno = "Turno Vespertino";
                                break;
                            case 3:
                                turno = "Turno Nocturno";
                                break;
                            default:
                                System.out.println("Opción no válida.");
                                break;
                        }
                        updateEmpleado.setString(1, turno);
                    }
                    break;

            }

            System.out.println("¿Qué dato quiere modificar?");
            System.out.println("1. Nombre.");
            System.out.println("2. Apellidos.");
            System.out.println("3. Teléfono.");
            System.out.println("4. Dirección.");
            System.out.println("5. Turno.");
            opcion=sc.nextInt();
            sc.nextLine();

        }

        System.out.println("¿Quieres confirmar los cambios?");
        System.out.println("1. Sí");
        System.out.println("2. No");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                System.out.println("Cambios aplicados");
                break;
            case 2:
                conn.rollback(saveUpdateEmpleado);
                System.out.println("Cambios revertidos");
                break;
        }
        sc.nextLine();
        conn.setAutoCommit(true);

    }

    public static void darBajaEmpleado(Connection conn, Scanner sc) throws SQLException {
        conn.setAutoCommit(false);
        Savepoint saveBajaEmpleado=conn.setSavepoint();

        System.out.println("Introduzca el DNI del empleado que quieras dar de baja:");
        String dni = sc.nextLine();

        if(!existeEmpleado(conn, dni)){
            System.out.println("El empleado no existe");
            conn.rollback(saveBajaEmpleado);
            conn.setAutoCommit(true);
            return;
        }

        if(comprobarBajaEmpleado(conn, dni)){
            System.out.println("El empleado esta dado de baja.");
            conn.setAutoCommit(true);
            return;
        }

        String sql = "UPDATE DatosEmpleado SET FechaBaja=? WHERE DNI=?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        LocalDate fecha=LocalDate.now();
        pstmt.setDate(1, Date.valueOf(fecha));
        pstmt.setString(2, dni);
        pstmt.executeUpdate();

        System.out.println("¿Quieres confirmar la baja del empleado con DNI: " + dni + " ?");
        System.out.println("1. Sí.");
        System.out.println("2. No.");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                break;
            case 2:
                conn.rollback(saveBajaEmpleado);
                break;
        }
        sc.nextLine();
        conn.setAutoCommit(true);

    }

    public static void mostrarEmpleado(Connection conn, Scanner sc) throws SQLException {

        String sql = "SELECT * FROM DatosEmpleado";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("Listado de empleados:");
        System.out.println("DNI\tNombre\tApellidos\tTelefono\tSueldo\tDireccion\tTurno\tFecha Baja");
        System.out.println("-----------------------------------------------");
        if(rs.next()) {
            do {
                System.out.println(rs.getString("DNI") + "\t" +
                        rs.getString("Nombre") + "\t" +
                        rs.getString("Apellidos") + "\t" +
                        rs.getString("Telefono") + "\t" +
                        rs.getDouble("Sueldo") + "\t" +
                        rs.getString("Direccion") + "\t" +
                        rs.getString("NombreTurno") + "\t" +
                        rs.getDate("FechaBaja"));
            }while(rs.next());
        }else {
            System.out.println("No hay empleados");
        }
    }

    public static void buscarEmpleado(Connection conn, Scanner sc) throws SQLException {

        int opcion = -1;
        while (opcion < 1 || opcion > 3) {

            System.out.println("Seleccione una de las siguientes opciones para su busqueda:");
            System.out.println("1. Busqueda por nombre.");
            System.out.println("2. Busqueda por apellidoS.");
            System.out.println("3. Busqueda por DNI.");

            opcion = sc.nextInt();
            sc.nextLine();
            switch (opcion) {
                case 1:
                    System.out.println("Introduzca el nombre del empleado:");
                    String nombre = sc.nextLine();
                    String sql = "SELECT * FROM DatosEmpleado WHERE Nombre=?";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nombre);
                    ResultSet rs = pstmt.executeQuery();

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
                    ResultSet rs1 = pstmt1.executeQuery();

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
                    ResultSet rs2 = pstmt2.executeQuery();

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

}
