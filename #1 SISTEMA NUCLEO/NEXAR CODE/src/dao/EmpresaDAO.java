package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import factory.ConexionFactory;
import modelo.Empresa;

public class EmpresaDAO {

    private ConexionFactory factory;

    public EmpresaDAO() {
        this.factory = new ConexionFactory();
    }

    /**
     * Trae los datos de la empresa desde SQL Server.
     */
    public Empresa obtenerDatos() {
        Empresa emp = null;
        String sql = "SELECT TOP 1 * FROM EMPRESA ORDER BY id_empresa ASC";

        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                emp = new Empresa();
                emp.setIdEmpresa(rs.getInt("id_empresa"));
                emp.setNombreEmpresa(rs.getString("nombre_empresa"));
                emp.setRtnEmpresa(rs.getString("rtn_empresa"));
                emp.setDuenoEmpresa(rs.getString("dueño_empresa")); 
                emp.setDireccionEmpresa(rs.getString("direccion_empresa"));
                emp.setEstadoEmpresa(rs.getBoolean("estado_empresa"));
                emp.setHabilitarFacturacion(rs.getBoolean("habilitar_facturacion_empresa"));
                emp.setNumeroTelefono(rs.getString("numero_telefono"));
                emp.setTelefonoSecundario(rs.getString("telefono_secundario"));
                emp.setWhatsapp(rs.getString("whatsapp_empresa"));
                emp.setEmail(rs.getString("email_empresa"));
                emp.setWeb(rs.getString("web_empresa"));
                emp.setFacebook(rs.getString("facebook_empresa"));
                
                // --- Nuevos campos recuperados ---
                emp.setMensajeTicketPieFactura(rs.getString("mensaje_ticket_pie_factura"));
                emp.setMensajeTicketPieRecibo(rs.getString("mensaje_ticket_pie_recibo"));
                emp.setMensajeTicketEntrega(rs.getString("mensaje_ticket_entrega"));
                emp.setMensajeTicketPieCotizacion(rs.getString("mensaje_ticket_pie_cotizacion"));
                emp.setLogoEmpresaRuta(rs.getString("logo_empresa_ruta"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener datos de empresa: " + e.getMessage());
        }
        return emp; 
    }

    /**
     * Si la empresa ya existe, hace UPDATE. Si la tabla está vacía, hace INSERT.
     */
    public boolean guardarOActualizar(Empresa emp) {
        Empresa existe = obtenerDatos();
        String sql;

        if (existe == null) {
            // No existe, hacemos INSERT (17 parámetros)
            sql = "INSERT INTO EMPRESA (nombre_empresa, rtn_empresa, dueño_empresa, direccion_empresa, estado_empresa, "
                + "habilitar_facturacion_empresa, numero_telefono, telefono_secundario, whatsapp_empresa, "
                + "email_empresa, web_empresa, facebook_empresa, mensaje_ticket_pie_factura, "
                + "mensaje_ticket_pie_recibo, mensaje_ticket_entrega, mensaje_ticket_pie_cotizacion, logo_empresa_ruta) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            // Ya existe, hacemos UPDATE usando su ID (17 parámetros + 1 para el WHERE = 18 en total)
            emp.setIdEmpresa(existe.getIdEmpresa()); 
            sql = "UPDATE EMPRESA SET nombre_empresa=?, rtn_empresa=?, dueño_empresa=?, direccion_empresa=?, "
                + "estado_empresa=?, habilitar_facturacion_empresa=?, numero_telefono=?, telefono_secundario=?, "
                + "whatsapp_empresa=?, email_empresa=?, web_empresa=?, facebook_empresa=?, "
                + "mensaje_ticket_pie_factura=?, mensaje_ticket_pie_recibo=?, mensaje_ticket_entrega=?, "
                + "mensaje_ticket_pie_cotizacion=?, logo_empresa_ruta=? WHERE id_empresa=?";
        }

        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emp.getNombreEmpresa());
            ps.setString(2, emp.getRtnEmpresa());
            ps.setString(3, emp.getDuenoEmpresa());
            ps.setString(4, emp.getDireccionEmpresa());
            ps.setBoolean(5, emp.isEstadoEmpresa());
            ps.setBoolean(6, emp.isHabilitarFacturacion());
            ps.setString(7, emp.getNumeroTelefono());
            ps.setString(8, emp.getTelefonoSecundario());
            ps.setString(9, emp.getWhatsapp());
            ps.setString(10, emp.getEmail());
            ps.setString(11, emp.getWeb());
            ps.setString(12, emp.getFacebook());
            
            // --- Nuevos parámetros (13 al 17) ---
            ps.setString(13, emp.getMensajeTicketPieFactura());
            ps.setString(14, emp.getMensajeTicketPieRecibo());
            ps.setString(15, emp.getMensajeTicketEntrega());
            ps.setString(16, emp.getMensajeTicketPieCotizacion());
            ps.setString(17, emp.getLogoEmpresaRuta());

            // Si es UPDATE, agregamos el parámetro 18 para el WHERE
            if (existe != null) {
                ps.setInt(18, emp.getIdEmpresa());
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar empresa: " + e.getMessage());
            return false;
        }
    }
}