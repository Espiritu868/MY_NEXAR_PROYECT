package gui;

import modelo.Empresa;
import dao.EmpresaDAO;
import utilidades.SesionGlobal;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

public class PanelDatosEmpresa extends JPanel {

    // --- Componentes - Datos Generales ---
    private JTextField txtNombreEmpresa;
    private JTextField txtRtnEmpresa;
    private JTextField txtDuenoEmpresa;
    private JTextArea txtDireccionEmpresa;
    private JCheckBox chkEstadoEmpresa;
    private JCheckBox chkHabilitarFacturacion;

    // --- Componentes - Contacto y Redes ---
    private JTextField txtNumeroTelefono;
    private JTextField txtTelefonoSecundario;
    private JTextField txtWhatsapp;
    private JTextField txtEmail;
    private JTextField txtWeb;
    private JTextField txtFacebook;
    
    // --- Componentes para Impresion
    private PanelConfiguracionImpresion panelImpresion;

    // --- Botón de Acción ---
    private JButton btnGuardar;

    public PanelDatosEmpresa() {
        initComponents(); 
        iniciarDiseno();  
    }

    private void iniciarDiseno() {
        this.removeAll(); 
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(18, 18, 18)); 

        JLabel lblTitulo = new JLabel("Configuración de la Empresa");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(245, 245, 245));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.add(lblTitulo, BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        pestanas.addTab("Datos Generales", crearPanelGenerales());
        pestanas.addTab("Contacto y Redes", crearPanelContacto());
        panelImpresion = new PanelConfiguracionImpresion();
        pestanas.addTab("Impresión", panelImpresion); 

        this.add(pestanas, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBackground(new Color(18, 18, 18));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setBackground(new Color(220, 53, 69)); 
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(180, 40));
        
        btnGuardar.addActionListener(e -> guardarDatos());

        panelInferior.add(btnGuardar);
        this.add(panelInferior, BorderLayout.SOUTH);

        cargarDatosGuardados();
    }
    
    private void cargarDatosGuardados() {
        EmpresaDAO dao = new EmpresaDAO();
        Empresa emp = dao.obtenerDatos();

        if (emp != null) {
            txtNombreEmpresa.setText(emp.getNombreEmpresa());
            txtRtnEmpresa.setText(emp.getRtnEmpresa());
            txtDuenoEmpresa.setText(emp.getDuenoEmpresa());
            txtDireccionEmpresa.setText(emp.getDireccionEmpresa());
            chkEstadoEmpresa.setSelected(emp.isEstadoEmpresa());
            chkHabilitarFacturacion.setSelected(emp.isHabilitarFacturacion());
            
            txtNumeroTelefono.setText(emp.getNumeroTelefono());
            txtTelefonoSecundario.setText(emp.getTelefonoSecundario());
            txtWhatsapp.setText(emp.getWhatsapp());
            txtEmail.setText(emp.getEmail());
            txtWeb.setText(emp.getWeb());
            txtFacebook.setText(emp.getFacebook());
            
            // Ya no intentamos cargar los mensajes de impresión aquí
            
            btnGuardar.setText("Actualizar Datos");
            btnGuardar.setBackground(new Color(13, 110, 253)); 
        } else {
            btnGuardar.setText("Guardar Cambios");
            btnGuardar.setBackground(new Color(220, 53, 69)); 
        }
    }

    private void guardarDatos() {
        if(txtNombreEmpresa.getText().trim().isEmpty() || txtRtnEmpresa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El Nombre de la Empresa y el RTN son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Empresa emp = new Empresa();
        emp.setNombreEmpresa(txtNombreEmpresa.getText().trim());
        emp.setRtnEmpresa(txtRtnEmpresa.getText().trim());
        emp.setDuenoEmpresa(txtDuenoEmpresa.getText().trim());
        emp.setDireccionEmpresa(txtDireccionEmpresa.getText().trim());
        emp.setEstadoEmpresa(chkEstadoEmpresa.isSelected());
        emp.setHabilitarFacturacion(chkHabilitarFacturacion.isSelected());
        
        emp.setNumeroTelefono(txtNumeroTelefono.getText().trim());
        emp.setTelefonoSecundario(txtTelefonoSecundario.getText().trim());
        emp.setWhatsapp(txtWhatsapp.getText().trim());
        emp.setEmail(txtEmail.getText().trim());
        emp.setWeb(txtWeb.getText().trim());
        emp.setFacebook(txtFacebook.getText().trim());
        
        // Ya no intentamos leer los mensajes de impresión aquí para guardarlos
        
        EmpresaDAO dao = new EmpresaDAO();
        if (dao.guardarOActualizar(emp)) {
            JOptionPane.showMessageDialog(this, "Datos guardados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            SesionGlobal.setEmpresaActual(emp);
            
            panelImpresion.recargarVistaPrevia();
            
            btnGuardar.setText("Actualizar Datos");
            btnGuardar.setBackground(new Color(13, 110, 253)); 
        }
    }

    // =========================================================
    // CREACIÓN DE PANELES
    // =========================================================

    private JPanel crearPanelGenerales() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        txtNombreEmpresa = new JTextField(35); 
        txtRtnEmpresa = new JTextField(15);    
        txtDuenoEmpresa = new JTextField(35);  
        txtDireccionEmpresa = new JTextArea(4, 35);
        txtDireccionEmpresa.setLineWrap(true);
        txtDireccionEmpresa.setWrapStyleWord(true); 
        
        chkEstadoEmpresa = new JCheckBox("Empresa Activa");
        chkEstadoEmpresa.setBackground(new Color(30, 30, 30));
        chkEstadoEmpresa.setForeground(Color.WHITE);
        
        chkHabilitarFacturacion = new JCheckBox("Habilitar Facturación (SAR)");
        chkHabilitarFacturacion.setBackground(new Color(30, 30, 30));
        chkHabilitarFacturacion.setForeground(Color.WHITE);

        agregarFila(panel, gbc, 0, "Nombre Empresa:", txtNombreEmpresa);
        agregarFila(panel, gbc, 1, "RTN:", txtRtnEmpresa);
        agregarFila(panel, gbc, 2, "Propietario:", txtDuenoEmpresa);
        
        gbc.gridy = 3; gbc.gridx = 0; gbc.anchor = GridBagConstraints.NORTHEAST; 
        JLabel lblDir = new JLabel("Dirección:");
        lblDir.setForeground(Color.WHITE);
        lblDir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lblDir, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JScrollPane(txtDireccionEmpresa), gbc);

        gbc.gridx = 1; gbc.gridy = 4; panel.add(chkEstadoEmpresa, gbc);
        gbc.gridy = 5; panel.add(chkHabilitarFacturacion, gbc);
        gbc.gridy = 6; gbc.weighty = 1.0; panel.add(new JLabel(""), gbc);

        return panel;
    }

    private JPanel crearPanelContacto() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        txtNumeroTelefono = new JTextField(15);
        txtTelefonoSecundario = new JTextField(15);
        txtWhatsapp = new JTextField(15);
        txtEmail = new JTextField(30);
        txtWeb = new JTextField(30);
        txtFacebook = new JTextField(30);

        agregarFila(panel, gbc, 0, "Teléfono Principal:", txtNumeroTelefono);
        agregarFila(panel, gbc, 1, "Teléfono Secundario:", txtTelefonoSecundario);
        agregarFila(panel, gbc, 2, "WhatsApp:", txtWhatsapp);
        agregarFila(panel, gbc, 3, "Correo:", txtEmail);
        agregarFila(panel, gbc, 4, "Web:", txtWeb);
        agregarFila(panel, gbc, 5, "Facebook:", txtFacebook);

        gbc.gridy = 6; gbc.weighty = 1.0; gbc.gridx = 0;
        panel.add(new JLabel(""), gbc);

        return panel;
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST; 
        JLabel label = new JLabel(etiqueta);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(campo, gbc);
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
