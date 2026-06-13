package utilidades;

import modelo.Empresa;
import javax.swing.*;
import java.awt.*;

public class GeneradorTickets {

    /**
     * Construye la vista previa del ticket para la interfaz gráfica.
     * Recibe un JTextArea para que el texto final sea editable en vivo.
     */
    public static JPanel crearTicketVistaPrevia(String tituloDocumento, JTextArea txtAreaEditable) {
        JPanel panelCentrador = new JPanel(new GridBagLayout());
        panelCentrador.setBackground(new Color(30, 30, 30)); 

        JPanel pnlTicket = new JPanel();
        pnlTicket.setLayout(new BoxLayout(pnlTicket, BoxLayout.Y_AXIS));
        pnlTicket.setBackground(Color.WHITE);
        pnlTicket.setPreferredSize(new Dimension(280, 450));
        pnlTicket.setMinimumSize(new Dimension(280, 450));
        pnlTicket.setMaximumSize(new Dimension(280, 450));

        pnlTicket.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        Font fBold = new Font("Courier New", Font.BOLD, 14);
        Font fNormal = new Font("Courier New", Font.PLAIN, 12);

        Empresa emp = SesionGlobal.getEmpresaActual();
        String empNombre = emp != null && emp.getNombreEmpresa() != null ? emp.getNombreEmpresa().toUpperCase() : "NEXAR WORKSHOP";
        String empRtn = emp != null && emp.getRtnEmpresa() != null ? "RTN: " + emp.getRtnEmpresa() : "RTN: PENDIENTE";
        
        pnlTicket.add(crearLabelCentrado(empNombre, fBold, Color.BLACK));
        pnlTicket.add(crearLabelCentrado(empRtn, fNormal, Color.BLACK));
        pnlTicket.add(Box.createVerticalStrut(5));
        pnlTicket.add(crearLabelCentrado(tituloDocumento, fNormal, Color.DARK_GRAY));
        pnlTicket.add(Box.createVerticalStrut(10));
        pnlTicket.add(crearLabelCentrado("===============================", fNormal, Color.BLACK));
        
        // --- Simulación de Items (Luego esto será un ciclo for con datos reales) ---
        pnlTicket.add(Box.createVerticalStrut(10));
        pnlTicket.add(crearLabelCentrado("CANT  DESCRIPCION      TOTAL", fNormal, Color.BLACK));
        pnlTicket.add(crearLabelCentrado("-------------------------------", fNormal, Color.BLACK));
        pnlTicket.add(crearLabelCentrado(" 1x   Reemplazo LCD    L 1500", fNormal, Color.BLACK));
        pnlTicket.add(crearLabelCentrado("-------------------------------", fNormal, Color.BLACK));
        pnlTicket.add(crearLabelCentrado("TOTAL:                 L 1500", fBold, Color.BLACK));
        pnlTicket.add(Box.createVerticalStrut(20));

        pnlTicket.add(crearLabelCentrado("===============================", fNormal, Color.BLACK));
        pnlTicket.add(txtAreaEditable);

        panelCentrador.add(pnlTicket);
        return panelCentrador;
    }

    /**
     * Método auxiliar genérico para centrar textos en los tickets.
     */
    public static JLabel crearLabelCentrado(String texto, Font fuente, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
}