package dam.jmdh;

import java.sql.*;

public class JDBC_c1aves_autogeneradas {
    public static void main(String[] args) {
        String basedatos = "bbdd_ejemplos_libro";
        String host = "localhost";
        String port = "3306";
        String urlConnection = "jdbc:mysql://" + host + ":" + port + "/" + basedatos;
        String user = "root";
        String pwd = "pass";

        try (Connection c = DriverManager.getConnection(urlConnection, user, pwd)) {
            try (
                 // Segundo párametro indica que devuelva la clave autogenerada al insertar
                 PreparedStatement sInsertFact = c.prepareStatement("INSERT INTO FACTURAS (DNI_CLIENTE) VALUES (?);", PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement sInsertLineaFact = c.prepareStatement("INSERT INTO LINEAS_FACTURA(NUM_FACTURA, LINEA_FACTURA, CONCEPTO, CANTIDAD) VALUES (?,?,?,?);")
            ) {
                c.setAutoCommit(false);  // Comienzo de transacción
                int i = 1;
                sInsertFact.setString(i++, "78901234X");
                sInsertFact.executeUpdate();                // Inserción de la factura

                // Obtenemos en un ResultSet la clave autogenerada de la factura insertada.
                ResultSet rs = sInsertFact.getGeneratedKeys();
                rs.next();
                int numFact = rs.getInt(1);  // Guardadmos la factura en variabl numFact

                // Preparamos los datos para la inserción de las líneas de factura
                int lineaFact = 1;
                i=1;
                sInsertLineaFact.setInt(i++, numFact);  // Número de factura autogenerado en tabla factura.
                sInsertLineaFact.setInt(i++, lineaFact++);
                sInsertLineaFact.setString(i++, "TUERCAS");
                sInsertLineaFact.setInt(i++, 25);
                sInsertLineaFact.executeUpdate();
                i=1;
                sInsertLineaFact.setInt(i++, numFact);
                sInsertLineaFact.setInt(i++, lineaFact++);
                sInsertLineaFact.setString(i++, "TORNILLOS");
                sInsertLineaFact.setInt(i++, 250);
                sInsertLineaFact.executeUpdate();
                c.commit();         // fin transacción
            } catch (SQLException e) {
                muestraErrorSQL(e);
                try {
                    c.rollback();
                    System.err.println("Se hace ROLLBACK");
                } catch (Exception er) {
                    System.err.println("ERROR haciendo ROLLBACK");
                    er.printStackTrace(System.err);
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR de conexión");
            e.printStackTrace(System.err);
        }
    }

    public static void muestraErrorSQL(SQLException e) {
        System.err.println("SQL ERROR mensaje: " + e.getMessage());
        System.err.println("SQL Estado: " + e.getSQLState());
        System.err.println("SQL código específico: " + e.getErrorCode());
    }
}
