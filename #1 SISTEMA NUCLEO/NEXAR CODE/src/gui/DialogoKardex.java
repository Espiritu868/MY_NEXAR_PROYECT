package gui;

import dao.KardexDAO;
import modelo.Producto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DialogoKardex extends JDialog {

    // Aquí están las variables que te daban error
    private Producto productoActual;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JLabel lblStockDinamico;

    public DialogoKardex(Window parent, Producto producto) {
        super(parent, "Kardex del Producto", ModalityType.APPLICATION_MODAL);
        this.productoActual = producto;
        iniciarDiseno();
        cargarHistorial();
    }

    private void iniciarDiseno() {
        this.setSize(750, 500);
        this.setLocationRelativeTo(getOwner());
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(new Color(18, 18, 18));

        // --- PANEL SUPERIOR ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(new Color(25, 25, 25));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel pnlInfo = new JPanel(new GridLayout(2, 1));
        pnlInfo.setOpaque(false);
        JLabel lblNombre = new JLabel("Producto: " + productoActual.getNombreProducto());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNombre.setForeground(Color.WHITE);
        
        lblStockDinamico = new JLabel("Stock Actual: " + productoActual.getStockProducto() + " unidades");
        lblStockDinamico.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblStockDinamico.setForeground(new Color(150, 150, 150));

        pnlInfo.add(lblNombre);
        pnlInfo.add(lblStockDinamico);
        
        JButton btnNuevoMov = new JButton("+ Nuevo Movimiento");
        btnNuevoMov.setBackground(new Color(13, 110, 253));
        btnNuevoMov.setForeground(Color.WHITE);
        btnNuevoMov.setFocusPainted(false);
        btnNuevoMov.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNuevoMov.addActionListener(e -> abrirFormularioMovimiento());

        pnlTop.add(pnlInfo, BorderLayout.CENTER);
        pnlTop.add(btnNuevoMov, BorderLayout.EAST);
        this.add(pnlTop, BorderLayout.NORTH);

        // --- TABLA DE HISTORIAL ---
        String[] columnas = {"Fecha", "Tipo", "Cantidad", "Observación", "Usuario/Firma"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setBackground(new Color(30, 30, 30));
        tablaHistorial.setForeground(Color.WHITE);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaHistorial.setRowHeight(35);
        tablaHistorial.getTableHeader().setBackground(new Color(22, 22, 22));
        tablaHistorial.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.getViewport().setBackground(new Color(18, 18, 18));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        this.add(scroll, BorderLayout.CENTER);
    }

    // --- MÉTODOS QUE FALTABAN ---
    public void cargarHistorial() {
        modeloTabla.setRowCount(0);
        KardexDAO dao = new KardexDAO();
        List<Object[]> historial = dao.obtenerHistorialKardex(productoActual.getIdProducto());
        for (Object[] fila : historial) {
            modeloTabla.addRow(fila);
        }
    }
    
    public void actualizarStockVisual(int nuevoStock) {
        productoActual.setStockProducto(nuevoStock);
        lblStockDinamico.setText("Stock Actual: " + nuevoStock + " unidades");
    }

    private void abrirFormularioMovimiento() {
        DialogoMovimientoKardex dialog = new DialogoMovimientoKardex(this, productoActual);
        dialog.setVisible(true);
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
