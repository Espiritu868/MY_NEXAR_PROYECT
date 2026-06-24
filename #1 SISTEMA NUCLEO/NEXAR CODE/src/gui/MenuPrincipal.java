package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    private JPanel panelCentral;
    private JPanel panelLateralIzquierdo;
    private JButton btnAdministracion;
    private JButton btnInventario;
    private JButton btnClientes;
    private JButton btnPuntoVenta;
    private JButton btnGarantias;
    private JButton btnCerrarSesion;
    private JButton btnNuevoRol;

    private final Color COLOR_FONDO_NEGRO = new Color(18, 18, 18);
    private final Color COLOR_BLANCO = new Color(245, 245, 245);
    private final Color COLOR_GRIS = new Color(70, 70, 70);
    private final Color COLOR_ROJO = new Color(220, 53, 69);

    public MenuPrincipal() {
        // 1. Configuración de la ventana principal
        setTitle("ORION SYSTEMS - CONEXION A TU ALCANCE");
        try {
            java.net.URL imgURL = getClass().getResource("/image/logo.png");
            if (imgURL != null) {
                setIconImage(new javax.swing.ImageIcon(imgURL).getImage());
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO_NEGRO);

        // 2. Crear y configurar el Panel Lateral Izquierdo
        panelLateralIzquierdo = new JPanel();
        panelLateralIzquierdo.setLayout(new BoxLayout(panelLateralIzquierdo, BoxLayout.Y_AXIS)); 
        panelLateralIzquierdo.setPreferredSize(new Dimension(240, 0));
        panelLateralIzquierdo.setBackground(COLOR_FONDO_NEGRO);
        
        // Borde blanco en la parte derecha del menú lateral
        panelLateralIzquierdo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BLANCO), 
            BorderFactory.createEmptyBorder(40, 20, 40, 20) 
        ));

        // Título del menú en gris
        JLabel lblMenu = new JLabel("MENÚ PRINCIPAL");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMenu.setForeground(COLOR_GRIS);
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 3. Instanciar y estilizar los botones
        btnAdministracion = crearBotonUI("Administración", COLOR_BLANCO, COLOR_FONDO_NEGRO);
        btnClientes = crearBotonUI("Clientes", COLOR_BLANCO, COLOR_FONDO_NEGRO);
        btnInventario = crearBotonUI("Inventario", COLOR_BLANCO, COLOR_FONDO_NEGRO);
        btnPuntoVenta = crearBotonUI("Punto de Venta", COLOR_BLANCO, COLOR_FONDO_NEGRO);
        btnGarantias = crearBotonUI("Garantías", COLOR_BLANCO, COLOR_FONDO_NEGRO); // <-- NUEVO
        btnCerrarSesion = crearBotonUI("Cerrar Sesión", COLOR_BLANCO, COLOR_ROJO);

       // 4. Agregar componentes al panel lateral
        panelLateralIzquierdo.add(lblMenu);
        panelLateralIzquierdo.add(Box.createVerticalStrut(30)); 
        panelLateralIzquierdo.add(btnAdministracion);
        panelLateralIzquierdo.add(Box.createVerticalStrut(15)); 
        panelLateralIzquierdo.add(btnClientes);
        panelLateralIzquierdo.add(Box.createVerticalStrut(15)); 
        panelLateralIzquierdo.add(btnInventario);     
        panelLateralIzquierdo.add(Box.createVerticalStrut(15)); 
        panelLateralIzquierdo.add(btnPuntoVenta);
        panelLateralIzquierdo.add(Box.createVerticalStrut(15)); 
        panelLateralIzquierdo.add(btnGarantias); // <-- BOTÓN AGREGADO AL MENÚ
        // --------------------------------
        
        panelLateralIzquierdo.add(Box.createVerticalGlue());     
        panelLateralIzquierdo.add(btnCerrarSesion);
        
        // 5. Crear y configurar el Panel Central
        panelCentral = new JPanel();
        panelCentral.setLayout(new BorderLayout());
        panelCentral.setBackground(COLOR_FONDO_NEGRO); 
        
        JLabel lblBienvenida = new JLabel("Bienvenido a Orion Systems", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblBienvenida.setForeground(COLOR_BLANCO);
        panelCentral.add(lblBienvenida, BorderLayout.CENTER);

        // 6. Agregar los dos paneles a la ventana principal
        add(panelLateralIzquierdo, BorderLayout.WEST); 
        add(panelCentral, BorderLayout.CENTER);

        // 7. Configuración de Eventos (ActionListeners)
        btnAdministracion.addActionListener(e -> {
            // Al presionar el botón, carga el sub-módulo de administración
            mostrarPanelHijo(new PanelAdministracion());
        });
        
        btnClientes.addActionListener(e -> {
            mostrarPanelHijo(new PanelGestionClientes());
        });
        
        btnInventario.addActionListener(e -> {
            mostrarPanelHijo(new PanelInventario());
        });
       
        btnPuntoVenta.addActionListener(e -> {
            mostrarPanelHijo(new PanelPuntoVenta());
        });
        
        btnGarantias.addActionListener(e -> {
            mostrarPanelHijo(new PanelGestionGarantias());
        });
        
        btnCerrarSesion.addActionListener(e -> {
            System.exit(0);
        });
        
    }

    /**
     * Reemplaza el contenido del centro con el nuevo panel seleccionado.
     */
    public void mostrarPanelHijo(JPanel nuevoPanel) {
        panelCentral.removeAll(); 
        nuevoPanel.setBackground(COLOR_FONDO_NEGRO); 
        panelCentral.add(nuevoPanel, BorderLayout.CENTER); 
        panelCentral.revalidate(); 
        panelCentral.repaint(); 
    }

    /**
     * Diseño de botones minimalistas.
     */
    private JButton crearBotonUI(String texto, Color bg, Color fg) {
        JButton boton = new JButton(texto);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        boton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        boton.setBackground(bg);
        boton.setForeground(fg);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        boton.putClientProperty("JButton.buttonType", "roundRect");
        return boton;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Error al inicializar FlatLaf: " + ex.getMessage());
        }
        
        try {
            dao.EmpresaDAO EmpresaDAO = new dao.EmpresaDAO();
            modelo.Empresa datosEmp = EmpresaDAO.obtenerDatos();
            
            if (datosEmp != null) {
                utilidades.SesionGlobal.setEmpresaActual(datosEmp);
                System.out.println("Datos de la empresa cargados exitosamente en RAM.");
            } else {
                System.out.println("No se encontraron datos de la empresa en la BD.");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la sesión global: " + e.getMessage());
        }
        // --------------------------------------------------

        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }
}