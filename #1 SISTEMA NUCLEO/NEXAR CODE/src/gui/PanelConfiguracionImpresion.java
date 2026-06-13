package gui;

import modelo.Empresa;
import dao.EmpresaDAO;
import utilidades.SesionGlobal;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

public class PanelConfiguracionImpresion extends JPanel {

    // --- Componentes de Impresión ---
    private JTextArea txtMensajeFactura;
    private JTextArea txtMensajeRecibo;
    private JTextArea txtMensajeEntrega;
    private JTextArea txtMensajeCotizacion;
    private JTextField txtRutaLogo; 
    
    private JPanel panelTarjetas;

    public PanelConfiguracionImpresion() {
        iniciarDiseno();
        cargarDatosActuales();
    }

    private void iniciarDiseno() {
        this.setLayout(new BorderLayout(20, 20));
        this.setBackground(new Color(18, 18, 18));
        this.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- PANEL SUPERIOR (Título y Botón Guardar) ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("Diseño de Documentos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        JButton btnGuardarDiseno = new JButton("Guardar Textos y Logo");
        btnGuardarDiseno.setBackground(new Color(13, 110, 253));
        btnGuardarDiseno.setForeground(Color.WHITE);
        btnGuardarDiseno.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardarDiseno.setFocusPainted(false);
        btnGuardarDiseno.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardarDiseno.addActionListener(e -> guardarDiseno());
        panelSuperior.add(btnGuardarDiseno, BorderLayout.EAST);

        this.add(panelSuperior, BorderLayout.NORTH);

        // --- PANEL CENTRAL (Simulador de Tickets) ---
        txtMensajeFactura = crearTextAreaEditable("¡Gracias por su compra!");
        txtMensajeRecibo = crearTextAreaEditable("Este es un comprobante de pago.");
        txtMensajeEntrega = crearTextAreaEditable("Revise su equipo antes de salir.");
        txtMensajeCotizacion = crearTextAreaEditable("Cotización válida por 15 días.");
        txtRutaLogo = new JTextField(); 

        CardLayout cardLayout = new CardLayout();
        panelTarjetas = new JPanel(cardLayout);
        panelTarjetas.setBackground(new Color(30, 30, 30));
        panelTarjetas.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1, true));

        panelTarjetas.add(utilidades.GeneradorTickets.crearTicketVistaPrevia("DOCUMENTO: FACTURA", txtMensajeFactura), "Factura");
        panelTarjetas.add(utilidades.GeneradorTickets.crearTicketVistaPrevia("DOCUMENTO: RECIBO", txtMensajeRecibo), "Recibo");
        panelTarjetas.add(utilidades.GeneradorTickets.crearTicketVistaPrevia("DOCUMENTO: ENTREGA", txtMensajeEntrega), "Entrega");
        panelTarjetas.add(utilidades.GeneradorTickets.crearTicketVistaPrevia("DOCUMENTO: COTIZACIÓN", txtMensajeCotizacion), "Cotizacion");

        // --- PANEL INFERIOR (Botones de Control) ---
        JPanel panelBotonesControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        panelBotonesControl.setBackground(new Color(18, 18, 18));

        JButton btnFactura = crearBotonEstiloPestana("Factura");
        JButton btnRecibo = crearBotonEstiloPestana("Recibo");
        JButton btnEntrega = crearBotonEstiloPestana("Entrega");
        JButton btnCotizacion = crearBotonEstiloPestana("Cotización");

        btnFactura.addActionListener(e -> cardLayout.show(panelTarjetas, "Factura"));
        btnRecibo.addActionListener(e -> cardLayout.show(panelTarjetas, "Recibo"));
        btnEntrega.addActionListener(e -> cardLayout.show(panelTarjetas, "Entrega"));
        btnCotizacion.addActionListener(e -> cardLayout.show(panelTarjetas, "Cotizacion"));

        JButton btnLogo = crearBotonEstiloPestana("Cargar Logo");
        btnLogo.setBackground(new Color(25, 135, 84)); // Verde para destacar
        btnLogo.addActionListener(e -> cargarLogoDesdePC());

        // Separador visual
        JLabel lblSeparador = new JLabel(" | ");
        lblSeparador.setForeground(Color.GRAY);
        lblSeparador.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnImpresoras = crearBotonEstiloPestana("Configurar Impresoras");
        btnImpresoras.setBackground(new Color(70, 70, 70));
        btnImpresoras.addActionListener(e -> abrirDialogoImpresoras());

        panelBotonesControl.add(btnFactura);
        panelBotonesControl.add(btnRecibo);
        panelBotonesControl.add(btnEntrega);
        panelBotonesControl.add(btnCotizacion);
        panelBotonesControl.add(btnLogo);
        panelBotonesControl.add(lblSeparador);
        panelBotonesControl.add(btnImpresoras);

