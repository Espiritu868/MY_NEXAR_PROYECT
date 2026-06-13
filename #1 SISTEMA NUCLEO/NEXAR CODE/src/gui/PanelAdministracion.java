package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelAdministracion extends JPanel {

    private JPanel panelSubMenu;
    private JButton btnDatosEmpresa;
    private JButton btnUsuarios;
    
    // Contenedor dinámico donde se mostrarán los sub-paneles
    private JPanel panelContenedorAdmon;

    public PanelAdministracion() {
        initComponents(); // Deja que NetBeans cargue su panel vacío
        iniciarDiseno();  // Ejecuta nuestro código manual para sobreescribirlo
    }

    /**
     * Este es NUESTRO método para armar la pantalla, esquivando el bloqueo de NetBeans.
     */
    private void iniciarDiseno() {
        // 1. Limpiamos cualquier cosa que NetBeans haya puesto y cambiamos el Layout
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(18, 18, 18));

        // 2. Crear el Sub-Menú superior
        panelSubMenu = new JPanel();
        panelSubMenu.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10)); 
        panelSubMenu.setBackground(new Color(30, 30, 30)); 
        panelSubMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70))); 

        // 3. Crear y estilizar los botones del sub-menú
        btnDatosEmpresa = crearBotonSubMenu("Datos de Empresa");
        btnUsuarios = crearBotonSubMenu("Gestión de Usuarios"); 

        panelSubMenu.add(btnDatosEmpresa);
        panelSubMenu.add(btnUsuarios);

        // 4. Crear el contenedor central (Aquí cargará PanelDatosEmpresa)
        panelContenedorAdmon = new JPanel();
        panelContenedorAdmon.setLayout(new BorderLayout());
        panelContenedorAdmon.setBackground(new Color(18, 18, 18));
        
        JLabel lblAdmon = new JLabel("Gracias por confiar en NEXAR. \nPor favor, seleccione una opción del menú superior", SwingConstants.CENTER);
        lblAdmon.setForeground(new Color(100, 100, 100));
        lblAdmon.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        panelContenedorAdmon.add(lblAdmon, BorderLayout.CENTER);

        // 5. Agregar paneles a este módulo
        this.add(panelSubMenu, BorderLayout.NORTH); 
        this.add(panelContenedorAdmon, BorderLayout.CENTER); 

        // 6. Configurar los Eventos
        btnDatosEmpresa.addActionListener(e -> {
            mostrarSubPanel(new PanelDatosEmpresa());
        });

        // --- AGREGAR ESTO ---
        btnUsuarios.addActionListener(e -> {
            mostrarSubPanel(new PanelGestionUsuarios());
        });
    }

    /**
     * Método interno para cambiar el panel central de la administración
     */
    private void mostrarSubPanel(JPanel nuevoPanel) {
        panelContenedorAdmon.removeAll();
        panelContenedorAdmon.add(nuevoPanel, BorderLayout.CENTER);
        panelContenedorAdmon.revalidate();
        panelContenedorAdmon.repaint();
    }

    /**
     * Diseño elegante para los botones del sub-menú
     */
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
