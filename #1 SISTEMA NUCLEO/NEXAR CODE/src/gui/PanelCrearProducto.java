package gui;

import dao.InventarioDAO;
import dao.CatalogosDAO;
import modelo.Producto;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

public class PanelCrearProducto extends JPanel {

    private JTextField txtCodigoBarras;
    private JLabel lblErrorCodigo; 
    private JTextField txtNombre;
    
    private JComboBox<ItemCatalogo> cmbCategoria;
    private JComboBox<ItemCatalogo> cmbProveedor;
    private JComboBox<ItemCatalogo> cmbUbicacion;
    
    private JTextField txtPrecioCompra;
    private JTextField txtPrecioVenta;
    private JCheckBox chkPrecioMayorista;
    private JTextField txtPrecioMayorista;
    
    private JTextField txtStockInicial;
    private JTextField txtStockMinimo;
    
    private JLabel lblVistaPreviaImagen;
    private String rutaImagenSeleccionada = null; 
    private JButton btnGuardar;
    
    private Producto productoAEditar = null;
    private JButton btnKardex;
    
    private Set<String> codigosEnRam;

    public PanelCrearProducto() {
        this(null); // Llama al constructor de abajo en modo "Crear"
    }

    public PanelCrearProducto(Producto p) {
        this.productoAEditar = p;
        InventarioDAO dao = new InventarioDAO();
        codigosEnRam = dao.obtenerCodigosEnRam();
        if (codigosEnRam == null) codigosEnRam = new HashSet<>();
        
        if(productoAEditar != null) {
            codigosEnRam.remove(productoAEditar.getCodigoBarras()); // Evita que su propio código marque error
        }
        
        iniciarDiseno();
        configurarValidacionEnVivo();
        aplicarRestriccionesNumericas();
        cargarDatosCombos(); 
        
        if(productoAEditar != null) {
            cargarDatosEdicion();
        }
    }

    private void iniciarDiseno() {
        this.setLayout(new BorderLayout(20, 20));
        this.setBackground(new Color(18, 18, 18));
        this.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        String textoTitulo = (productoAEditar == null) ? "Registrar Nuevo Producto" : "Edición de Producto";
        JLabel lblTitulo = new JLabel(textoTitulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        this.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout(30, 0));
        panelCentral.setOpaque(false);

        // --- IZQUIERDA: FORMULARIO ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigoBarras = new JTextField(20);
        txtCodigoBarras.putClientProperty("JTextField.placeholderText", "Dejar vacío para usar ID autogenerado");
        
        lblErrorCodigo = new JLabel(" ");
        lblErrorCodigo.setForeground(new Color(220, 53, 69)); 
        lblErrorCodigo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        txtNombre = new JTextField(20);
        
        cmbCategoria = new JComboBox<>();
        cmbProveedor = new JComboBox<>();
        cmbUbicacion = new JComboBox<>();
        
        txtPrecioCompra = new JTextField(10);
        txtPrecioVenta = new JTextField(10);
        
        chkPrecioMayorista = new JCheckBox("Habilitar Precio Mayorista");
        chkPrecioMayorista.setBackground(new Color(30, 30, 30));
        chkPrecioMayorista.setForeground(Color.WHITE);
        chkPrecioMayorista.setFocusPainted(false);
        
        txtPrecioMayorista = new JTextField(10);
        txtPrecioMayorista.setEnabled(false); 
        
        chkPrecioMayorista.addActionListener(e -> {
            txtPrecioMayorista.setEnabled(chkPrecioMayorista.isSelected());
            if (!chkPrecioMayorista.isSelected()) txtPrecioMayorista.setText(""); 
        });

        txtStockInicial = new JTextField(10);
        txtStockMinimo = new JTextField("0", 10); 

        // Ensamblaje 
        agregarFilaCorta(pnlForm, gbc, 0, "Código de Barras:", txtCodigoBarras);
        gbc.gridy = 1; gbc.gridx = 1; gbc.insets = new Insets(0, 10, 10, 10);
        pnlForm.add(lblErrorCodigo, gbc);
        gbc.insets = new Insets(10, 10, 10, 10); 
        
        agregarFila(pnlForm, gbc, 2, "Nombre del Producto:", txtNombre);
        agregarFila(pnlForm, gbc, 3, "Categoría:", crearPanelCatalogo(cmbCategoria, "Categoría"));
        agregarFila(pnlForm, gbc, 4, "Proveedor:", crearPanelCatalogo(cmbProveedor, "Proveedor"));
        agregarFila(pnlForm, gbc, 5, "Ubicación:", crearPanelCatalogo(cmbUbicacion, "Ubicación"));
        
        agregarFilaCorta(pnlForm, gbc, 6, "Precio Compra (L):", txtPrecioCompra);
        agregarFilaCorta(pnlForm, gbc, 7, "Precio Venta (L):", txtPrecioVenta);
        
        gbc.gridy = 8; gbc.gridx = 0; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        pnlForm.add(chkPrecioMayorista, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        txtPrecioMayorista.setPreferredSize(new Dimension(150, 32)); 
        txtPrecioMayorista.setBackground(new Color(40, 40, 40));
        txtPrecioMayorista.setForeground(Color.WHITE);
        txtPrecioMayorista.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)), BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        pnlForm.add(txtPrecioMayorista, gbc);
        
