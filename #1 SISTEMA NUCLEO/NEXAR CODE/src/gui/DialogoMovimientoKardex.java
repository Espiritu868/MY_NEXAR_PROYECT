package gui;

import dao.KardexDAO;
import modelo.Producto;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DialogoMovimientoKardex extends JDialog {

    private Producto productoActual;
    private DialogoKardex padreKardex; // Para actualizar la tabla de atrás
    
    private JRadioButton rbAgregar, rbDisminuir;
    private JTextField txtCantidad;
    private JTextArea txtObservacion;

    public DialogoMovimientoKardex(DialogoKardex parent, Producto producto) {
        super(parent, "Registrar Movimiento", ModalityType.APPLICATION_MODAL);
        this.padreKardex = parent;
        this.productoActual = producto;
        iniciarDiseno();
    }

    private void iniciarDiseno() {
        this.setSize(400, 480);
        this.setLocationRelativeTo(getOwner());
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(new Color(18, 18, 18));

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(new Color(30, 30, 30));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 5, 0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0; gbc.weightx = 1.0;

        // Tipo de Movimiento
        JLabel lblTipo = new JLabel("Tipo de Movimiento:"); lblTipo.setForeground(Color.WHITE);
        rbAgregar = new JRadioButton("Entrada (+)"); rbAgregar.setForeground(new Color(25, 135, 84)); rbAgregar.setBackground(new Color(30, 30, 30));
        rbDisminuir = new JRadioButton("Salida (-)"); rbDisminuir.setForeground(new Color(220, 53, 69)); rbDisminuir.setBackground(new Color(30, 30, 30));
        ButtonGroup bg = new ButtonGroup(); bg.add(rbAgregar); bg.add(rbDisminuir); rbAgregar.setSelected(true);
        JPanel pnlRadios = new JPanel(new FlowLayout(FlowLayout.LEFT)); pnlRadios.setOpaque(false); pnlRadios.add(rbAgregar); pnlRadios.add(rbDisminuir);
        
        gbc.gridy = 0; pnlForm.add(lblTipo, gbc);
        gbc.gridy = 1; pnlForm.add(pnlRadios, gbc);

        // Cantidad
        JLabel lblCantidad = new JLabel("Cantidad:"); lblCantidad.setForeground(Color.WHITE);
        txtCantidad = new JTextField(); txtCantidad.setBackground(new Color(40, 40, 40)); txtCantidad.setForeground(Color.WHITE); txtCantidad.setCaretColor(Color.WHITE);
        txtCantidad.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txtCantidad.addKeyListener(new KeyAdapter() { @Override public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); } });
        
        gbc.gridy = 2; pnlForm.add(lblCantidad, gbc);
        gbc.gridy = 3; pnlForm.add(txtCantidad, gbc);

        // Observación
        JLabel lblObs = new JLabel("Motivo u Observación:"); lblObs.setForeground(Color.WHITE);
        txtObservacion = new JTextArea(4, 20); 
        txtObservacion.setText("Ajuste manual del kardex"); // <--- AÑADIR ESTA LÍNEA AQUÍ
        txtObservacion.setBackground(new Color(40, 40, 40)); 
        txtObservacion.setForeground(Color.WHITE); 
        txtObservacion.setCaretColor(Color.WHITE);
        JScrollPane scrollObs = new JScrollPane(txtObservacion); scrollObs.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        
        gbc.gridy = 4; pnlForm.add(lblObs, gbc);
        gbc.gridy = 5; pnlForm.add(scrollObs, gbc);

        this.add(pnlForm, BorderLayout.CENTER);

        // Botones
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15)); pnlBotones.setBackground(new Color(18, 18, 18));
        JButton btnCancelar = new JButton("Cancelar"); btnCancelar.setBackground(new Color(70, 70, 70)); btnCancelar.setForeground(Color.WHITE); btnCancelar.addActionListener(e -> this.dispose());
        JButton btnGuardar = new JButton("Firmar Movimiento"); btnGuardar.setBackground(new Color(25, 135, 84)); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> firmarYGuardar());
        
        pnlBotones.add(btnCancelar); pnlBotones.add(btnGuardar);
        this.add(pnlBotones, BorderLayout.SOUTH);
    }

    private void firmarYGuardar() {
        if (txtCantidad.getText().trim().isEmpty() || txtObservacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar cantidad y observación.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
        }

        int cantidad = Integer.parseInt(txtCantidad.getText().trim());
        if (cantidad <= 0) { JOptionPane.showMessageDialog(this, "Cantidad inválida.", "Error", JOptionPane.ERROR_MESSAGE); return; }

        boolean esEntrada = rbAgregar.isSelected();
        if (!esEntrada && cantidad > productoActual.getStockProducto()) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente para esta salida.", "Error", JOptionPane.ERROR_MESSAGE); return;
        }

        // --- SOLICITAR FIRMA (SOLO CONTRASEÑA) ---
        JPasswordField pfPass = new JPasswordField();
        Object[] msg = { "Ingrese su contraseña para autorizar el movimiento:", pfPass };

        int opcion = JOptionPane.showConfirmDialog(this, msg, "Firma de Autorización", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (opcion == JOptionPane.OK_OPTION) {
            String pass = new String(pfPass.getPassword());

            KardexDAO dao = new KardexDAO();
            // Ya no le pasamos el usuario, solo la contraseña
            int idAutorizador = dao.validarFirmaUsuario(pass);

            if (idAutorizador > 0) {
                if (pass.equals(String.valueOf(idAutorizador))) {
                    JOptionPane.showMessageDialog(this, "Acceso Denegado: Por políticas de seguridad de Nexar, no se permite autorizar transacciones con contraseñas por defecto. Por favor, actualice su contraseña.", "Seguridad Nexar", JOptionPane.ERROR_MESSAGE);
                    return; 
                }

                if (dao.registrarMovimiento(productoActual.getIdProducto(), esEntrada ? "Entrada" : "Salida", cantidad, txtObservacion.getText().trim(), idAutorizador)) {
                    JOptionPane.showMessageDialog(this, "Movimiento registrado y firmado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
                    padreKardex.cargarHistorial();
                    int nuevoStock = esEntrada ? (productoActual.getStockProducto() + cantidad) : (productoActual.getStockProducto() - cantidad);
                    padreKardex.actualizarStockVisual(nuevoStock);
                    
                    this.dispose(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Error de base de datos al guardar movimiento.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta o usuario inactivo.", "Firma Rechazada", JOptionPane.ERROR_MESSAGE);
            }
        }
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
