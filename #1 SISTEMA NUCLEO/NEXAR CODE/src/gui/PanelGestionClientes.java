package gui;

import modelo.Cliente;
import dao.ClienteDAO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class PanelGestionClientes extends JPanel {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevoCliente;
    private JTextField txtBusqueda;
    private TableRowSorter<DefaultTableModel> sorter; // Para el buscador

    public PanelGestionClientes() {
        iniciarDiseno();
    }

    private void iniciarDiseno() {
        this.removeAll();
        this.setLayout(new BorderLayout(20, 20));
        this.setBackground(new Color(18, 18, 18)); 
        this.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- 1. PANEL SUPERIOR (Título, Búsqueda y Botón Nuevo) ---
        JPanel panelSuperior = new JPanel(new BorderLayout(20, 0)); // 20px de separación horizontal
        panelSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("Directorio de Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);

        // El buscador lo metemos en un panel central para que no se estire a lo loco
        JPanel panelCentro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelCentro.setOpaque(false);
        txtBusqueda = new JTextField(20);
        txtBusqueda.putClientProperty("JTextField.placeholderText", "Buscar Cliente...");
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBusqueda.setPreferredSize(new Dimension(250, 35));
        panelCentro.add(txtBusqueda);

        btnNuevoCliente = new JButton("+ Nuevo Cliente");
        btnNuevoCliente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNuevoCliente.setBackground(new Color(13, 110, 253));
        btnNuevoCliente.setForeground(Color.WHITE);
        btnNuevoCliente.setFocusPainted(false);
        btnNuevoCliente.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnNuevoCliente.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevoCliente.putClientProperty("JButton.buttonType", "roundRect");
        
        btnNuevoCliente.addActionListener(e -> abrirFormularioCliente(null));

        // Asignamos cada cosa a su esquina para que nunca choquen
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(panelCentro, BorderLayout.CENTER);
        panelSuperior.add(btnNuevoCliente, BorderLayout.EAST);

        this.add(panelSuperior, BorderLayout.NORTH);
        // --- 2. CONFIGURACIÓN DE LA TABLA ESTILO WEB ---
        // Columnas 7 y 8 estarán ocultas, guardan el nombre real y el apellido real
        String[] columnas = {"ID", "", "Nombre Completo", "Identidad/RTN", "Teléfono", "Correo Electrónico", "Acciones", "NombreRaw", "ApellidoRaw"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo Acciones
            }
        };

        tablaClientes = new JTable(modeloTabla);
        
        tablaClientes.setShowGrid(false);
        tablaClientes.setIntercellSpacing(new Dimension(0, 0));
        tablaClientes.setRowHeight(55);
        tablaClientes.setBackground(new Color(30, 30, 30)); 
        tablaClientes.setForeground(Color.WHITE);
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaClientes.setSelectionBackground(new Color(50, 50, 50)); 
        tablaClientes.setSelectionForeground(Color.WHITE);

        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaClientes.getTableHeader().setBackground(new Color(22, 22, 22));
        tablaClientes.getTableHeader().setForeground(new Color(180, 180, 180));
        tablaClientes.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70)));
        tablaClientes.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Ocultar ID (0), NombreRaw (7) y ApellidoRaw (8)
        ocultarColumna(0);
        ocultarColumna(7);
        ocultarColumna(8);

        // Renderizadores
        tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(60);
        tablaClientes.getColumnModel().getColumn(1).setMaxWidth(60);
        tablaClientes.getColumnModel().getColumn(1).setCellRenderer(new AvatarRenderer());

        tablaClientes.getColumnModel().getColumn(6).setPreferredWidth(120);
        tablaClientes.getColumnModel().getColumn(6).setMaxWidth(120);
        tablaClientes.getColumnModel().getColumn(6).setCellRenderer(new PanelAccionesRenderer());
        tablaClientes.getColumnModel().getColumn(6).setCellEditor(new PanelAccionesEditor());

        // LÓGICA DEL BUSCADOR EN TIEMPO REAL
        sorter = new TableRowSorter<>(modeloTabla);
        tablaClientes.setRowSorter(sorter);
        txtBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
            private void filtrar() {
                String texto = txtBusqueda.getText();
                if (texto.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    // Filtra por Nombre Completo (2) y por Identidad (3)
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 2, 3));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1, true));
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));

        this.add(scrollPane, BorderLayout.CENTER);

        cargarDatosDesdeBD();
    }

    private void ocultarColumna(int index) {
        tablaClientes.getColumnModel().getColumn(index).setMinWidth(0);
        tablaClientes.getColumnModel().getColumn(index).setMaxWidth(0);
        tablaClientes.getColumnModel().getColumn(index).setWidth(0);
    }

    public void cargarDatosDesdeBD() {
        modeloTabla.setRowCount(0);
        ClienteDAO dao = new ClienteDAO();
        for (Cliente c : dao.listarClientesActivos()) {
            String apellido = c.getApellidoCliente() != null ? c.getApellidoCliente() : "";
            String nombreCompleto = c.getNombreCliente() + " " + apellido;
            
            modeloTabla.addRow(new Object[]{
                c.getIdCliente(),           // 0: ID
                nombreCompleto,             // 1: Avatar
                nombreCompleto,             // 2: Nombre Completo
                c.getIdentidadCliente(),    // 3: Identidad
                c.getTelefonoCliente(),     // 4: Teléfono
                c.getCorreoCliente(),       // 5: Correo
                "",                         // 6: Botones
                c.getNombreCliente(),       // 7: Nombre Real Oculto
                apellido                    // 8: Apellido Real Oculto
            });
        }
    }

    private void abrirFormularioCliente(Cliente cliente) {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) ventanaPadre, "Cliente", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0)); 
        
        PanelFormularioCliente panelFormulario = new PanelFormularioCliente(dialog, this, cliente);
        
        dialog.add(panelFormulario);
        dialog.pack();
        dialog.setLocationRelativeTo(ventanaPadre);
        dialog.setVisible(true);
    }

    // =========================================================================
    // CLASES INTERNAS (Renderizadores y Botones)
    // =========================================================================

    private class AvatarRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    String nombre = value != null ? value.toString() : "?";
                    String inicial = nombre.isEmpty() ? "?" : nombre.substring(0, 1).toUpperCase();

                    int hash = Math.abs(inicial.hashCode());
                    Color[] paleta = {new Color(220, 53, 69), new Color(13, 110, 253), new Color(25, 135, 84), new Color(253, 126, 20), new Color(111, 66, 193)};
                    g2.setColor(paleta[hash % paleta.length]);
                    
                    int size = 36;
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;
                    g2.fill(new Ellipse2D.Double(x, y, size, size));

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = x + (size - fm.stringWidth(inicial)) / 2;
                    int textY = y + ((size - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(inicial, textX, textY);
                }
            };
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
    }

    private class IconoEditar implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(13, 110, 253)); 
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(x + 12, y + 2, x + 16, y + 6); 
            g2.drawLine(x + 16, y + 6, x + 6, y + 16); 
            g2.drawLine(x + 12, y + 2, x + 2, y + 12); 
            g2.drawLine(x + 2, y + 12, x + 2, y + 16); 
            g2.drawLine(x + 2, y + 16, x + 6, y + 16); 
        }
        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }
    }

    private class IconoEliminar implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 53, 69)); 
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(x + 2, y + 4, x + 16, y + 4); 
            g2.drawLine(x + 7, y + 2, x + 11, y + 2); 
            g2.drawRect(x + 4, y + 4, 10, 12); 
            g2.drawLine(x + 7, y + 7, x + 7, y + 13); 
            g2.drawLine(x + 11, y + 7, x + 11, y + 13); 
        }
        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }
    }

    private class PanelAcciones extends JPanel {
        private JButton btnEditar;
        private JButton btnEliminar;

        public PanelAcciones() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
            setOpaque(true);

            btnEditar = new JButton(new IconoEditar());
            btnEditar.setContentAreaFilled(false);
            btnEditar.setBorder(null);
            btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEditar.setToolTipText("Editar Cliente");

            btnEliminar = new JButton(new IconoEliminar());
            btnEliminar.setContentAreaFilled(false);
            btnEliminar.setBorder(null);
            btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEliminar.setToolTipText("Eliminar Cliente");

            add(btnEditar);
            add(btnEliminar);
        }
        public JButton getBtnEditar() { return btnEditar; }
        public JButton getBtnEliminar() { return btnEliminar; }
    }

    private class PanelAccionesRenderer implements TableCellRenderer {
        private PanelAcciones panel = new PanelAcciones();
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
    }

    private class PanelAccionesEditor extends AbstractCellEditor implements TableCellEditor {
        private PanelAcciones panel = new PanelAcciones();
        private int filaVista;

        public PanelAccionesEditor() {
            panel.getBtnEditar().addActionListener(e -> {
                fireEditingStopped();
                int filaModelo = tablaClientes.convertRowIndexToModel(filaVista);
                
                Cliente c = new Cliente();
                c.setIdCliente((int) modeloTabla.getValueAt(filaModelo, 0));
                c.setIdentidadCliente(modeloTabla.getValueAt(filaModelo, 3).toString());
                c.setTelefonoCliente(modeloTabla.getValueAt(filaModelo, 4) != null ? modeloTabla.getValueAt(filaModelo, 4).toString() : "");
                c.setCorreoCliente(modeloTabla.getValueAt(filaModelo, 5) != null ? modeloTabla.getValueAt(filaModelo, 5).toString() : "");
                c.setNombreCliente(modeloTabla.getValueAt(filaModelo, 7).toString());
                c.setApellidoCliente(modeloTabla.getValueAt(filaModelo, 8).toString());
                
                abrirFormularioCliente(c);
            });

            panel.getBtnEliminar().addActionListener(e -> {
                fireEditingStopped();
                int filaModelo = tablaClientes.convertRowIndexToModel(filaVista);
                int idCliente = (int) modeloTabla.getValueAt(filaModelo, 0);
                String nombre = modeloTabla.getValueAt(filaModelo, 2).toString();
                
                int confirmacion = JOptionPane.showConfirmDialog(panel, 
                    "¿Está seguro de desactivar al cliente: " + nombre + "?", 
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                    
                if (confirmacion == JOptionPane.YES_OPTION) {
                    ClienteDAO dao = new ClienteDAO();
                    if (dao.desactivarCliente(idCliente)) {
                        JOptionPane.showMessageDialog(panel, "Cliente eliminado.");
                        cargarDatosDesdeBD();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            filaVista = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        @Override
        public Object getCellEditorValue() { return null; }
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
