import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;
@SuppressWarnings("SqlResolve")

public class Empleado {
    //While esta mal solo sale cuando buscas enmpleado
    static void menuEmpleados(Connection conn, Scanner sc) throws SQLException{

        System.out.println("Bienvenido al menú de empleado:");
        int opcion = -1;

        while (opcion != 5) {

            System.out.println("Selecciona una de las siguientes opciones del empleado:");
            System.out.println("1. Dar alta a un empleado.");
            System.out.println("2. Dar baja a un empleado.");
            System.out.println("3. Modificar un empleado.");
            System.out.println("4. Mostrar empleados.");
            System.out.println("5. Buscar un empleado(Por Nombre, Apellidos o DNI).");

            opcion = sc.nextInt();
            sc.nextLine();
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
    //Tienes que comprobar que no esten insertados y llamar a esta funcion nada mas entrar al menu xq si no va a dar excepcion el alta de empleado
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


    public static boolean existeEmpleado(Connection conn, String dni) throws SQLException {

        boolean existe;

        String sql="SELECT * FROM DatosEmpleado Where DNI=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, dni);
        ResultSet rs = pstmt.executeQuery(sql);

        if(rs.next()){

            existe=true;

        }else {

            existe=false;

        }

        return existe;
    }

    public static void darAltaEmpleado(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduzca su DNI:");
        String dni=sc.nextLine();

        boolean existe;

        do{

            existe=existeEmpleado(conn,dni);

            if (existe) {

                System.out.println("Ese DNI ya está registrado.");
                System.out.println("Introduzca de nuevo su DNI:");
                dni = sc.nextLine();

            } else {

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
        }while (existe);
    }
    //Esta mal entero copia si quieres el de pelicula y cambia los datos
    public static void modificarEmpleado(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduzca el DNI del empleado que quieras modificar:");
        String dni = sc.nextLine();

        boolean existe;

        do {

            existe = existeEmpleado(conn, dni);

            if (existe) {

                System.out.println("Usuario existente.");
                String nombre=null;
                int opcionNombre=-1;
                while(opcionNombre<1 || opcionNombre>2){

                    System.out.println("¿Quiere modificar su nombre?");
                    System.out.println("1. Sí.");
                    System.out.println("2. No.");
                    opcionNombre=sc.nextInt();
                    sc.nextLine();
                    switch (opcionNombre){

                        case 1:
                            System.out.println("Introduzca su nombre:");
                            nombre = sc.nextLine();
                            break;
                        case 2:
                            nombre="SELECT Nombre FROM DatosEmpleado WHERE DNI=?";
                            PreparedStatement pstmt = conn.prepareStatement(nombre);
                            pstmt.setString(1, dni);
                            pstmt.executeUpdate();
                            break;
                        default:
                            System.out.println("Opcion no válida.");
                            break;
                    }
                }

                String apellidos=null;
                int opcionApellidos=-1;
                while(opcionApellidos<1 || opcionApellidos>2){

                    System.out.println("¿Quiere modificar sus apellidos?");
                    System.out.println("1. Sí.");
                    System.out.println("2. No.");
                    opcionNombre=sc.nextInt();
                    sc.nextLine();
                    switch (opcionApellidos){

                        case 1:
                            System.out.println("Introduzca sus apellidos:");
                            apellidos = sc.nextLine();
                            break;
                        case 2:
                            nombre="SELECT Apellidos FROM DatosEmpleado WHERE DNI=?";
                            PreparedStatement pstmt = conn.prepareStatement(apellidos);
                            pstmt.setString(1, dni);
                            pstmt.executeUpdate();
                            break;
                        default:
                            System.out.println("Opcion no válida.");
                            break;
                    }
                }

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
                            nombre="SELECT Telefono FROM DatosEmpleado WHERE DNI=?";
                            PreparedStatement pstmt = conn.prepareStatement(telefono);
                            pstmt.setString(1, dni);
                            pstmt.executeUpdate();
                            break;
                        default:
                            System.out.println("Opcion no válida.");
                            break;
                    }
                }

                String direccion=null;
                int opcionDireccion=-1;
                while(opcionDireccion<1 || opcionDireccion>2){

                    System.out.println("¿Quiere introducir su dirección?");
                    System.out.println("1. Sí.");
                    System.out.println("2. No.");
                    opcionDireccion=sc.nextInt();
                    sc.nextLine();
                    switch (opcionDireccion){

                        case 1:
                            System.out.println("Introduzca su dirección:");
                            direccion = sc.nextLine();
                            break;
                        case 2:
                            nombre="SELECT Direccion FROM DatosEmpleado WHERE DNI=?";
                            PreparedStatement pstmt = conn.prepareStatement(direccion);
                            pstmt.setString(1, dni);
                            pstmt.executeUpdate();
                            break;
                        default:
                            System.out.println("Opcion no válida.");
                            break;
                    }
                }

                int opcionturno = -1;
                String turno = null;
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

            } else {

                System.out.println("Ese DNI no está registrado.");
                System.out.println("Introduzca de nuevo el DNI:");
                dni = sc.nextLine();

            }
        }while (!existe);
    }
    //La fecha no se comprueba asi, solo estas ejecutando el select sin guardarlo en ningun lado fecha solo es el string base del preparedStatement no se guarda ahi el resultado
    //La fecha actual no se obtiene asi, solo has creado un objeto fecha sin valor ninguno
    //El while esta mal no puedes obligar a poner un dni, si todos estan de baja o no existen empleados te quedas aqui para siempre
    public static void darBajaEmpleado(Connection conn, Scanner sc) throws SQLException {

        System.out.println("Introduzca el DNI del empleado que quieras dar de baja:");
        String dni = sc.nextLine();

        boolean existe;

        do {

            existe = existeEmpleado(conn, dni);

            if (existe) {

                String fecha="SELECT FechaBaja FROM DatosEmpleado WHERE DNI=?";

                PreparedStatement pstmt = conn.prepareStatement(fecha);
                pstmt.setString(1, dni);
                pstmt.executeUpdate();

                if(fecha==null){

                    Date baja=new Date();

                    String sql = "UPDATE DatosEmpleado SET FechaBaja= WHERE DNI=?";
                    PreparedStatement pstmt1 = conn.prepareStatement(sql);
                    pstmt1.setDate(1, (java.sql.Date) baja);
                    pstmt1.setString(2, dni);
                    pstmt1.executeUpdate();

                }else{

                    System.out.println("Ese empleado ya está dado de baja.");
                    System.out.println("Introduzca de nuevo el DNI:");
                    dni = sc.nextLine();

                }


            }else {

                System.out.println("Ese DNI no está registrado.");
                System.out.println("Introduzca de nuevo el DNI:");
                dni = sc.nextLine();

            }
        }while (!existe);
    }

    public static void mostrarEmpleado(Connection conn, Scanner sc) throws SQLException {

        String sql = "SELECT * FROM DatosEmpleado";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery(sql);

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

}