        agregarFilaCorta(pnlForm, gbc, 9, "Stock Inicial:", txtStockInicial);
        agregarFilaCorta(pnlForm, gbc, 10, "Stock Mínimo:", txtStockMinimo);

        gbc.gridy = 11; gbc.weighty = 1.0;
        pnlForm.add(new JLabel(""), gbc);

        JScrollPane scrollForm = new JScrollPane(pnlForm);
        scrollForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);

        // --- DERECHA: PANEL DE IMAGEN ---
        JPanel pnlImagen = new JPanel();
        pnlImagen.setLayout(new BoxLayout(pnlImagen, BoxLayout.Y_AXIS));
        pnlImagen.setBackground(new Color(30, 30, 30));
        pnlImagen.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        pnlImagen.setPreferredSize(new Dimension(250, 0)); 

        JLabel lblTituloImg = new JLabel("Fotografía");
        lblTituloImg.setForeground(Color.WHITE);
        lblTituloImg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloImg.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblVistaPreviaImagen = new JLabel("Sin Imagen", SwingConstants.CENTER);
        lblVistaPreviaImagen.setPreferredSize(new Dimension(150, 150));
        lblVistaPreviaImagen.setMaximumSize(new Dimension(150, 150));
        lblVistaPreviaImagen.setBorder(BorderFactory.createDashedBorder(Color.GRAY, 3, 2));
        lblVistaPreviaImagen.setForeground(Color.GRAY);
        lblVistaPreviaImagen.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnCargarImagen = new JButton("Examinar...");
        btnCargarImagen.setBackground(new Color(50, 50, 50));
        btnCargarImagen.setForeground(Color.WHITE);
        btnCargarImagen.setFocusPainted(false);
        btnCargarImagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCargarImagen.addActionListener(e -> seleccionarImagen());

        pnlImagen.add(lblTituloImg);
        pnlImagen.add(Box.createVerticalStrut(20));
        pnlImagen.add(lblVistaPreviaImagen);
        pnlImagen.add(Box.createVerticalStrut(20));
        pnlImagen.add(btnCargarImagen);

        panelCentral.add(scrollForm, BorderLayout.CENTER);
        panelCentral.add(pnlImagen, BorderLayout.EAST);
        this.add(panelCentral, BorderLayout.CENTER);

        // --- PANEL INFERIOR: BOTONES ---
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlBotones.setOpaque(false);
        
        btnKardex = new JButton("Ver Kardex");
        btnKardex.setBackground(new Color(25, 135, 84)); // Verde
        btnKardex.setForeground(Color.WHITE);
        btnKardex.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKardex.setPreferredSize(new Dimension(140, 40));
        btnKardex.setFocusPainted(false);
        btnKardex.setVisible(productoAEditar != null);
        btnKardex.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            DialogoKardex dialogo = new DialogoKardex(parentWindow, productoAEditar);
            dialogo.setVisible(true);
            InventarioDAO dao = new InventarioDAO();
            productoAEditar = dao.obtenerProductoPorId(productoAEditar.getIdProducto());
            cargarDatosEdicion(); // Refresca los textfields visualmente
        });
        String textoBtnGuardar = (productoAEditar == null) ? "Guardar Producto" : "Actualizar Producto";
        btnGuardar = new JButton(textoBtnGuardar);
        btnGuardar.setBackground(new Color(13, 110, 253));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setPreferredSize(new Dimension(180, 40));
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> guardarProducto());
        
        pnlBotones.add(btnKardex);
        pnlBotones.add(btnGuardar);
        this.add(pnlBotones, BorderLayout.SOUTH);
    }

    // =========================================================
    // LÓGICA: RESTRICCIONES DE TECLADO
    // =========================================================
    private void aplicarRestriccionesNumericas() {
        permitirSoloNumeros(txtPrecioCompra, true);
        permitirSoloNumeros(txtPrecioVenta, true);
        permitirSoloNumeros(txtPrecioMayorista, true);
        permitirSoloNumeros(txtStockInicial, false);
        permitirSoloNumeros(txtStockMinimo, false);
    }

    private void permitirSoloNumeros(JTextField campo, boolean permiteDecimales) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (permiteDecimales) {
                    if (!Character.isDigit(c) && c != '.') e.consume();
                    if (c == '.' && campo.getText().contains(".")) e.consume();
                } else {
                    if (!Character.isDigit(c)) e.consume();
                }
            }
        });
    }

    // =========================================================
    // LÓGICA DE GUARDADO EN BASE DE DATOS
    // =========================================================
    private void cargarDatosCombos() {
        CatalogosDAO dao = new CatalogosDAO();
        
        cmbCategoria.removeAllItems();
        cmbCategoria.addItem(new ItemCatalogo(0, "Seleccione Categoría..."));
        for (Map.Entry<Integer, String> entry : dao.listarCategorias().entrySet()) {
            cmbCategoria.addItem(new ItemCatalogo(entry.getKey(), entry.getValue()));
        }
        
        cmbProveedor.removeAllItems();
        cmbProveedor.addItem(new ItemCatalogo(0, "Seleccione Proveedor..."));
        for (Map.Entry<Integer, String> entry : dao.listarProveedores().entrySet()) {
            cmbProveedor.addItem(new ItemCatalogo(entry.getKey(), entry.getValue()));
        }
        
        cmbUbicacion.removeAllItems();
        cmbUbicacion.addItem(new ItemCatalogo(0, "Seleccione Ubicación..."));
        for (Map.Entry<Integer, String> entry : dao.listarUbicaciones().entrySet()) {
            cmbUbicacion.addItem(new ItemCatalogo(entry.getKey(), entry.getValue()));
        }
    }
    
    private void guardarProducto() {
        if (txtNombre.getText().trim().isEmpty() || txtPrecioCompra.getText().trim().isEmpty() || txtPrecioVenta.getText().trim().isEmpty() || txtStockInicial.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los campos obligatorios (Nombre, Precios y Stock).", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ItemCatalogo cat = (ItemCatalogo) cmbCategoria.getSelectedItem();
        ItemCatalogo prov = (ItemCatalogo) cmbProveedor.getSelectedItem();
        ItemCatalogo ubi = (ItemCatalogo) cmbUbicacion.getSelectedItem();
        
        if (cat == null || cat.id == 0 || prov == null || prov.id == 0 || ubi == null || ubi.id == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar Categoría, Proveedor y Ubicación.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Producto p = (productoAEditar == null) ? new Producto() : productoAEditar;
            p.setCodigoBarras(txtCodigoBarras.getText().trim());
            p.setNombreProducto(txtNombre.getText().trim());
            p.setIdCategoria(cat.id);
            p.setIdProveedor(prov.id);
            p.setIdUbicacion(ubi.id);
            p.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText().trim()));
            p.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText().trim()));
            p.setPrecioMayorista(chkPrecioMayorista.isSelected() && !txtPrecioMayorista.getText().trim().isEmpty() ? Double.parseDouble(txtPrecioMayorista.getText().trim()) : 0.0);
            p.setStockProducto(Integer.parseInt(txtStockInicial.getText().trim()));
            p.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
            p.setRutaImagen(rutaImagenSeleccionada);

            InventarioDAO dao = new InventarioDAO();
            boolean exito;
            if (productoAEditar == null) {
                exito = dao.registrarProducto(p);
            } else {
                exito = dao.actualizarProducto(p);
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Producto " + ((productoAEditar == null)? "guardado" : "actualizado") + " exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                if (productoAEditar == null) limpiarFormulario();
            } else {
                // AQUÍ FALTABA ESTE BLOQUE
                JOptionPane.showMessageDialog(this, "Error al guardar el producto en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique que los valores numéricos sean válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtCodigoBarras.setText(""); txtNombre.setText(""); txtPrecioCompra.setText("");
        txtPrecioVenta.setText(""); txtPrecioMayorista.setText(""); chkPrecioMayorista.setSelected(false);
        txtPrecioMayorista.setEnabled(false); txtStockInicial.setText(""); txtStockMinimo.setText("0");
        lblVistaPreviaImagen.setIcon(null); lblVistaPreviaImagen.setText("Sin Imagen"); rutaImagenSeleccionada = null;
        cmbCategoria.setSelectedIndex(0); cmbProveedor.setSelectedIndex(0); cmbUbicacion.setSelectedIndex(0);
        codigosEnRam = new InventarioDAO().obtenerCodigosEnRam();
    }
    
    private void cargarDatosEdicion() {
        txtCodigoBarras.setText(productoAEditar.getCodigoBarras());
        txtNombre.setText(productoAEditar.getNombreProducto());
        txtPrecioCompra.setText(String.valueOf(productoAEditar.getPrecioCompra()));
        txtPrecioVenta.setText(String.valueOf(productoAEditar.getPrecioVenta()));
        
        if(productoAEditar.getPrecioMayorista() > 0) {
            chkPrecioMayorista.setSelected(true);
            txtPrecioMayorista.setEnabled(true);
            txtPrecioMayorista.setText(String.valueOf(productoAEditar.getPrecioMayorista()));
        }
        
        // Bloquear el stock porque es por Kardex
        txtStockInicial.setText(String.valueOf(productoAEditar.getStockProducto()));
        txtStockInicial.setEnabled(false);
        txtStockInicial.setToolTipText("El stock solo puede modificarse a través del Kardex.");
        
        txtStockMinimo.setText(String.valueOf(productoAEditar.getStockMinimo()));
        
        seleccionarComboPorId(cmbCategoria, productoAEditar.getIdCategoria());
        seleccionarComboPorId(cmbProveedor, productoAEditar.getIdProveedor());
        seleccionarComboPorId(cmbUbicacion, productoAEditar.getIdUbicacion());
        
        if(productoAEditar.getRutaImagen() != null && new File(productoAEditar.getRutaImagen()).exists()) {
            rutaImagenSeleccionada = productoAEditar.getRutaImagen();
            ImageIcon icono = new ImageIcon(rutaImagenSeleccionada);
            Image img = icono.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblVistaPreviaImagen.setText("");
            lblVistaPreviaImagen.setIcon(new ImageIcon(img));
        }
    }
    
    private void seleccionarComboPorId(JComboBox<ItemCatalogo> combo, int id) {
        for(int i = 0; i < combo.getItemCount(); i++) {
            ItemCatalogo item = combo.getItemAt(i);
            if(item.id == id) { combo.setSelectedIndex(i); break; }
        }
    }

    // =========================================================
    // VENTANAS PARA CREAR/EDITAR CATÁLOGOS (FUNCIONALES)
    // =========================================================

    private void abrirDialogoMantenimiento(String tipoCatalogo) {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) ventanaPadre, "Nuevo/a " + tipoCatalogo, true);
        dialog.setSize(400, 450); dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout()); dialog.getContentPane().setBackground(new Color(18, 18, 18));

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.CENTER)); pnlTop.setBackground(new Color(18, 18, 18));
        JLabel lblTop = new JLabel("Registrar " + tipoCatalogo); lblTop.setFont(new Font("Segoe UI", Font.BOLD, 18)); lblTop.setForeground(Color.WHITE);
        pnlTop.add(lblTop); dialog.add(pnlTop, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridBagLayout()); pnlForm.setBackground(new Color(30, 30, 30));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 5, 10, 5); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0; gbc.weightx = 1.0;

        JTextField txtNombreCat = crearInputOscuro();
        agregarFilaDialog(pnlForm, gbc, 0, "Nombre:", txtNombreCat);
        
        JTextField txtDesc = null, txtGarantia = null, txtEncargado = null, txtTel = null, txtDir = null, txtRepuestos = null;

        if (tipoCatalogo.equals("Categoría")) {
            txtDesc = crearInputOscuro();
            txtGarantia = crearInputOscuro();
            permitirSoloNumeros(txtGarantia, false); // Solo números para los días
            agregarFilaDialog(pnlForm, gbc, 1, "Descripción:", txtDesc);
            agregarFilaDialog(pnlForm, gbc, 2, "Días de Garantía:", txtGarantia);
        } else if (tipoCatalogo.equals("Proveedor")) {
            txtEncargado = crearInputOscuro(); txtTel = crearInputOscuro();
            txtDir = crearInputOscuro(); txtRepuestos = crearInputOscuro();
            permitirSoloNumeros(txtTel, false); // Solo números para teléfono
            agregarFilaDialog(pnlForm, gbc, 1, "Encargado:", txtEncargado);
            agregarFilaDialog(pnlForm, gbc, 2, "Teléfono:", txtTel);
            agregarFilaDialog(pnlForm, gbc, 3, "Dirección:", txtDir);
            agregarFilaDialog(pnlForm, gbc, 4, "Tipo Repuestos:", txtRepuestos);
        }

        gbc.gridy = 10; gbc.weighty = 1.0; pnlForm.add(new JLabel(""), gbc);
        dialog.add(pnlForm, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pnlBotones.setBackground(new Color(18, 18, 18));
        JButton btnCancelar = new JButton("Cancelar"); btnCancelar.setBackground(new Color(70, 70, 70)); btnCancelar.setForeground(Color.WHITE); btnCancelar.addActionListener(e -> dialog.dispose());
        JButton btnGuardarCat = new JButton("Guardar"); btnGuardarCat.setBackground(new Color(13, 110, 253)); btnGuardarCat.setForeground(Color.WHITE);
        
        // Variables finales para usar dentro del listener
        final JTextField fTxtDesc = txtDesc, fTxtGarantia = txtGarantia, fTxtEncargado = txtEncargado, fTxtTel = txtTel, fTxtDir = txtDir, fTxtRepuestos = txtRepuestos;
        
        btnGuardarCat.addActionListener(e -> {
            if (txtNombreCat.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(dialog, "El nombre es obligatorio."); return; }
            CatalogosDAO dao = new CatalogosDAO();
            boolean exito = false;
            
            if (tipoCatalogo.equals("Categoría")) {
                int garantias = fTxtGarantia.getText().trim().isEmpty() ? 0 : Integer.parseInt(fTxtGarantia.getText().trim());
                exito = dao.registrarCategoria(txtNombreCat.getText().trim(), fTxtDesc.getText().trim(), garantias);
            } else if (tipoCatalogo.equals("Proveedor")) {
                exito = dao.registrarProveedor(txtNombreCat.getText().trim(), fTxtEncargado.getText().trim(), fTxtTel.getText().trim(), fTxtDir.getText().trim(), fTxtRepuestos.getText().trim());
            } else {
                exito = dao.registrarUbicacion(txtNombreCat.getText().trim());
            }

            if (exito) {
                JOptionPane.showMessageDialog(dialog, tipoCatalogo + " registrada exitosamente.");
                cargarDatosCombos(); // Recarga los combos inmediatamente
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al guardar en la base de datos.");
            }
        });
        
        pnlBotones.add(btnCancelar); pnlBotones.add(btnGuardarCat);
        dialog.add(pnlBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // =========================================================
    // UTILIDADES RESTANTES (Sin cambios estructurales)
    // =========================================================
    private JTextField crearInputOscuro() {
        JTextField txt = new JTextField(20); txt.setFont(new Font("Segoe UI", Font.PLAIN, 14)); txt.setBackground(new Color(40, 40, 40));
        txt.setForeground(Color.WHITE); txt.setCaretColor(Color.WHITE); txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)), BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return txt;
    }

    private void agregarFilaDialog(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JTextField campo) {
        gbc.gridy = fila * 2; JLabel lbl = new JLabel(etiqueta); lbl.setForeground(Color.WHITE); lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, gbc); gbc.gridy = (fila * 2) + 1; panel.add(campo, gbc);
    }

    private void configurarValidacionEnVivo() {
        txtCodigoBarras.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validarCodigo(); }
            @Override public void removeUpdate(DocumentEvent e) { validarCodigo(); }
            @Override public void changedUpdate(DocumentEvent e) { validarCodigo(); }
        });
    }

    private void validarCodigo() {
        String codigoStr = txtCodigoBarras.getText().trim();
        if (codigoStr.isEmpty()) { restaurarEstiloCodigo(); btnGuardar.setEnabled(true); return; }
        if (codigosEnRam.contains(codigoStr)) {
            txtCodigoBarras.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 53, 69), 2), BorderFactory.createEmptyBorder(4, 7, 4, 7)));
            lblErrorCodigo.setText("⚠ Este código ya está registrado en el inventario."); btnGuardar.setEnabled(false); 
        } else { restaurarEstiloCodigo(); btnGuardar.setEnabled(true); }
    }

    private void restaurarEstiloCodigo() {
        txtCodigoBarras.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)), BorderFactory.createEmptyBorder(5, 8, 5, 8))); lblErrorCodigo.setText(" ");
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridy = fila; gbc.gridx = 0; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.EAST;
        JLabel label = new JLabel(etiqueta); label.setForeground(Color.WHITE); label.setFont(new Font("Segoe UI", Font.BOLD, 13)); panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        if (campo instanceof JTextField) {
            campo.setFont(new Font("Segoe UI", Font.PLAIN, 14)); campo.setBackground(new Color(40, 40, 40)); campo.setForeground(Color.WHITE);
            ((JTextField)campo).setCaretColor(Color.WHITE); campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)), BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        } panel.add(campo, gbc);
    }

    private void agregarFilaCorta(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridy = fila; gbc.gridx = 0; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        JLabel label = new JLabel(etiqueta); label.setForeground(Color.WHITE); label.setFont(new Font("Segoe UI", Font.BOLD, 13)); panel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        if (campo instanceof JTextField) {
            campo.setPreferredSize(new Dimension(200, 32)); campo.setFont(new Font("Segoe UI", Font.PLAIN, 14)); campo.setBackground(new Color(40, 40, 40));
            campo.setForeground(Color.WHITE); ((JTextField)campo).setCaretColor(Color.WHITE); campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)), BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        } panel.add(campo, gbc); gbc.fill = GridBagConstraints.HORIZONTAL; 
    }

    private JPanel crearPanelCatalogo(JComboBox<ItemCatalogo> combo, String tipoCatalogo) {
        JPanel pnl = new JPanel(new BorderLayout(5, 0)); 
        pnl.setOpaque(false); 
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        pnl.add(combo, BorderLayout.CENTER);
        
        JPanel pnlBotones = new JPanel(new GridLayout(1, 2, 5, 0)); 
        pnlBotones.setOpaque(false);
        
        JButton btnNuevo = new JButton("+"); 
        btnNuevo.setBackground(new Color(25, 135, 84)); 
        btnNuevo.setForeground(Color.WHITE); 
        btnNuevo.setFocusPainted(false); 
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.addActionListener(e -> abrirDialogoMantenimiento(tipoCatalogo));
        
        JButton btnEditar = new JButton("✎"); 
        btnEditar.setBackground(new Color(13, 110, 253)); 
        btnEditar.setForeground(Color.WHITE); 
        btnEditar.setFocusPainted(false); 
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditar.addActionListener(e -> {
            ItemCatalogo item = (ItemCatalogo) combo.getSelectedItem();
            if (item == null || item.id == 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un(a) " + tipoCatalogo + " válido de la lista para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            } else {
                abrirDialogoEdicion(tipoCatalogo, item);
            }
        });
        
        // --- ESTO ERA LO QUE FALTABA ---
        pnlBotones.add(btnNuevo); 
        pnlBotones.add(btnEditar); 
        pnl.add(pnlBotones, BorderLayout.EAST); 
        return pnl; // Devolvemos el panel construido en lugar de null
    }

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser(); fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile(); rutaImagenSeleccionada = archivo.getAbsolutePath();
            ImageIcon iconoOriginal = new ImageIcon(rutaImagenSeleccionada); Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblVistaPreviaImagen.setText(""); lblVistaPreviaImagen.setIcon(new ImageIcon(imagenEscalada));
        }
    }

    private class ItemCatalogo {
        int id; String nombre;
        public ItemCatalogo(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return nombre; }
    }
    
    private void abrirDialogoEdicion(String tipoCatalogo, ItemCatalogo item) {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) ventanaPadre, "Editar " + tipoCatalogo, true);
        dialog.setSize(400, 450); dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout()); dialog.getContentPane().setBackground(new Color(18, 18, 18));

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.CENTER)); pnlTop.setBackground(new Color(18, 18, 18));
        JLabel lblTop = new JLabel("Editar " + tipoCatalogo); lblTop.setFont(new Font("Segoe UI", Font.BOLD, 18)); lblTop.setForeground(Color.WHITE);
        pnlTop.add(lblTop); dialog.add(pnlTop, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridBagLayout()); pnlForm.setBackground(new Color(30, 30, 30));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 5, 10, 5); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0; gbc.weightx = 1.0;

        JTextField txtNombreCat = crearInputOscuro();
        txtNombreCat.setText(item.nombre); // Precargar nombre
        agregarFilaDialog(pnlForm, gbc, 0, "Nombre:", txtNombreCat);
        
        JTextField txtDesc = null, txtGarantia = null, txtEncargado = null, txtTel = null, txtDir = null, txtRepuestos = null;
        CatalogosDAO dao = new CatalogosDAO();

        if (tipoCatalogo.equals("Categoría")) {
            String[] datos = dao.obtenerDatosCategoria(item.id);
            txtDesc = crearInputOscuro(); txtDesc.setText(datos[0]);
            txtGarantia = crearInputOscuro(); txtGarantia.setText(datos[1]);
            permitirSoloNumeros(txtGarantia, false);
            agregarFilaDialog(pnlForm, gbc, 1, "Descripción:", txtDesc);
            agregarFilaDialog(pnlForm, gbc, 2, "Días de Garantía:", txtGarantia);
        } else if (tipoCatalogo.equals("Proveedor")) {
            String[] datos = dao.obtenerDatosProveedor(item.id);
            txtEncargado = crearInputOscuro(); txtEncargado.setText(datos[0]);
            txtTel = crearInputOscuro(); txtTel.setText(datos[1]);
            txtDir = crearInputOscuro(); txtDir.setText(datos[2]);
            txtRepuestos = crearInputOscuro(); txtRepuestos.setText(datos[3]);
            permitirSoloNumeros(txtTel, false);
            agregarFilaDialog(pnlForm, gbc, 1, "Encargado:", txtEncargado);
            agregarFilaDialog(pnlForm, gbc, 2, "Teléfono:", txtTel);
            agregarFilaDialog(pnlForm, gbc, 3, "Dirección:", txtDir);
            agregarFilaDialog(pnlForm, gbc, 4, "Tipo Repuestos:", txtRepuestos);
        }

        gbc.gridy = 10; gbc.weighty = 1.0; pnlForm.add(new JLabel(""), gbc);
        dialog.add(pnlForm, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pnlBotones.setBackground(new Color(18, 18, 18));
        JButton btnCancelar = new JButton("Cancelar"); btnCancelar.setBackground(new Color(70, 70, 70)); btnCancelar.setForeground(Color.WHITE); btnCancelar.addActionListener(e -> dialog.dispose());
        JButton btnGuardarCat = new JButton("Actualizar"); btnGuardarCat.setBackground(new Color(25, 135, 84)); btnGuardarCat.setForeground(Color.WHITE);
        
        final JTextField fTxtDesc = txtDesc, fTxtGarantia = txtGarantia, fTxtEncargado = txtEncargado, fTxtTel = txtTel, fTxtDir = txtDir, fTxtRepuestos = txtRepuestos;
        
        btnGuardarCat.addActionListener(e -> {
            if (txtNombreCat.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(dialog, "El nombre es obligatorio."); return; }
            boolean exito = false;
            
            if (tipoCatalogo.equals("Categoría")) {
                int garantias = fTxtGarantia.getText().trim().isEmpty() ? 0 : Integer.parseInt(fTxtGarantia.getText().trim());
                exito = dao.actualizarCategoria(item.id, txtNombreCat.getText().trim(), fTxtDesc.getText().trim(), garantias);
            } else if (tipoCatalogo.equals("Proveedor")) {
                exito = dao.actualizarProveedor(item.id, txtNombreCat.getText().trim(), fTxtEncargado.getText().trim(), fTxtTel.getText().trim(), fTxtDir.getText().trim(), fTxtRepuestos.getText().trim());
            } else {
                exito = dao.actualizarUbicacion(item.id, txtNombreCat.getText().trim());
            }

            if (exito) {
                JOptionPane.showMessageDialog(dialog, tipoCatalogo + " actualizada exitosamente.");
                cargarDatosCombos(); // Recarga los combos inmediatamente
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al actualizar en la base de datos.");
            }
        });
        
        pnlBotones.add(btnCancelar); pnlBotones.add(btnGuardarCat);
        dialog.add(pnlBotones, BorderLayout.SOUTH);
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
