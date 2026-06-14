package dao;

import factory.ConexionFactory;
import modelo.Producto;
import utilidades.Seguridad;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class VentasDAO {
    private ConexionFactory factory;

    public VentasDAO() {
        this.factory = new ConexionFactory();
    }

    public Map<Integer, String> obtenerMetodosPago() {
        Map<Integer, String> metodos = new HashMap<>();
        String sqlSelect = "SELECT id_metodo_pago, nombre_metodo FROM METODOS_PAGO";
        
        try (Connection con = factory.getConexion(); 
             PreparedStatement ps = con.prepareStatement(sqlSelect); 
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                metodos.put(rs.getInt("id_metodo_pago"), rs.getString("nombre_metodo"));
            }
            
            if (metodos.isEmpty()) {
                String[] defaults = {"Efectivo", "Tarjeta", "Transferencia"};
                try {
                    String sqlInsert = "INSERT INTO METODOS_PAGO (nombre_metodo) VALUES (?)";
                    try (PreparedStatement psInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                        for (String metodo : defaults) {
                            psInsert.setString(1, metodo);
                            psInsert.executeUpdate();
                            try (ResultSet rsKeys = psInsert.getGeneratedKeys()) {
                                if (rsKeys.next()) metodos.put(rsKeys.getInt(1), metodo);
                            }
                        }
                    }
                } catch (SQLException ex) {
                    String sqlInsertManual = "INSERT INTO METODOS_PAGO (id_metodo_pago, nombre_metodo) VALUES (?, ?)";
                    try (PreparedStatement psInsertManual = con.prepareStatement(sqlInsertManual)) {
                        int id = 1;
                        for (String metodo : defaults) {
                            psInsertManual.setInt(1, id); psInsertManual.setString(2, metodo);
                            psInsertManual.executeUpdate();
                            metodos.put(id, metodo); id++;
                        }
                    }
                }
            }
        } catch (SQLException e) { System.err.println("Error obteniendo métodos de pago: " + e.getMessage()); }
        return metodos;
    }

    public boolean empresaTieneFacturacionHabilitada(int idEmpresa) {
        String sql = "SELECT habilitar_facturacion_empresa FROM EMPRESA WHERE id_empresa = ?";
        try (Connection con = factory.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBoolean("habilitar_facturacion_empresa");
            }
        } catch (SQLException e) { System.err.println("Error verificando facturación: " + e.getMessage()); }
        return false;
    }

    public Producto buscarProductoPorCodigo(String codigoBarras) {
        String sql = "SELECT * FROM INVENTARIO WHERE codigo_barras_producto = ? AND eliminado_producto = 0";
        try (Connection con = factory.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoBarras);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setCodigoBarras(rs.getString("codigo_barras_producto"));
                    p.setNombreProducto(rs.getString("nombre_producto"));
                    p.setPrecioVenta(rs.getDouble("precio_venta_producto"));
                    p.setStockProducto(rs.getInt("stock_producto"));
                    p.setRutaImagen(rs.getString("ruta_imagen_producto"));
                    return p;
                }
            }
        } catch (SQLException e) { System.err.println("Error buscando producto por código: " + e.getMessage()); }
        return null;
    }

    // --- AQUÍ SE VALIDA LA CONTRASEÑA EN LA BASE DE DATOS ---
    public int obtenerIdUsuarioPorPassword(String passwordPlana) {
        String hash = Seguridad.encriptarSHA256(passwordPlana);
        String sql = "SELECT id_usuario FROM USUARIOS WHERE password_hash = ? AND estado_usuario = 1";
        try (Connection con = factory.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_usuario");
            }
        } catch (SQLException e) { System.err.println("Error validando usuario: " + e.getMessage()); }
        return -1; 
    }

    public boolean procesarVentaCompleta(int idCliente, int idUsuario, int idMetodoPago, double subtotal, double impuesto, double total, List<Object[]> detalles) {
        String sqlVenta = "INSERT INTO VENTAS (fecha_venta, id_cliente_venta, id_usuario, id_metodo_pago, subtotal_venta, impuesto_venta, total_venta) VALUES (GETDATE(), ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO DETALLES_VENTA (id_ventas, id_producto, descripcion_venta, cantidad_venta, precio_unitario_venta, subtotal_venta) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlStockActual = "SELECT stock_producto FROM INVENTARIO WHERE id_producto = ?";
        String sqlRestarStock = "UPDATE INVENTARIO SET stock_producto = ? WHERE id_producto = ?";
        
        // El orden de las columnas ajustado exactamente a tu imagen del Kardex
        String sqlKardex = "INSERT INTO KARDEX (id_producto, id_usuario, fecha_movimiento_producto, tipo_movimiento_producto, cantidad_producto, stock_restante_producto, referencia_producto) VALUES (?, ?, GETDATE(), 'Salida', ?, ?, 'Venta en POS')";

        Connection con = null;
        try {
            con = factory.getConexion();
            con.setAutoCommit(false); 

            int idVentaGenerado = 0;
            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, idCliente); psVenta.setInt(2, idUsuario); psVenta.setInt(3, idMetodoPago);
                psVenta.setDouble(4, subtotal); psVenta.setDouble(5, impuesto); psVenta.setDouble(6, total);
                psVenta.executeUpdate();
                try (ResultSet rsKeys = psVenta.getGeneratedKeys()) { if (rsKeys.next()) idVentaGenerado = rsKeys.getInt(1); }
            }

            if (idVentaGenerado == 0) { con.rollback(); return false; }

            try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
                 PreparedStatement psStockActual = con.prepareStatement(sqlStockActual);
                 PreparedStatement psStockUpdate = con.prepareStatement(sqlRestarStock);
                 PreparedStatement psKardex = con.prepareStatement(sqlKardex)) {
                
                for (Object[] fila : detalles) {
                    int idProd = (int) fila[0]; String nombre = (String) fila[2];
                    int cantidad = (int) fila[3]; double precio = (double) fila[4]; double subtotFila = (double) fila[5];

                    psDetalle.setInt(1, idVentaGenerado); psDetalle.setInt(2, idProd); psDetalle.setString(3, nombre);
                    psDetalle.setInt(4, cantidad); psDetalle.setDouble(5, precio); psDetalle.setDouble(6, subtotFila);
                    psDetalle.executeUpdate();

                    int stockActual = 0;
                    psStockActual.setInt(1, idProd);
                    try (ResultSet rsStock = psStockActual.executeQuery()) { if (rsStock.next()) stockActual = rsStock.getInt("stock_producto"); }
                    
                    int stockRestante = stockActual - cantidad;

                    psStockUpdate.setInt(1, stockRestante); psStockUpdate.setInt(2, idProd);
                    psStockUpdate.executeUpdate();

                    psKardex.setInt(1, idProd); psKardex.setInt(2, idUsuario); psKardex.setInt(3, cantidad); psKardex.setInt(4, stockRestante);
                    psKardex.executeUpdate();
                }
            }
            con.commit(); return true;
        } catch (SQLException e) {
            if (con != null) try { con.rollback(); } catch (SQLException ex) { }
            System.err.println("Error procesando venta y kardex: " + e.getMessage()); return false;
        } finally {
            if (con != null) try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { }
        }
    }
}