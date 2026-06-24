package dao;

import factory.ConexionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class GarantiaDAO {
    private ConexionFactory factory;

    public GarantiaDAO() {
        this.factory = new ConexionFactory();
    }

    public List<Object[]> listarGarantias() {
        List<Object[]> lista = new ArrayList<>();
        
        // Cruzamos Ventas, Detalles y Clientes. Filtramos solo los que tienen garantía (> 0)
        String sql = "SELECT v.id_ventas, v.id_cliente_venta, c.nombre_cliente, c.apellido_cliente, " +
                     "d.descripcion_venta, d.identificador_serie, v.fecha_venta, d.dias_garantia " +
                     "FROM DETALLES_VENTA d " +
                     "INNER JOIN VENTAS v ON d.id_ventas = v.id_ventas " +
                     "LEFT JOIN CLIENTES c ON v.id_cliente_venta = c.id_cliente " +
                     "WHERE d.dias_garantia > 0 " +
                     "ORDER BY v.fecha_venta DESC";

        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date hoy = new Date();

            while (rs.next()) {
                int idVenta = rs.getInt("id_ventas");
                int idCliente = rs.getInt("id_cliente_venta");
                
                // Formatear el nombre del cliente o Consumidor Final
                String nombreClie = rs.getString("nombre_cliente");
                if (rs.getString("apellido_cliente") != null) {
                    nombreClie += " " + rs.getString("apellido_cliente");
                }
                if (idCliente == 1 || nombreClie == null || nombreClie.trim().isEmpty()) {
                    nombreClie = "CONSUMIDOR FINAL";
                }

                String producto = rs.getString("descripcion_venta");
                String serie = rs.getString("identificador_serie");
                if (serie == null || serie.trim().isEmpty()) serie = "N/A";

                java.sql.Timestamp fechaVenta = rs.getTimestamp("fecha_venta");
                int diasGarantia = rs.getInt("dias_garantia");

                // Cálculo matemático de la fecha de vencimiento
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaVenta);
                cal.add(Calendar.DAY_OF_YEAR, diasGarantia);
                Date fechaVencimiento = cal.getTime();

                // Determinar el estado
                String estado = "VENCIDA";
                if (fechaVencimiento.after(hoy)) {
                    estado = "VIGENTE";
                }

                // Empaquetar la fila exacta como la espera el modelo de la tabla
                lista.add(new Object[]{
                    "Venta #" + idVenta,
                    nombreClie.trim(),
                    producto,
                    serie,
                    sdf.format(fechaVenta),
                    sdf.format(fechaVencimiento),
                    estado,
                    idVenta, // ID Venta Oculto
                    0        // ID Detalle Oculto (Reservado por si lo ocupamos después)
                });
            }
        } catch (SQLException e) {
            System.err.println("Error listando garantías en Orion Systems: " + e.getMessage());
        }
        return lista;
    }
}