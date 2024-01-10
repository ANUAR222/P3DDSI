import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Scanner;
@SuppressWarnings("SqlResolve")
public class Pelicula {
    public static void insertarPelicula(Connection conn) throws SQLException, ParseException {
        conn.setAutoCommit(false);
        Savepoint saveInsertPelicula= conn.setSavepoint();
        String sqlInsertPelicula= "INSERT INTO DatosPelicula (Nombre, Precio, FechaEstreno, FechaAlta, Sinopsis) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement insertPelicula= conn.prepareStatement(sqlInsertPelicula);
        Scanner sc= new Scanner(System.in);
        System.out.println("Introduce el nombre de la pelicula");
        insertPelicula.setString(1, sc.nextLine());
        System.out.println("Intoduce el precio");
        double precio=sc.nextDouble();
        while (precio<0){
            System.out.println("Intoduce el precio correctamente");
            precio=sc.nextDouble();
        }
        insertPelicula.setDouble(2, precio);
        LocalDate fechaAlta = LocalDate.now();
        insertPelicula.setDate(4, Date.valueOf(fechaAlta));
        System.out.println("Selecciona el dato a introducir o finaliza:\n1.Fecha Estreno\n2.Sinopsis\n3.Finalizar");
        int opcion=sc.nextInt();
        insertPelicula.setDate(3, null);
        insertPelicula.setString(5, null);
        while (opcion!=3){
            if(opcion==1) {
                System.out.println("Introduce la fecha");
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                String fechaString = sc.nextLine();
                // Convertir el String a Date
                Date fechaEstreno = (Date) formato.parse(fechaString);
                insertPelicula.setDate(3, fechaEstreno);
            }
            else if(opcion==2){
                System.out.println("Introduce la sinopsis");
                insertPelicula.setString(5, sc.nextLine());
            }
            System.out.println("Selecciona el dato a introducir o finaliza:\n1.Fecha Estreno\n2.Sinopsis\n3.Finalizar");
            opcion = sc.nextInt();
        }
        insertPelicula.execute();
        mostrarPelicula(conn, -1);
        System.out.println("¿Quieres confirmar los cambios?\n1. Si\n2. No");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                break;
            case 2:
                conn.rollback(saveInsertPelicula);
                break;
        }
        conn.setAutoCommit(true);
    }

    public static boolean comprobarIdPelicula(Connection conn, int idPelicula) throws SQLException{
        String sqlComprobar = "SELECT COUNT(*) FROM DatosPelicula WHERE IDPelicula = ?";
        PreparedStatement comprobar = conn.prepareStatement(sqlComprobar);
        comprobar.setInt(1, idPelicula);
        ResultSet resultComprobar = comprobar.executeQuery();
        if(resultComprobar.next()){
            if (resultComprobar.getInt(1)!=1){
                System.out.println("Id incorrecto. Se cancela la operacion");
                return false;
            }
            else return true;
        }
        else return false;
    }

    public static boolean comprobarBajaPelicula(Connection conn, int idpelicula) throws SQLException{
        String sqlComprobarBaja = "SELECT FechaBaja FROM DatosPelicula WHERE IDPelicula = ?";
        PreparedStatement comprobarBaja = conn.prepareStatement(sqlComprobarBaja);
        comprobarBaja.setInt(1, idpelicula);
        ResultSet resultComprobarBaja = comprobarBaja.executeQuery();

        if (resultComprobarBaja.next()) {
            Date fechaBaja = resultComprobarBaja.getDate("FechaBaja");
            return fechaBaja != null;
        }
        return true;
    }
    public static void mostrarPelicula(Connection conn, int idPelicula) throws SQLException {
        String sqlSelect;
        PreparedStatement select;
        if(idPelicula==-1){
            sqlSelect = "SELECT * FROM DatosPelicula ORDER BY IDPelicula DESC FETCH FIRST 1 ROW ONLY";
            select = conn.prepareStatement(sqlSelect);
        }else {
            sqlSelect = "SELECT * FROM DatosPelicula WHERE IDPelicula = ?";
            select = conn.prepareStatement(sqlSelect);
            select.setInt(1, idPelicula);
        }
        ResultSet resultSet = select.executeQuery();
        if (resultSet.next()) {

            System.out.println("IDPelicula: " + resultSet.getInt("IDPelicula"));
            System.out.println("Nombre: " + resultSet.getString("Nombre"));
            System.out.println("Precio: " + resultSet.getDouble("Precio"));
            System.out.println("Fecha Estreno: " + resultSet.getDate("FechaEstreno"));
            System.out.println("Fecha Alta: " + resultSet.getDate("FechaAlta"));
            System.out.println("Fecha Baja" + resultSet.getDate("FechaBaja"));
            System.out.println("Sinopsis: " + resultSet.getString("Sinopsis"));
            System.out.println("Calificacion: " + resultSet.getDouble("Calificacion"));
            System.out.println("Reseñas: " + resultSet.getInt("Reseñas"));

        }
    }
    public static void bajaPelicula(Connection conn) throws SQLException{
        conn.setAutoCommit(false);
        Savepoint saveBajaPelicula=conn.setSavepoint();
        Scanner sc= new Scanner(System.in);
        System.out.println("Introduce la id de la pelicula a dar de baja");
        int idPelicula= sc.nextInt();
        if(!comprobarIdPelicula(conn, idPelicula)){
            conn.rollback(saveBajaPelicula);
            conn.setAutoCommit(true);
            return;
        }
        String sqlBaja = "UPDATE DatosPelicula SET FechaBaja = ? WHERE IDPelicula = ?";
        PreparedStatement baja = conn.prepareStatement(sqlBaja);
        LocalDate fechaBaja = LocalDate.now();
        baja.setDate(1, Date.valueOf(fechaBaja));
        baja.setInt(2, idPelicula);
        baja.execute();
        System.out.println("¿Quieres confirmar la baja de la pelicula con id: " + idPelicula + " ?\n1. Si\n2. No");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                break;
            case 2:
                conn.rollback(saveBajaPelicula);
                break;
        }
        conn.setAutoCommit(true);
    }

    public static void modificarPelicula(Connection conn) throws SQLException, ParseException {
        conn.setAutoCommit(false);
        Savepoint saveUpdatePelicula=conn.setSavepoint();
        Scanner sc= new Scanner(System.in);
        System.out.println("Introduce la id de la pelicula a dar de baja");
        int idPelicula= sc.nextInt();
        if(!comprobarIdPelicula(conn, idPelicula)) {
            conn.rollback(saveUpdatePelicula);
            conn.setAutoCommit(true);
            return;
        }
        if(comprobarBajaPelicula(conn, idPelicula)){
            System.out.println("La pelicula esta dada de baja.");
            return;
        }
        System.out.println("¿Que campo desea modificar?:\n1. Nombre\n2. Precio\n3. Fecha Estreno\n4. Sinopsis\n5. Finalizar");
        int opcion = sc.nextInt();
        PreparedStatement updatePelicula;
        String sqlUpdatePelicula;
        while (opcion!=5) {
            switch (opcion) {
                case 1:
                    sqlUpdatePelicula = "UPDATE DatosPelicula SET Nombre = ? WHERE IDPelicula = ?";
                    updatePelicula = conn.prepareStatement(sqlUpdatePelicula);
                    updatePelicula.setInt(2, idPelicula);
                    System.out.println("Introduce el nombre");
                    updatePelicula.setString(1, sc.nextLine());
                    updatePelicula.execute();
                    break;
                case 2:
                    sqlUpdatePelicula = "UPDATE DatosPelicula SET Precio = ? WHERE IDPelicula = ?";
                    updatePelicula = conn.prepareStatement(sqlUpdatePelicula);
                    updatePelicula.setInt(2, idPelicula);
                    System.out.println("Introduce el precio");
                    double precio = sc.nextDouble();
                    while (precio < 0) {
                        System.out.println("Intoduce el precio correctamente");
                        precio = sc.nextDouble();
                    }
                    updatePelicula.setDouble(1, precio);
                    updatePelicula.execute();
                    break;
                case 3:
                    sqlUpdatePelicula = "UPDATE DatosPelicula SET FechaEstreno = ? WHERE IDPelicula = ?";
                    updatePelicula = conn.prepareStatement(sqlUpdatePelicula);
                    updatePelicula.setInt(2, idPelicula);
                    System.out.println("Introduce la Fecha de Estreno");
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaString = sc.nextLine();
                    // Convertir el String a Date
                    Date fechaEstreno = (Date) formato.parse(fechaString);
                    updatePelicula.setDate(1, fechaEstreno);
                    updatePelicula.execute();
                    break;
                case 4:
                    sqlUpdatePelicula = "UPDATE DatosPelicula SET Sinopsis = ? WHERE IDPelicula = ?";
                    updatePelicula = conn.prepareStatement(sqlUpdatePelicula);
                    updatePelicula.setInt(2, idPelicula);
                    System.out.println("Introduce la sinopsis");
                    updatePelicula.setString(1, sc.nextLine());
                    updatePelicula.execute();
                    break;
            }
            System.out.println("¿Que campo desea modificar?:\n1. Nombre\n2. Precio\n3. Fecha Estreno\n4. Sinopsis\n5. Finalizar");
            opcion = sc.nextInt();
        }

        String sqlSelect = "SELECT * FROM DatosPelicula WHERE IDPelicula = ?";
        PreparedStatement select = conn.prepareStatement(sqlSelect);
        select.setInt(1, idPelicula);
        ResultSet resultSet = select.executeQuery();
        if (resultSet.next()) {

            String nombre = resultSet.getString("Nombre");
            double precioSelect = resultSet.getDouble("Precio");
            Date fechaEstreno = resultSet.getDate("FechaEstreno");
            Date fechaAltaSelect = resultSet.getDate("FechaAlta");
            Date fechaBaja = resultSet.getDate("FechaBaja");
            String sinopsisSelect = resultSet.getString("Sinopsis");
            double calificacion = resultSet.getDouble("Calificacion");
            int reseñas = resultSet.getInt("Reseñas");


            // Mostrar la información de la última película
            System.out.println("IDPelicula: " + idPelicula);
            System.out.println("Nombre: " + nombre);
            System.out.println("Precio: " + precioSelect);
            System.out.println("Fecha Estreno: " + fechaEstreno);
            System.out.println("Fecha Alta: " + fechaAltaSelect);
            System.out.println("Fecha Baja" + fechaBaja);
            System.out.println("Sinopsis: " + sinopsisSelect);
            System.out.println("Calificacion: " + calificacion);
            System.out.println("Reseñas: " + reseñas);

        }
        System.out.println("¿Quieres confirmar los cambios?\n1. Si\n2. No");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                break;
            case 2:
                conn.rollback(saveUpdatePelicula);
                break;
        }
        conn.setAutoCommit(true);

    }
    public static void insertarGenero(Connection conn) throws SQLException {
        System.out.println("Introduce el nombre del genero");
        String sqlInsertGenero = "INSERT INTO DatosGenero (Nombre) Values (?)";
        PreparedStatement insertGenero = conn.prepareStatement(sqlInsertGenero);
        Scanner sc = new Scanner(System.in);
        insertGenero.setString(1, sc.nextLine());
        insertGenero.execute();
    }
    public static void mostrarGenero(Connection conn) throws SQLException{
        String sqlSelect = "SELECT * FROM DatosGenero";
        PreparedStatement select = conn.prepareStatement(sqlSelect);
        ResultSet resultSelect = select.executeQuery();
        while(resultSelect.next()){
            System.out.println("ID: " + resultSelect.getInt("IDGenero") + " Nombre: " + resultSelect.getString("Nombre"));
        }
    }
    public static void insertarActor(Connection conn) throws SQLException{
        System.out.println("Introduce el nombre del Actor");
        String sqlInsertActor = "INSERT INTO Actores (NombreActor) Values (?)";
        PreparedStatement insertActor = conn.prepareStatement(sqlInsertActor);
        Scanner sc = new Scanner(System.in);
        insertActor.setString(1, sc.nextLine());
        insertActor.execute();
    }
    public static void mostrarActor(Connection conn) throws SQLException{
        String sqlSelect = "SELECT * FROM Actores";
        PreparedStatement select = conn.prepareStatement(sqlSelect);
        ResultSet resultSelect = select.executeQuery();
        while(resultSelect.next()){
            System.out.println("Nombre: " + resultSelect.getString("NombreActor"));
        }
    }

    public static void mostrarGenerosPelicula(Connection conn, int idPelicula) throws SQLException{
        String sqlSelect = "SELECT IDGenero FROM PerteneceA WHERE IDPelicula = ?";
        PreparedStatement select = conn.prepareStatement(sqlSelect);
        ResultSet resultSelect = select.executeQuery();
        sqlSelect = "SELECT Nombre FROM DatosGenero WHERE IDGenero = ?";
        select = conn.prepareStatement(sqlSelect);
        ResultSet resultSelectGenero;
        System.out.println("Generos a los que pertenece la pelicula con id: " + idPelicula);
        while (resultSelect.next()){
            select.setInt(1, resultSelect.getInt("IDGenero"));
            resultSelectGenero = select.executeQuery();
            if(resultSelectGenero.next()){
                System.out.println(resultSelectGenero.getString("Nombre"));
            }
        }
    }
    public static void mostrarActoresPelicula(Connection conn, int idPelicula) throws SQLException{
        String sqlSelect = "SELECT NombreActor FROM Actua WHERE IDPelicula = ?";
        PreparedStatement select = conn.prepareStatement(sqlSelect);
        ResultSet resultSelect = select.executeQuery();
        System.out.println("Los actores que participan en esta pelicula con id: " + idPelicula + " son:");
        while (resultSelect.next()){
            System.out.println(resultSelect.getString("NombreActor"));
        }
    }
    public static void añadirGeneroActor(Connection conn) throws SQLException{
        conn.setAutoCommit(false);
        Savepoint saveAñadir = conn.setSavepoint();
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce el id de la pelicula");
        int idPelicula = sc.nextInt();
        if(!comprobarIdPelicula(conn, idPelicula)||comprobarBajaPelicula(conn, idPelicula)){
            System.out.println("El id es incorrecto o esta dada de baja");
            return;
        }
        System.out.println("Selecciona una opcion:\n1. Añadir genero\n2. Añadir Actor\n3. Mostrar Generos\n4. Mostrar Actores\n5. Finalizar");
        int opcion = sc.nextInt();
        String sqlInsert;
        PreparedStatement insert;
        String sqlComprobar;
        PreparedStatement comprobar;
        ResultSet resultComprobar;
        while (opcion!=5){
            switch (opcion){
                case 1:
                    sqlInsert = "INSERT INTO PerteneceA (IDPelicula, IDGenero) VALUES (?, ?)";
                    insert = conn.prepareStatement(sqlInsert);
                    insert.setInt(1, idPelicula);
                    System.out.println("Introduce el genero");
                    int idGenero = sc.nextInt();
                    sqlComprobar = "SELECT COUNT(*) FROM DatosGenero WHERE IDGenero = ?";
                    comprobar = conn.prepareStatement(sqlComprobar);
                    comprobar.setInt(1, idGenero);
                    resultComprobar = comprobar.executeQuery();
                    if(resultComprobar.next()){
                        if (resultComprobar.getInt(1)!=1){
                            System.out.println("Id incorrecto. Se cancela la operacion");
                            break;
                        }
                    }
                    insert.setInt(2, idGenero);
                    insert.execute();
                    break;
                case 2:
                    sqlInsert = "INSERT INTO Actua (IDPelicula, NombreActor) VALUES (?, ?)";
                    insert = conn.prepareStatement(sqlInsert);
                    insert.setInt(1, idPelicula);
                    System.out.println("Introduce el nombre del actor");
                    String nombreActor = sc.nextLine();
                    sqlComprobar = "SELECT COUNT(*) FROM Actores WHERE NombreActor = ?";
                    comprobar = conn.prepareStatement(sqlComprobar);
                    comprobar.setString(1, nombreActor);
                    resultComprobar = comprobar.executeQuery();
                    if(resultComprobar.next()){
                        if (resultComprobar.getInt(1)!=1){
                            System.out.println("Nombre incorrecto. Se cancela la operacion");
                            break;
                        }
                    }
                    insert.setString(2, nombreActor);
                    insert.execute();
                    break;
                case 3:
                    mostrarGenero(conn);
                    break;
                case 4:
                    mostrarActor(conn);
                    break;
            }
            System.out.println("Selecciona una opcion:\n1. Añadir genero\n2. Añadir Actor\n3. Mostrar Generos\n4. Mostrar Actores\n5. Finalizar");
            opcion = sc.nextInt();
        }
        mostrarActoresPelicula(conn, idPelicula);
        mostrarGenerosPelicula(conn, idPelicula);
        System.out.println("¿Quieres confirmar los cambios?\n1. Si\n2. No");
        switch (sc.nextInt()){
            case 1:
                conn.commit();
                break;
            case 2:
                conn.rollback(saveAñadir);
                break;
        }
        conn.setAutoCommit(true);
    }
}
