package gui;

import dao.InventarioDAO;
import modelo.Producto;
import utilidades.SesionGlobal;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.util.List;

public class PanelBuscarProducto extends JPanel {

    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelBuscarProducto() {
        iniciarDiseno();
    }

    private void iniciarDiseno() {
        this.setLayout(new BorderLayout(20, 20));
        this.setBackground(new Color(18, 18, 18));
        this.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- PANEL SUPERIOR ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("Catálogo de Inventario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);

        txtBusqueda = new JTextField(20);
        txtBusqueda.putClientProperty("JTextField.placeholderText", "Buscar por nombre o código...");
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBusqueda.setPreferredSize(new Dimension(300, 35));

        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(txtBusqueda, BorderLayout.EAST);
        this.add(panelSuperior, BorderLayout.NORTH);

        // --- TABLA DE INVENTARIO ---
        String[] columnas = {"ID", "Foto", "Código", "Producto", "P. Compra", "P. Venta", "P. Técnico", "Stock", "Acciones"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 8; }
        };

        tablaInventario = new JTable(modeloTabla) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (!isRowSelected(row)) {
                    Object valorStock = getValueAt(row, 7);
                    int stock = 1; 
                    if (valorStock != null) {
                        try {
                            stock = Integer.parseInt(valorStock.toString());
                        } catch (NumberFormatException e) {}
                    }
                    
                    // Si el stock es 0, toda la fila se pinta de rojo
                    if (stock <= 0) {
                        c.setForeground(new Color(220, 53, 69)); 
                    } else {
                        c.setForeground(Color.WHITE); 
                    }
                    c.setBackground(new Color(30, 30, 30)); 
                }
                return c;
            }
        };
        // ------------------------------------------------------------------
        tablaInventario.setShowGrid(false);
        tablaInventario.setRowHeight(70); 
        tablaInventario.setBackground(new Color(30, 30, 30));
        tablaInventario.setForeground(Color.WHITE);
        tablaInventario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaInventario.setSelectionBackground(new Color(50, 50, 50));

        tablaInventario.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaInventario.getTableHeader().setBackground(new Color(22, 22, 22));
        tablaInventario.getTableHeader().setForeground(new Color(180, 180, 180));
        tablaInventario.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70)));
        tablaInventario.getTableHeader().setPreferredSize(new Dimension(0, 45));

        tablaInventario.getColumnModel().getColumn(0).setMinWidth(0);
        tablaInventario.getColumnModel().getColumn(0).setMaxWidth(0); 

        tablaInventario.getColumnModel().getColumn(1).setPreferredWidth(80); 
        tablaInventario.getColumnModel().getColumn(1).setMaxWidth(80);
        tablaInventario.getColumnModel().getColumn(1).setCellRenderer(new ImagenProductoRenderer());

        tablaInventario.getColumnModel().getColumn(8).setPreferredWidth(100); 
        tablaInventario.getColumnModel().getColumn(8).setMaxWidth(100);
        tablaInventario.getColumnModel().getColumn(8).setCellRenderer(new PanelAccionesRenderer());
        tablaInventario.getColumnModel().getColumn(8).setCellEditor(new PanelAccionesEditor());

        sorter = new TableRowSorter<>(modeloTabla);
        tablaInventario.setRowSorter(sorter);
        txtBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
            private void filtrar() {
                String texto = txtBusqueda.getText();
                if (texto.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 2, 3)); 
            }
        });

        // --- LÓGICA DE CURSORES Y CLICS EN TABLA ---
        tablaInventario.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int columna = tablaInventario.columnAtPoint(e.getPoint());
                // El cursor de mano solo aparece si pasas sobre la Foto (1) o las Acciones (8)
                if (columna == 1 || columna == 8) {
                    tablaInventario.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    tablaInventario.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        tablaInventario.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tablaInventario.rowAtPoint(e.getPoint());
                int columna = tablaInventario.columnAtPoint(e.getPoint());
                if (fila >= 0 && columna == 1) { // 1 es la columna Foto
                    int filaModelo = tablaInventario.convertRowIndexToModel(fila);
                    String rutaImagen = (String) modeloTabla.getValueAt(filaModelo, 1);
                    
                    if (rutaImagen == null || rutaImagen.isEmpty() || !new File(rutaImagen).exists()) {
                        if (SesionGlobal.getEmpresaActual() != null && SesionGlobal.getEmpresaActual().getLogoEmpresaRuta() != null) {
                            rutaImagen = SesionGlobal.getEmpresaActual().getLogoEmpresaRuta();
                        }
                    }
                    mostrarZoomImagen(rutaImagen);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaInventario);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1, true));
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        this.add(scrollPane, BorderLayout.CENTER);

        cargarDatosDesdeBD();
    }

    public void cargarDatosDesdeBD() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        InventarioDAO dao = new InventarioDAO();
        List<Producto> productos = dao.listarProductosActivos();

        for (Producto p : productos) {
            String pCompra = String.format("L %.2f", p.getPrecioCompra());
            String pVenta = String.format("L %.2f", p.getPrecioVenta());
            String pMayorista = p.getPrecioMayorista() > 0 ? String.format("L %.2f", p.getPrecioMayorista()) : "N/A";

            modeloTabla.addRow(new Object[]{
                p.getIdProducto(),       // 0: ID
                p.getRutaImagen(),       // 1: Ruta Foto
                p.getCodigoBarras(),     // 2: Código
                p.getNombreProducto(),   // 3: Nombre
                pCompra,                 // 4: Precio Compra
                pVenta,                  // 5: Precio Venta
                pMayorista,              // 6: Precio Técnico/Mayorista
                p.getStockProducto(),    // 7: Stock
                ""                       // 8: Botones de Acción
            });
        }
    }

    // =========================================================
    // RENDERIZADOR DE IMAGEN
    // =========================================================
    private class ImagenProductoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

            String rutaImagen = (value != null) ? value.toString() : null;

            if (rutaImagen == null || rutaImagen.isEmpty() || !new File(rutaImagen).exists()) {
                if (SesionGlobal.getEmpresaActual() != null && SesionGlobal.getEmpresaActual().getLogoEmpresaRuta() != null) {
                    rutaImagen = SesionGlobal.getEmpresaActual().getLogoEmpresaRuta();
                }
            }

            if (rutaImagen != null && new File(rutaImagen).exists()) {
                ImageIcon icon = new ImageIcon(rutaImagen);
                Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(img));
            } else {
                label.setText("No Img");
                label.setForeground(Color.GRAY);
            }
            return label;
        }
    }
    
    // =========================================================
    // CLASES PARA BOTONES DE ACCIÓN (LÁPIZ Y BASURERO)
    // =========================================================
    
    // 1. Dibujo de los iconos
    private class IconoEditar implements Icon {
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(13, 110, 253)); g2.setStroke(new BasicStroke(2));
            g2.drawLine(x + 12, y + 2, x + 16, y + 6); g2.drawLine(x + 16, y + 6, x + 6, y + 16); 
            g2.drawLine(x + 12, y + 2, x + 2, y + 12); g2.drawLine(x + 2, y + 12, x + 2, y + 16); g2.drawLine(x + 2, y + 16, x + 6, y + 16); 
        }
        @Override public int getIconWidth() { return 18; } @Override public int getIconHeight() { return 18; }
    }

    private class IconoEliminar implements Icon {
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 53, 69)); g2.setStroke(new BasicStroke(2));
            g2.drawLine(x + 2, y + 4, x + 16, y + 4); g2.drawLine(x + 7, y + 2, x + 11, y + 2); 
            g2.drawRect(x + 4, y + 4, 10, 12); g2.drawLine(x + 7, y + 7, x + 7, y + 13); g2.drawLine(x + 11, y + 7, x + 11, y + 13); 
        }
        @Override public int getIconWidth() { return 18; } @Override public int getIconHeight() { return 18; }
    }

    // 2. Contenedor de botones
    private class PanelAcciones extends JPanel {
        private JButton btnEditar, btnEliminar;
        public PanelAcciones() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 20)); // Margen superior para centrar verticalmente
            setOpaque(true);
            btnEditar = new JButton(new IconoEditar()); 
            btnEditar.setContentAreaFilled(false); 
            btnEditar.setBorder(null); 
            btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
            btnEditar.setToolTipText("Editar Producto");
            btnEliminar = new JButton(new IconoEliminar()); 
            btnEliminar.setContentAreaFilled(false); 
            btnEliminar.setBorder(null); 
            btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
            btnEliminar.setToolTipText("Eliminar Producto");
            add(btnEditar); add(btnEliminar);
        }
        public JButton getBtnEditar() { return btnEditar; } public JButton getBtnEliminar() { return btnEliminar; }
    }

    private class PanelAccionesRenderer implements TableCellRenderer {
        private PanelAcciones panel = new PanelAcciones();
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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
                int filaModelo = tablaInventario.convertRowIndexToModel(filaVista);
                int idProducto = (int) modeloTabla.getValueAt(filaModelo, 0); // Obtenemos el ID oculto
                
                // LA SOLUCIÓN: Usar PanelBuscarProducto.this en lugar de panel
                PanelInventario parent = (PanelInventario) SwingUtilities.getAncestorOfClass(PanelInventario.class, PanelBuscarProducto.this);
                
                if(parent != null) {
                    InventarioDAO dao = new InventarioDAO();
                    Producto p = dao.obtenerProductoPorId(idProducto); // Traemos los datos frescos
                    
                    if (p != null) {
                        parent.mostrarSubPanel(new PanelCrearProducto(p)); // Abrimos modo edición
                    } else {
                        JOptionPane.showMessageDialog(PanelBuscarProducto.this, "Error: No se encontraron los datos del producto en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Agregamos este else para que NUNCA vuelva a fallar en silencio
                    JOptionPane.showMessageDialog(PanelBuscarProducto.this, "Error de interfaz: No se encontró el contenedor principal.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.getBtnEliminar().addActionListener(e -> {
                fireEditingStopped();
                int filaModelo = tablaInventario.convertRowIndexToModel(filaVista);
                int idProducto = (int) modeloTabla.getValueAt(filaModelo, 0);
                String nombre = modeloTabla.getValueAt(filaModelo, 3).toString();
                
                int confirmacion = JOptionPane.showConfirmDialog(panel, "¿Está seguro de eliminar el producto:\n" + nombre + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    InventarioDAO dao = new InventarioDAO();
                    if (dao.eliminarProductoLogico(idProducto)) {
                        JOptionPane.showMessageDialog(panel, "Producto eliminado.");
                        cargarDatosDesdeBD();
                    }
                }
            });
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            filaVista = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        @Override public Object getCellEditorValue() { return null; }
    }
    
    private void mostrarZoomImagen(String ruta) {
        if (ruta == null || !new File(ruta).exists()) return;
        
        JDialog zoomDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Previsualización del Producto", true);
        zoomDialog.setLayout(new BorderLayout());
        zoomDialog.getContentPane().setBackground(new Color(18, 18, 18)); 
        
        int tamanoEstandar = 600;
        zoomDialog.setSize(tamanoEstandar, tamanoEstandar);
        
        Image imgOriginal = new ImageIcon(ruta).getImage();
        int anchoOriginal = imgOriginal.getWidth(null);
        int altoOriginal = imgOriginal.getHeight(null);
        
        if (anchoOriginal <= 0 || altoOriginal <= 0) return;

        int nuevoAncho = tamanoEstandar - 40;
        int nuevoAlto = tamanoEstandar - 40;
        
        if (anchoOriginal > altoOriginal) {
            nuevoAlto = (altoOriginal * nuevoAncho) / anchoOriginal;
        } else {
            nuevoAncho = (anchoOriginal * nuevoAlto) / altoOriginal;
        }
        
        // --- LLAMADA AL MOTOR DE ESCALADO PROGRESIVO ---
        java.awt.image.BufferedImage imgNitida = escalarConMaximaNitidez(imgOriginal, nuevoAncho, nuevoAlto);
        
        JLabel lblZoom = new JLabel(new ImageIcon(imgNitida));
        lblZoom.setHorizontalAlignment(SwingConstants.CENTER);
        
        zoomDialog.add(lblZoom, BorderLayout.CENTER);
        zoomDialog.setLocationRelativeTo(this);
        zoomDialog.setVisible(true);
    }

    /**
     * Reduce las dimensiones de la imagen por pasos intermedios para evitar la pérdida de definición.
     */
    private java.awt.image.BufferedImage escalarConMaximaNitidez(Image img, int targetWidth, int targetHeight) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        
        // Crear un lienzo inicial en RAM con la imagen original
        java.awt.image.BufferedImage scratch = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scratch.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        
        // Reducción progresiva dividiendo por 2 hasta acercarnos al tamaño final
        while (w > targetWidth * 2 || h > targetHeight * 2) {
            w = (w > targetWidth * 2) ? w / 2 : targetWidth;
            h = (h > targetHeight * 2) ? h / 2 : targetHeight;
            
            java.awt.image.BufferedImage temp = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            g2 = temp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(scratch, 0, 0, w, h, null);
            g2.dispose();
            scratch = temp;
        }
        
        // Renderizado final con ajuste Bicúbico y Antialiasing para máxima fidelidad
        java.awt.image.BufferedImage imgFinal = new java.awt.image.BufferedImage(targetWidth, targetHeight, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        g2 = imgFinal.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(scratch, 0, 0, targetWidth, targetHeight, null);
        g2.dispose();
        
        return imgFinal;
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