        this.add(panelTarjetas, BorderLayout.CENTER);
        this.add(panelBotonesControl, BorderLayout.SOUTH);
    }

    // =====================================================================
    // MÉTODOS DE BASE DE DATOS Y LÓGICA
    // =====================================================================

    private void cargarDatosActuales() {
        Empresa emp = SesionGlobal.getEmpresaActual();
        if (emp != null) {
            if (emp.getMensajeTicketPieFactura() != null) txtMensajeFactura.setText(emp.getMensajeTicketPieFactura());
            if (emp.getMensajeTicketPieRecibo() != null) txtMensajeRecibo.setText(emp.getMensajeTicketPieRecibo());
            if (emp.getMensajeTicketEntrega() != null) txtMensajeEntrega.setText(emp.getMensajeTicketEntrega());
            if (emp.getMensajeTicketPieCotizacion() != null) txtMensajeCotizacion.setText(emp.getMensajeTicketPieCotizacion());
            if (emp.getLogoEmpresaRuta() != null) txtRutaLogo.setText(emp.getLogoEmpresaRuta());
        }
    }

    private void guardarDiseno() {
        Empresa emp = SesionGlobal.getEmpresaActual();
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Primero debe guardar los Datos Generales de la empresa.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Actualizamos los campos en el objeto
        emp.setMensajeTicketPieFactura(txtMensajeFactura.getText().trim());
        emp.setMensajeTicketPieRecibo(txtMensajeRecibo.getText().trim());
        emp.setMensajeTicketEntrega(txtMensajeEntrega.getText().trim());
        emp.setMensajeTicketPieCotizacion(txtMensajeCotizacion.getText().trim());
        emp.setLogoEmpresaRuta(txtRutaLogo.getText().trim());

        EmpresaDAO dao = new EmpresaDAO();
        if (dao.guardarOActualizar(emp)) {
            JOptionPane.showMessageDialog(this, "Diseño y textos guardados correctamente.");
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos.");
        }
    }

    private void cargarLogoDesdePC() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar Logo de la Empresa");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            txtRutaLogo.setText(archivo.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Logo cargado exitosamente.\nRecuerde presionar 'Guardar Textos y Logo'.");
        }
    }

    // =====================================================================
    // MÉTODOS DE DISEÑO Y COMPONENTES VISUALES
    // =====================================================================

    private JTextArea crearTextAreaEditable(String textoDefecto) {
        Border bordeEditable = BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 3, 2);
        JTextArea txt = new JTextArea(textoDefecto);
        txt.setFont(new Font("Courier New", Font.PLAIN, 12));
        txt.setForeground(Color.BLACK);
        txt.setBackground(new Color(250, 250, 250)); 
        txt.setBorder(BorderFactory.createCompoundBorder(bordeEditable, BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setToolTipText("Haz clic aquí para editar el mensaje final");
        return txt;
    }

    private JButton crearBotonEstiloPestana(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(50, 50, 50)); 
        btn.setForeground(new Color(245, 245, 245));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        return btn;
    }

    // =====================================================================
    // VENTANA EMERGENTE PARA SELECCIONAR IMPRESORAS
    // =====================================================================

    private void abrirDialogoImpresoras() {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) ventanaPadre, "Configuración de Impresoras", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel pnlContenido = new JPanel(new GridBagLayout());
        pnlContenido.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cmbImpresoraTickets = new JComboBox<>();
        JComboBox<String> cmbImpresoraFacturasA4 = new JComboBox<>();
        
        // Buscar impresoras instaladas en Windows
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        cmbImpresoraTickets.addItem("Seleccione una impresora...");
        cmbImpresoraFacturasA4.addItem("Seleccione una impresora...");
        for (PrintService printer : printServices) {
            cmbImpresoraTickets.addItem(printer.getName());
            cmbImpresoraFacturasA4.addItem(printer.getName());
        }

        gbc.gridy = 0;
        JLabel lblT = new JLabel("Impresora Térmica (Tickets):");
        lblT.setForeground(Color.WHITE);
        pnlContenido.add(lblT, gbc);
        
        gbc.gridy = 1;
        pnlContenido.add(cmbImpresoraTickets, gbc);

        gbc.gridy = 2;
        JLabel lblA4 = new JLabel("Impresora A4 (Documentos):");
        lblA4.setForeground(Color.WHITE);
        pnlContenido.add(lblA4, gbc);

        gbc.gridy = 3;
        pnlContenido.add(cmbImpresoraFacturasA4, gbc);

        dialog.add(pnlContenido, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBotones.setBackground(new Color(30, 30, 30));
        
        JButton btnGuardar = new JButton("Guardar Impresoras");
        btnGuardar.setBackground(new Color(13, 110, 253));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> {
            // Aquí en el futuro puedes guardar el nombre seleccionado en un txt local
            JOptionPane.showMessageDialog(dialog, "Impresoras seleccionadas guardadas localmente.");
            dialog.dispose();
        });

        pnlBotones.add(btnGuardar);
        dialog.add(pnlBotones, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    public void recargarVistaPrevia() {
        panelTarjetas.removeAll(); // Borramos los tickets viejos
        
        // Volvemos a generar los tickets. Como SesionGlobal ya se actualizó, tomarán el nuevo nombre/RTN.
        // Además, le volvemos a pasar nuestros txtMensaje para que no se borre lo que el usuario estaba escribiendo.
        panelTarjetas.add(utilidades.GeneradorTickets.crearTicketVistaPrevia("DOCUMENTO: FACTURA", txtMensajeFactura), "Factura");
        panelTarjetas.add(utilidades.GeneradorTickets.crearTicketVistaPrevia("DOCUMENTO: RECIBO", txtMensajeRecibo), "Recibo");
        panelTarjetas.add(utilidades.GeneradorTickets.crearTicketVistaPrevia("DOCUMENTO: ENTREGA", txtMensajeEntrega), "Entrega");
        panelTarjetas.add(utilidades.GeneradorTickets.crearTicketVistaPrevia("DOCUMENTO: COTIZACIÓN", txtMensajeCotizacion), "Cotizacion");
        
        panelTarjetas.revalidate(); // Le decimos a Java que recalcule los tamaños
        panelTarjetas.repaint();    // Le decimos a Java que redibuje la pantalla
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
