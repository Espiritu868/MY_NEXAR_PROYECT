package gui;

import javax.swing.*;
import java.awt.*;

public class PanelInventario extends JPanel {

    private JPanel panelSubMenu;
    private JButton btnCrearProducto;
    private JButton btnBuscarProducto;
    private JPanel panelContenedorInventario;

    public PanelInventario() {
        iniciarDiseno();
    }

    private void iniciarDiseno() {
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(18, 18, 18));

        // 1. Crear el Sub-Menú superior
        panelSubMenu = new JPanel();
        panelSubMenu.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10)); 
        panelSubMenu.setBackground(new Color(30, 30, 30)); 
        panelSubMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70))); 

        // 2. Crear y estilizar los botones del sub-menú
        btnCrearProducto = crearBotonSubMenu("Crear Producto");
        btnBuscarProducto = crearBotonSubMenu("Buscar Producto"); 

        panelSubMenu.add(btnCrearProducto);
        panelSubMenu.add(btnBuscarProducto);

        // 3. Crear el contenedor central
        panelContenedorInventario = new JPanel();
        panelContenedorInventario.setLayout(new BorderLayout());
        panelContenedorInventario.setBackground(new Color(18, 18, 18));
        
        JLabel lblAdmon = new JLabel("Módulo de Inventario. Seleccione una opción del menú superior.", SwingConstants.CENTER);
        lblAdmon.setForeground(new Color(100, 100, 100));
        lblAdmon.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        panelContenedorInventario.add(lblAdmon, BorderLayout.CENTER);

        this.add(panelSubMenu, BorderLayout.NORTH); 
        this.add(panelContenedorInventario, BorderLayout.CENTER); 

        // 4. Configurar los Eventos
        btnCrearProducto.addActionListener(e -> {
            mostrarSubPanel(new PanelCrearProducto());
        });

        btnBuscarProducto.addActionListener(e -> {
            mostrarSubPanel(new PanelBuscarProducto());
        });
    }

    public void mostrarSubPanel(JPanel nuevoPanel) {
        panelContenedorInventario.removeAll();
        panelContenedorInventario.add(nuevoPanel, BorderLayout.CENTER);
        panelContenedorInventario.revalidate();
        panelContenedorInventario.repaint();
    }

    private JButton crearBotonSubMenu(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(new Color(245, 245, 245));
        boton.setForeground(new Color(18, 18, 18));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(180, 35));
        boton.putClientProperty("JButton.buttonType", "roundRect");
        return boton;
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
