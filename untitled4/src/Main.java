/*import java.sql.*;
import java.util.Date;
import java.util.Scanner;


// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
class Main {
    public static void main(String[] args) throws SQLException {
        Scanner sc= new Scanner(System.in);
        System.out.println("Introduce el usuario");
        String usuario= sc.nextLine();
        System.out.println("Introduce la contraseña");
        String contrasenia= sc.nextLine();
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        Connection conn=null;
        try {
            conn = DriverManager.getConnection
                    ("jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es", usuario, contrasenia);
        }
        catch (SQLException e){
            //Si el fallo es un usuario o contraseña incorrecto se da un intento mas
            if(e.getErrorCode()==1017){
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
                    e1.printStackTrace();
                    System.exit(-1);
                }
            }
            else{
                e.printStackTrace();
                System.exit(-2);
            }
        }

        System.out.println("Conexion realizada");
        crearTablas(conn);

        int opcion=-1;
        while(opcion!=4){
            System.out.println("Selecciona una de las siguientes opciones:\n1. Borrar y crear tablas.\n2. Dar alta producto.\n3. Mostrar tablas.\n4. Cerrar aplicacion.");
            opcion= sc.nextInt();
            switch (opcion) {
                case 1 -> crearTablas(conn);
                case 2 -> darAlta(conn);
                case 3 -> mostrarTablas(conn);
            }
        }

        salir(conn);
    }


    static void crearTablas(Connection conn) {
        try {
            PreparedStatement sentencia;

            // Borrado de tablas si existen
            try {
                sentencia= conn.prepareStatement("DROP TABLE Detalle_Pedido");
                sentencia.execute();
            } catch (SQLException e) {
                // Si no existe la tabla no hacemos nada
            }

            try {
                sentencia= conn.prepareStatement("DROP TABLE Pedido");
                sentencia.execute();
            } catch (SQLException e) {
                // Si no existe la tabla no hacemos nada
            }

            try {
                sentencia= conn.prepareStatement("DROP TABLE Stock");
                sentencia.execute();
            } catch (SQLException e) {
                // Si no existe la tabla no hacemos nada
            }

            // Creación de la tabla Stock
            sentencia= conn.prepareStatement("CREATE TABLE Stock (Cproducto INT PRIMARY KEY, Cantidad INT)");
            sentencia.execute();

            // Creación de la tabla Pedido
            sentencia= conn.prepareStatement("CREATE TABLE Pedido (Cpedido INT PRIMARY KEY, Ccliente INT, Fecha_pedido DATE)");
            sentencia.execute();

            // Creación de la tabla Detalle_Pedido
            sentencia= conn.prepareStatement("CREATE TABLE Detalle_Pedido (Cpedido INT, Cproducto INT, Cantidad INT, PRIMARY KEY (Cpedido, Cproducto), FOREIGN KEY (Cpedido) REFERENCES Pedido(Cpedido), FOREIGN KEY (Cproducto) REFERENCES Stock(Cproducto))");
            sentencia.execute();

            // Inserción de 10 tuplas predefinidas en la tabla Stock
            sentencia=conn.prepareStatement("INSERT INTO Stock (Cproducto, Cantidad) VALUES (?, ?)");

            sentencia.setInt(1, 1);
            sentencia.setInt(2, 100);
            sentencia.execute();

            sentencia.setInt(1, 2);
            sentencia.setInt(2, 150);
            sentencia.execute();

            sentencia.setInt(1, 3);
            sentencia.setInt(2, 200);
            sentencia.execute();

            sentencia.setInt(1, 4);
            sentencia.setInt(2, 50);
            sentencia.execute();

            sentencia.setInt(1, 5);
            sentencia.setInt(2, 300);
            sentencia.execute();

            sentencia.setInt(1, 6);
            sentencia.setInt(2, 80);
            sentencia.execute();

            sentencia.setInt(1, 7);
            sentencia.setInt(2, 120);
            sentencia.execute();

            sentencia.setInt(1, 8);
            sentencia.setInt(2, 250);
            sentencia.execute();

            sentencia.setInt(1, 9);
            sentencia.setInt(2, 180);
            sentencia.execute();

            sentencia.setInt(1, 10);
            sentencia.setInt(2, 90);
            sentencia.execute();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static void darAlta(Connection conn) throws SQLException {
        //Se desactiva el Auto Commit para manejar savepoint y rollbacks
        conn.setAutoCommit(false);
        //Se crea un savepoint para poder cancelar el pedido
        Savepoint crearPedido= conn.setSavepoint();
        //Se crea un savepoint para poder eliminar los detalles de pedido y se inicializa a null al no haber detalles del pedido todavia
        Savepoint detallesPedido= null;

        Scanner sc = new Scanner(System.in);

        System.out.println("Introduce el codigo de cliente.");
        int Ccliente= sc.nextInt();

        Date utilfechaPedido= new Date();
        Date sqlFechaPedido= new java.sql.Date(utilfechaPedido.getTime());

        //Calcula el Cpedido correspondiente si esta vacia la tabla sera "1" si no sera el ultimo Cpedido mas uno
        int Cpedido;
        String sqlVacio="SELECT COUNT(*) as numPedidos FROM Pedido";
        PreparedStatement consultaVacio= conn.prepareStatement(sqlVacio);
        ResultSet resConsultaVacio= consultaVacio.executeQuery();
        if(resConsultaVacio.next()){
            if(resConsultaVacio.getInt("numPedidos")==0){
                Cpedido=1;
            }
            else{
                Cpedido= resConsultaVacio.getInt("numPedidos");
                Cpedido++;
            }
        }
        else{
            System.out.println("La operacion para calcular el numero de pedidos ha fallado");
            return;
        }

        String consulta= "INSERT INTO Pedido (Cpedido, Ccliente, Fecha_pedido) VALUES (? , ?, ?)";
        PreparedStatement sentencia= conn.prepareStatement(consulta);
        sentencia.setInt(1, Cpedido);
        sentencia.setInt(2, Ccliente);
        sentencia.setDate(3, (java.sql.Date) sqlFechaPedido);
        ResultSet res= sentencia.executeQuery();
        int opcion;
        do {
            System.out.println("Selecciona una de las siguientes opciones:\n1. Añadir detalle de producto.\n2. Eliminar todos los detalles de producto.\n3. Cancelar pedido.\n4. Finalizar pedido");
            opcion=sc.nextInt();
            switch (opcion) {
                //Añade el producto pasando como parametro Cpedido para el posterior INSERT en Detalle-Pedido y devuelve el savepoint anterior a esta operacion
                case 1 -> {
                    if (detallesPedido == null) {
                        detallesPedido = aniadirDetallesProducto(conn, Cpedido);
                        mostrarTablas(conn);
                    } else {
                        System.out.println("Elimina los detalles de producto antes de añadirlos de nuevo");
                    }
                }
                //Rollback para eliminar los detalles del pedido si se han añadido
                case 2 -> {
                    if (detallesPedido != null) {
                        conn.rollback(detallesPedido);
                        detallesPedido=null;
                        System.out.println("Se han eliminado correctamente los detalles del pedido");
                        mostrarTablas(conn);
                    }
                }
                //Elimina el pedido y sale de la funcion
                case 3 -> {
                    conn.rollback(crearPedido);
                    mostrarTablas(conn);
                }

                //Confirma el pedido y sale de la funcion
                case 4 -> conn.commit();
            }
        }while (opcion<=2);

        //Devuelve Auto Commit a su valor por defecto
        conn.setAutoCommit(true);
    }

    static Savepoint aniadirDetallesProducto(Connection conn, int Cpedido) throws SQLException{
        Savepoint detallesPedido=conn.setSavepoint();
        Scanner sc= new Scanner(System.in);

        System.out.println("Introduce el codigo de producto y la cantidad separados por un espacio");
        int Cproducto= sc.nextInt();
        int cantidad= sc.nextInt();
        String numProductos="SELECT COUNT(*) as numProductos FROM Stock";
        PreparedStatement consultaNumProductos= conn.prepareStatement(numProductos);
        ResultSet resConsultaNumProductos= consultaNumProductos.executeQuery();
        if(resConsultaNumProductos.next()){
            int intentos=0;
            while(((Cproducto<=0)||(Cproducto>resConsultaNumProductos.getInt("numProductos")))&&(intentos<3)){
                System.out.println("Introduce un codigo de producto correcto");
                Cproducto= sc.nextInt();
                intentos++;
            }
            if((Cproducto<=0)||(Cproducto>resConsultaNumProductos.getInt("numProductos"))){
                System.out.println("Intentos agotados");
                return null;
            }
        }
        else{
            System.out.println("La operacion para calcular el numero de productos ha fallado");
            return null;
        }
        while(cantidad<=0){
            System.out.println("Introduce una cantidad positiva");
            conn.rollback(detallesPedido);
            cantidad= sc.nextInt();
        }
        String consultaCantidad= "SELECT (Cantidad) FROM Stock WHERE Cproducto = ?";
        PreparedStatement comprobarCantidad= conn.prepareStatement(consultaCantidad);
        comprobarCantidad.setInt(1, Cproducto);
        ResultSet res= comprobarCantidad.executeQuery();
        if(res.next()){
            int intentos=0;
            while((res.getInt("Cantidad")<cantidad)&&(intentos!=3)&&(res.getInt("Cantidad")!=0)){
                System.out.println("La cantidad deseada excede el stock. El cantidad disponible es:" + res.getInt("Cantidad") + " .\nIntroduzca correctamente la cantidad deseada");
                cantidad= sc.nextInt();
                intentos++;
            }
            if((res.getInt("Cantidad")<cantidad)&&(res.getInt("Cantidad")!=0)){
                System.out.println("Intentos agotados");
                conn.rollback(detallesPedido);
                return null;
            }
        }
        else{
            System.out.println("Consulta de cantidad en tabla Stock ha fallado");
            return null;
        }

        String sqlInsert="INSERT INTO Detalle_Pedido Values ( ?, ?, ?)";
        PreparedStatement insertDetallePedido= conn.prepareStatement(sqlInsert);
        insertDetallePedido.setInt(1, Cpedido);
        insertDetallePedido.setInt(2, Cproducto);
        insertDetallePedido.setInt(3, cantidad);
        ResultSet resInsertDetallePedido= insertDetallePedido.executeQuery();

        String sqlUpdate="UPDATE Stock SET Cantidad = Cantidad - ? WHERE Cproducto = ?";
        PreparedStatement updateStock= conn.prepareStatement(sqlUpdate);
        updateStock.setInt(1, cantidad);
        updateStock.setInt(2, Cproducto);
        ResultSet resultUpdate= updateStock.executeQuery();


        return detallesPedido;
    }







    static void mostrarTablas(Connection conn) throws SQLException {

        Scanner sc= new Scanner(System.in);
        consultaTablaStock(conn);
        consultaTablaPedido(conn);
        consultaTablaDetallePedido(conn);
        System.out.println("Pulsa \"enter\" para salir de la visualizacion de tablas");
        String aux= sc.nextLine();

    }

    static void consultaTablaStock(Connection conn) throws SQLException {

        ResultSet r = buscar(conn,"select * from Stock");

        try {

            System.out.println("\n TODOS LOS REGISTROS DE LA TABLA STOCK:\n");

            while (r.next()) {

                System.out.println(r.getInt("Cproducto") + " | " + r.getInt("Cantidad"));

            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void consultaTablaPedido(Connection conn) throws SQLException {

        ResultSet r = buscar(conn, "select * from Pedido");
        try {
            System.out.println("\n TODOS LOS REGISTROS DE LA TABLA PEDIDO:\n");


            while (r.next()) {

                System.out.println(r.getInt("Cpedido") + " | " + r.getInt("Ccliente") + " | " + r.getDate("Fecha_pedido"));
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    static void consultaTablaDetallePedido(Connection conn) throws SQLException {

        ResultSet r = buscar(conn, "select * from Detalle_Pedido");
        try {
            System.out.println("\n TODOS LOS REGISTROS DE LA TABLA DETALLE-PEDIDO:\n");


            while (r.next()) {

                System.out.println(r.getInt("Cpedido") + " | " + r.getInt("Cproducto") + " | " + r.getInt("Cantidad"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    static ResultSet buscar(Connection conn, String sql) throws SQLException {
        PreparedStatement sentencia= conn.prepareStatement(sql);
        return sentencia.executeQuery();
    }

    static void salir(Connection conn) throws SQLException{

        conn.close();
        System.out.println("Conexion cerrada");
    }
}*/
/*
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;


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
                        crearTablas(conn);
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

        String url = "jdbc:oracle:thin:@//oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
        return DriverManager.getConnection(url, "x5114418", "x5114418");
    }

    public static void crearTablas(Connection conn) throws SQLException {
        // Sentencias SQL para crear las tablas
        String crearTablasSQL = "CREATE TABLE DatosPelicula (IDPelicula NUMBER PRIMARY KEY, " +
                "Nombre VARCHAR2(100), Precio NUMBER, FechaEstreno DATE, FechaAlta DATE, " +
                "FechaBaja DATE, Sinopsis CLOB, Calificacion NUMBER); " +
                "CREATE TABLE PerteneceA (IDPelicula NUMBER, IDGenero NUMBER, " +
                "PRIMARY KEY (IDPelicula, IDGenero), " +
                "FOREIGN KEY (IDPelicula) REFERENCES DatosPelicula(IDPelicula)); " +
                "CREATE TABLE Actua (IDPelicula NUMBER, NombreActor VARCHAR2(100), " +
                "PRIMARY KEY (IDPelicula, NombreActor)); " +
                "CREATE TABLE Actores (NombreActor VARCHAR2(100) PRIMARY KEY); " +
                "CREATE TABLE DatosCliente (CorreoElectronico VARCHAR2(100) PRIMARY KEY, " +
                "Nombre VARCHAR2(100), Apellidos VARCHAR2(100), Telefono VARCHAR2(20), " +
                "FechaAlta DATE, FechaBaja DATE); " +
                "CREATE TABLE DatosEmpleado (DNI VARCHAR2(20) PRIMARY KEY, " +
                "Nombre VARCHAR2(100), Apellidos VARCHAR2(100), Telefono VARCHAR2(20), " +
                "Sueldo NUMBER, Direccion VARCHAR2(200), FechaBaja DATE, NombreTurno VARCHAR2(100)); " +
                "CREATE TABLE DatosTurno (NombreTurno VARCHAR2(100) PRIMARY KEY, " +
                "HoraEntrada VARCHAR2(8), HoraSalida VARCHAR2(8)); " +
                "CREATE TABLE DatosAlquila (CorreoElectronico VARCHAR2(100), " +
                "IDPelicula NUMBER, FechaAlquiler DATE, FechaVencimiento DATE, " +
                "PrecioAlquiler NUMBER, FechaAcceso DATE, " +
                "PRIMARY KEY (CorreoElectronico, IDPelicula), " +
                "FOREIGN KEY (CorreoElectronico) REFERENCES DatosCliente(CorreoElectronico), " +
                "FOREIGN KEY (IDPelicula) REFERENCES DatosPelicula(IDPelicula)); " +
                "CREATE TABLE AlquilaV2 (CorreoElectronico VARCHAR2(100), " +
                "IDPelicula NUMBER, FechaAlquiler DATE, FechaVencimiento DATE, " +
                "FechaAcceso DATE, " +
                "PRIMARY KEY (CorreoElectronico, IDPelicula), " +
                "FOREIGN KEY (CorreoElectronico) REFERENCES DatosCliente(CorreoElectronico), " +
                "FOREIGN KEY (IDPelicula) REFERENCES DatosPelicula(IDPelicula)); " +
                "ALTER TABLE DatosPelicula ADD CONSTRAINT unique_pelicula_fecha " +
                "UNIQUE (IDPelicula, FechaAlta, FechaBaja); " +
                "CREATE TABLE PrecioAlquiler (IDPelicula NUMBER, FechaAlquiler DATE, " +
                "FechaVencimiento DATE, PrecioAlquiler NUMBER, " +
                "PRIMARY KEY (IDPelicula, FechaAlquiler, FechaVencimiento), " +
                "FOREIGN KEY (IDPelicula, FechaAlquiler, FechaVencimiento) " +
                "REFERENCES DatosPelicula(IDPelicula, FechaAlta, FechaBaja)); " +
                "CREATE TABLE DatosGenero (IDGenero NUMBER PRIMARY KEY, Nombre VARCHAR2(100)); " +
                "ALTER TABLE DatosAlquila ADD Calificacion NUMBER; " +
                "CREATE OR REPLACE TRIGGER trg_actualiza_calificacion " +
                "AFTER INSERT OR UPDATE OR DELETE ON DatosAlquila " +
                "FOR EACH ROW " +
                "DECLARE " +
                "v_media_calificacion NUMBER; " +
                "BEGIN " +
                "SELECT AVG(Calificacion) " +
                "INTO v_media_calificacion " +
                "FROM DatosAlquila " +
                "WHERE IDPelicula = :NEW.IDPelicula; " +
                "UPDATE DatosPelicula " +
                "SET Calificacion = v_media_calificacion " +
                "WHERE IDPelicula = :NEW.IDPelicula; " +
                "EXCEPTION " +
                "WHEN OTHERS THEN " +
                "DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM); " +
                "END; " +
                "CREATE TABLE AuditoriaAlquileres (ID_AUD NUMBER PRIMARY KEY, " +
                "CorreoElectronico VARCHAR2(100), IDPelicula NUMBER, " +
                "FechaAlquiler DATE, FechaVencimiento DATE, Accion VARCHAR2(10), " +
                "FechaRegistro DATE DEFAULT SYSDATE); " +
                "CREATE OR REPLACE TRIGGER trg_auditoria_alquileres " +
                "AFTER INSERT ON DatosAlquila " +
                "FOR EACH ROW " +
                "BEGIN " +
                "INSERT INTO AuditoriaAlquileres (CorreoElectronico, IDPelicula, FechaAlquiler, FechaVencimiento, Accion) " +
                "VALUES (:NEW.CorreoElectronico, :NEW.IDPelicula, :NEW.FechaAlquiler, :NEW.FechaVencimiento, 'INSERT'); " +
                "END;";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(crearTablasSQL);
            System.out.println("Tablas creadas.");
        }
    }

    public static void borrarTablas(Connection conn) throws SQLException {
        // Sentencias SQL para borrar las tablas
        String borrarTablasSQL = "DROP TABLE AuditoriaAlquileres; " +
                "DROP TABLE DatosAlquila; " +
                "DROP TABLE AlquilaV2; " +
                "DROP TABLE PrecioAlquiler; " +
                "DROP TABLE DatosEmpleado; " +
                "DROP TABLE DatosTurno; " +
                "DROP TABLE DatosCliente; " +
                "DROP TABLE Actores; " +
                "DROP TABLE Actua; " +
                "DROP TABLE PerteneceA; " +
                "DROP TABLE DatosPelicula; " +
                "DROP TABLE DatosGenero;";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(borrarTablasSQL);
            System.out.println("Tablas borradas.");
        }
    }

    public static void darAltaCliente(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduce el correo electrónico del cliente:");
        String correoElectronico = sc.nextLine();
        System.out.println("Introduce el nombre del cliente:");
        String nombre = sc.nextLine();
        System.out.println("Introduce los apellidos del cliente:");
        String apellidos = sc.nextLine();
        System.out.println("Introduce el teléfono del cliente:");
        String telefono = sc.nextLine();

        String sql = "INSERT INTO DatosCliente (CorreoElectronico, Nombre, Apellidos, Telefono, FechaAlta) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correoElectronico);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellidos);
            pstmt.setString(4, telefono);
            LocalDate LocalDate = null;
            pstmt.setDate(5, Date.valueOf(LocalDate.now()));
            pstmt.executeUpdate();
            System.out.println("Cliente dado de alta.");
        }
    }

    public static void darAltaEmpleado(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduce el correo electrónico del empleado:");
        String correoElectronico = sc.nextLine();
        System.out.println("Introduce el nombre del empleado:");
        String nombre = sc.nextLine();
        System.out.println("Introduce los apellidos del empleado:");
        String apellidos = sc.nextLine();
        System.out.println("Introduce el teléfono del empleado:");
        String telefono = sc.nextLine();
        System.out.println("Introduce el salario del empleado:");
        double salario = sc.nextDouble();
        sc.nextLine();  // Consumir el salto de línea pendiente
        System.out.println("Introduce el turno del empleado (M/T/N):");
        String turno = sc.nextLine();

        String sql = "INSERT INTO DatosEmpleado (CorreoElectronico, Nombre, Apellidos, Telefono, Salario, Turno, FechaAlta) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correoElectronico);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellidos);
            pstmt.setString(4, telefono);
            pstmt.setDouble(5, salario);
            pstmt.setString(6, turno);
            LocalDate LocalDate = null;
            pstmt.setDate(7, Date.valueOf(LocalDate.now()));
            pstmt.executeUpdate();
            System.out.println("Empleado dado de alta.");
        }
    }

    public static void darAltaPelicula(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduce el nombre de la película:");
        String nombre = sc.nextLine();
        System.out.println("Introduce el precio de la película:");
        double precio = sc.nextDouble();
        sc.nextLine();  // Consumir el salto de línea pendiente
        System.out.println("Introduce la fecha de estreno (YYYY-MM-DD):");
        String fechaEstreno = sc.nextLine();
        System.out.println("Introduce la sinopsis de la película:");
        String sinopsis = sc.nextLine();

        String sql = "INSERT INTO DatosPelicula (Nombre, Precio, FechaEstreno, Sinopsis) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setDouble(2, precio);
            pstmt.setDate(3, Date.valueOf(fechaEstreno));
            pstmt.setString(4, sinopsis);
            pstmt.executeUpdate();
            System.out.println("Película dada de alta.");
        }
    }

    public static void darAltaGenero(Connection conn, Scanner sc) throws SQLException {
        System.out.println("Introduce el nombre del género:");
        String nombre = sc.nextLine();

        String sql = "INSERT INTO DatosGenero (Nombre) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.executeUpdate();
            System.out.println("Género dado de alta.");
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
*/

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
            String[] tablas = {
                    "PrecioAlquiler", "DatosAlquiler", "DatosEmpleado", "DatosTurno",
                    "DatosCliente", "Actua", "Actores", "PerteneceA", "DatosGenero", "DatosPelicula"
            };

            for (String tabla : tablas) {
                String dropTabla = "DROP TABLE " + tabla + " CASCADE CONSTRAINTS";
                ejecutarSQL(conn, dropTabla);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Creación de las tablas
        try {
            // Creación de la tabla DatosPelicula
            String createDatosPelicula = "CREATE TABLE DatosPelicula (" +
                    "IDPelicula NUMBER PRIMARY KEY, " +
                    "Nombre VARCHAR2(100), " +
                    "Precio NUMBER, " +
                    "FechaEstreno DATE, " +
                    "FechaAlta DATE, " +
                    "FechaBaja DATE, " +
                    "Sinopsis CLOB, " +
                    "Calificacion NUMBER)";
            ejecutarSQL(conn, createDatosPelicula);

            // Creación de la tabla DatosGenero
            String createDatosGenero = "CREATE TABLE DatosGenero (" +
                    "IDGenero NUMBER PRIMARY KEY, " +
                    "Nombre VARCHAR2(100))";
            ejecutarSQL(conn, createDatosGenero);

            // Creación de la tabla PerteneceA
            String createPerteneceA = "CREATE TABLE PerteneceA (" +
                    "IDPelicula NUMBER, " +
                    "IDGenero NUMBER, " +
                    "PRIMARY KEY (IDPelicula, IDGenero), " +
                    "FOREIGN KEY (IDPelicula) REFERENCES DatosPelicula(IDPelicula), " +
                    "FOREIGN KEY (IDGenero) REFERENCES DatosGenero(IDGenero))";
            ejecutarSQL(conn, createPerteneceA);

            // Creación de la tabla Actores
            String createActores = "CREATE TABLE Actores (" +
                    "NombreActor VARCHAR2(100) PRIMARY KEY)";
            ejecutarSQL(conn, createActores);

            // Creación de la tabla Actua
            String createActua = "CREATE TABLE Actua (" +
                    "IDPelicula NUMBER, " +
                    "NombreActor VARCHAR2(100), " +
                    "PRIMARY KEY (IDPelicula, NombreActor), " +
                    "FOREIGN KEY (IDPelicula) REFERENCES DatosPelicula(IDPelicula), " +
                    "FOREIGN KEY (NombreActor) REFERENCES Actores(NombreActor))";
            ejecutarSQL(conn, createActua);

            // Creación de la tabla DatosCliente
            String createDatosCliente = "CREATE TABLE DatosCliente (" +
                    "CorreoElectronico VARCHAR2(100) PRIMARY KEY, " +
                    "Nombre VARCHAR2(100), " +
                    "Apellidos VARCHAR2(100), " +
                    "Telefono VARCHAR2(20), " +
                    "FechaAlta DATE, " +
                    "FechaBaja DATE)";
            ejecutarSQL(conn, createDatosCliente);

            // Creación de la tabla DatosTurno
            String createDatosTurno = "CREATE TABLE DatosTurno (" +
                    "NombreTurno VARCHAR2(100) PRIMARY KEY, " +
                    "HoraEntrada FLOAT, " +
                    "HoraSalida FLOAT, " +
                    "SueldoHora FLOAT, " +
                    "SueldoTotal FLOAT)";
            ejecutarSQL(conn, createDatosTurno);

            // Creación de la tabla DatosEmpleado
            String createDatosEmpleado = "CREATE TABLE DatosEmpleado (" +
                    "DNI VARCHAR2(20) PRIMARY KEY, " +
                    "Nombre VARCHAR2(100), " +
                    "Apellidos VARCHAR2(100), " +
                    "Telefono VARCHAR2(20), " +
                    "Sueldo FLOAT, " +
                    "Direccion VARCHAR2(200), " +
                    "FechaBaja DATE DEFAULT NULL, " +
                    "NombreTurno VARCHAR2(100), " +
                    "FOREIGN KEY (NombreTurno) REFERENCES DatosTurno(NombreTurno))";
            ejecutarSQL(conn, createDatosEmpleado);

            // Creación de la tabla DatosAlquiler
            String createDatosAlquiler = "CREATE TABLE DatosAlquiler (" +
                    "CorreoElectronico VARCHAR2(100), " +
                    "IDPelicula NUMBER, " +
                    "FechaAlquiler DATE, " +
                    "FechaVencimiento DATE, " +
                    "FechaAcceso DATE, " +
                    "Calificacion NUMBER, " +
                    "PRIMARY KEY (CorreoElectronico, IDPelicula), " +
                    "FOREIGN KEY (CorreoElectronico) REFERENCES DatosCliente(CorreoElectronico), " +
                    "FOREIGN KEY (IDPelicula) REFERENCES DatosPelicula(IDPelicula))";
            ejecutarSQL(conn, createDatosAlquiler);

            // Creación de la tabla PrecioAlquiler
            String createPrecioAlquiler = "CREATE TABLE PrecioAlquiler (" +
                    "IDPelicula NUMBER, " +
                    "FechaAlquiler DATE, " +
                    "FechaVencimiento DATE, " +
                    "PrecioAlquiler NUMBER, " +
                    "PRIMARY KEY (IDPelicula, FechaAlquiler, FechaVencimiento), " +
                    "FOREIGN KEY (IDPelicula) REFERENCES DatosPelicula(IDPelicula))";
            ejecutarSQL(conn, createPrecioAlquiler);

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
            stmt.setFloat(5, 40.0f);
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
