package utilidades;

import modelo.Empresa;
import javax.swing.*;
import java.awt.*;

// Importaciones de iTextPDF para generar y dibujar el documento
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.Image;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.BaseColor;
import java.io.FileOutputStream;

public class GeneradorTickets {

    /**
     * Construye la vista previa del ticket para la interfaz gráfica.
     */
    public static JPanel crearTicketVistaPrevia(String tituloDocumento, JTextArea txtAreaEditable) {
        JPanel panelCentrador = new JPanel(new GridBagLayout());
        panelCentrador.setBackground(new Color(30, 30, 30)); 

        JPanel pnlTicket = new JPanel();
        pnlTicket.setLayout(new BoxLayout(pnlTicket, BoxLayout.Y_AXIS));
        pnlTicket.setBackground(Color.WHITE);
        pnlTicket.setPreferredSize(new Dimension(280, 450));
        pnlTicket.setMinimumSize(new Dimension(280, 450));
        pnlTicket.setMaximumSize(new Dimension(280, 450));

        pnlTicket.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        Font fBold = new Font("Courier New", Font.BOLD, 14);
        Font fNormal = new Font("Courier New", Font.PLAIN, 12);

        Empresa emp = SesionGlobal.getEmpresaActual();
        String empNombre = emp != null && emp.getNombreEmpresa() != null ? emp.getNombreEmpresa().toUpperCase() : "NEXAR STORE";
        String empRtn = emp != null && emp.getRtnEmpresa() != null ? "RTN: " + emp.getRtnEmpresa() : "RTN: PENDIENTE";
        
        pnlTicket.add(crearLabelCentrado(empNombre, fBold, Color.BLACK));
        pnlTicket.add(crearLabelCentrado(empRtn, fNormal, Color.BLACK));
        pnlTicket.add(Box.createVerticalStrut(5));
        pnlTicket.add(crearLabelCentrado(tituloDocumento, fNormal, Color.DARK_GRAY));
        pnlTicket.add(Box.createVerticalStrut(10));
        pnlTicket.add(crearLabelCentrado("===============================", fNormal, Color.BLACK));
        
        pnlTicket.add(Box.createVerticalStrut(10));
        pnlTicket.add(crearLabelCentrado("CANT  DESCRIPCION       TOTAL", fNormal, Color.BLACK));
        pnlTicket.add(crearLabelCentrado("-------------------------------", fNormal, Color.BLACK));
        pnlTicket.add(crearLabelCentrado(" 1x   Reemplazo LCD    L 1500", fNormal, Color.BLACK));
        pnlTicket.add(crearLabelCentrado("-------------------------------", fNormal, Color.BLACK));
        pnlTicket.add(crearLabelCentrado("TOTAL:                 L 1500", fBold, Color.BLACK));
        pnlTicket.add(Box.createVerticalStrut(20));

        pnlTicket.add(crearLabelCentrado("===============================", fNormal, Color.BLACK));
        pnlTicket.add(txtAreaEditable);

        panelCentrador.add(pnlTicket);
        return panelCentrador;
    }

    public static JLabel crearLabelCentrado(String texto, Font fuente, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /**
     * Genera el archivo PDF físico del ticket de venta utilizando iTextPDF.
     * Incorpora dibujos vectoriales para las redes sociales y salto de línea en descripciones largas.
     */
    public static void generarTicketVentaPDF(String rutaDestino, String nombreCliente, java.util.List<Object[]> detalles, double subtotal, double isv, double total, boolean esFactura) throws Exception {
        Rectangle tamanoTicket = new Rectangle(226, 800); // Formato de ticket térmico 80mm
        Document documento = new Document(tamanoTicket, 10, 10, 10, 10);
        PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(rutaDestino));
        documento.open();

        com.itextpdf.text.Font fNormal = FontFactory.getFont(FontFactory.COURIER, 9);
        com.itextpdf.text.Font fBold = FontFactory.getFont(FontFactory.COURIER_BOLD, 9);
        com.itextpdf.text.Font fTitulo = FontFactory.getFont(FontFactory.COURIER_BOLD, 12);

        Empresa emp = SesionGlobal.getEmpresaActual();
        
        String empNombre = emp != null && emp.getNombreEmpresa() != null ? emp.getNombreEmpresa().toUpperCase() : "NEXAR STORE";
        String empDueño = emp != null && emp.getDuenoEmpresa() != null ? "Prop: " + emp.getDuenoEmpresa() : "";
        String empRtn = emp != null && emp.getRtnEmpresa() != null ? "RTN: " + emp.getRtnEmpresa() : "RTN: PENDIENTE";

        // Título Principal
        Paragraph parrafoNombre = new Paragraph(empNombre + "\n", fTitulo);
        parrafoNombre.setAlignment(Element.ALIGN_CENTER);
        documento.add(parrafoNombre);

        // Cabecera Texto Plano (Dueño y RTN)
        Paragraph cabeceraTexto = new Paragraph();
        cabeceraTexto.setAlignment(Element.ALIGN_CENTER);
        cabeceraTexto.setFont(fBold);
        if (!empDueño.isEmpty()) cabeceraTexto.add(new Chunk(empDueño + "\n"));
        cabeceraTexto.add(new Chunk(empRtn + "\n\n"));
        documento.add(cabeceraTexto);

        // --- SECCIÓN DE DATOS CON ÍCONOS DIBUJADOS ---
        Paragraph cabeceraIconos = new Paragraph();
        cabeceraIconos.setAlignment(Element.ALIGN_CENTER);
        cabeceraIconos.setFont(fNormal);

        if (emp != null) {
            agregarLineaIcono(cabeceraIconos, writer, "dir", emp.getDireccionEmpresa());
            agregarLineaIcono(cabeceraIconos, writer, "tel", emp.getNumeroTelefono());
            agregarLineaIcono(cabeceraIconos, writer, "tel", emp.getTelefonoSecundario());
            agregarLineaIcono(cabeceraIconos, writer, "wa", emp.getWhatsapp());
            agregarLineaIcono(cabeceraIconos, writer, "fb", emp.getFacebook());
            agregarLineaIcono(cabeceraIconos, writer, "mail", emp.getEmail());
            agregarLineaIcono(cabeceraIconos, writer, "web", emp.getWeb());
        }
        documento.add(cabeceraIconos);

        // Título del Documento
        String tituloDocumento = esFactura ? "FACTURA" : "COMPROBANTE DE VENTA";
        Paragraph tituloDoc = new Paragraph("===============================\n" + tituloDocumento + "\n===============================\n", fBold);
        tituloDoc.setAlignment(Element.ALIGN_CENTER);
        documento.add(tituloDoc);

        // Información de Venta
        Paragraph info = new Paragraph("Cliente: " + nombreCliente + "\nFecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) + "\n-------------------------------\n", fNormal);
        info.setAlignment(Element.ALIGN_LEFT);
        documento.add(info);

        // --- DETALLES DE PRODUCTOS CON SALTO DE LÍNEA INTELIGENTE ---
        StringBuilder sbDetalles = new StringBuilder();
        sbDetalles.append(String.format("%-2s %-15s %6s\n", "C.", "DESCRIPCION", "TOTAL"));
        sbDetalles.append("-------------------------------\n");
        
        for (Object[] fila : detalles) {
            String nom = fila[2].toString();
            int cant = (int) fila[3];
            double totalFila = (double) fila[5];

            int maxLen = 15; // Límite de caracteres antes de bajar de renglón
            
            if (nom.length() <= maxLen) {
                // Producto corto: Imprime normal
                sbDetalles.append(String.format("%-2s %-15s %6.2f\n", cant, nom, totalFila));
            } else {
                // Producto largo: Imprime la primera línea con cantidades y precios
                sbDetalles.append(String.format("%-2s %-15s %6.2f\n", cant, nom.substring(0, maxLen), totalFila));
                
                // Baja de renglón imprimiendo el resto del nombre perfectamente alineado
                int index = maxLen;
                while (index < nom.length()) {
                    int end = Math.min(index + maxLen, nom.length());
                    sbDetalles.append(String.format("   %-15s\n", nom.substring(index, end)));
                    index += maxLen;
                }
            }
        }
        sbDetalles.append("-------------------------------\n");
        Paragraph parrafoDetalles = new Paragraph(sbDetalles.toString(), fNormal);
        documento.add(parrafoDetalles);

        // --- TOTALES FINALES ---
        Paragraph totales = new Paragraph(
            String.format("%-15s L %8.2f\n", "SUBTOTAL:", subtotal) +
            String.format("%-15s L %8.2f\n", "I.S.V (15%):", isv) +
            String.format("%-15s L %8.2f\n", "TOTAL A PAGAR:", total) +
            "\n===============================\n", fBold);
        totales.setAlignment(Element.ALIGN_RIGHT);
        documento.add(totales);

        // Pie de Página
        String msjPie = emp != null && emp.getMensajeTicketPieFactura() != null ? emp.getMensajeTicketPieFactura() : "¡Gracias por su preferencia!";
        if (!esFactura) {
            msjPie = emp != null && emp.getMensajeTicketPieRecibo() != null ? emp.getMensajeTicketPieRecibo() : "Este documento no es valido como factura.";
        }
        Paragraph pie = new Paragraph(msjPie, fNormal);
        pie.setAlignment(Element.ALIGN_CENTER);
        documento.add(pie);

        documento.close();
    }

    // ==============================================================================
    // MOTOR DE DIBUJO VECTORIAL PARA EL PDF
    // ==============================================================================

    /**
     * Agrega el bloque de ícono + texto alineado a la cabecera.
     */
    private static void agregarLineaIcono(Paragraph p, PdfWriter writer, String tipo, String texto) throws Exception {
        if (texto != null && !texto.trim().isEmpty()) {
            Image icon = crearIconoVectorial(writer, tipo);
            p.add(new Chunk(icon, 0, -1)); // -1 alinea el icono ligeramente con el texto
            p.add(new Chunk(" " + texto.trim() + "\n"));
        }
    }

    /**
     * Dibuja los iconos literalmente a través de figuras geométricas usando PdfContentByte.
     */
    private static Image crearIconoVectorial(PdfWriter writer, String tipo) throws Exception {
        PdfContentByte cb = writer.getDirectContent();
        PdfTemplate tp = cb.createTemplate(10, 10);
        tp.setColorStroke(BaseColor.DARK_GRAY);
        tp.setColorFill(BaseColor.DARK_GRAY);
        tp.setLineWidth(0.8f);

        switch (tipo) {
            case "tel":
                // Dibuja un teléfono móvil clásico (Bordes redondeados y la rayita del altavoz)
                tp.roundRectangle(2, 1, 6, 8, 1); 
                tp.stroke();
                tp.moveTo(4, 2); tp.lineTo(6, 2); 
                tp.stroke();
                break;
            case "wa":
                // Dibuja una burbuja de chat de WhatsApp (Círculo con colita)
                tp.circle(5, 5, 4); 
                tp.stroke();
                tp.moveTo(2, 3); tp.lineTo(1, 1); tp.lineTo(3, 2); 
                tp.stroke();
                break;
            case "fb":
                // Dibuja la 'f' característica de Facebook en un bloque cuadrado
                tp.rectangle(1, 1, 8, 8); 
                tp.fill();
                tp.setColorStroke(BaseColor.WHITE); 
                tp.setLineWidth(1.2f);
                tp.moveTo(6, 8); tp.lineTo(4, 8); tp.lineTo(4, 1); tp.stroke();
                tp.moveTo(3, 5); tp.lineTo(6, 5); tp.stroke();
                break;
            case "mail":
                // Dibuja el sobre de un correo electrónico
                tp.rectangle(1, 2, 8, 6); 
                tp.stroke();
                tp.moveTo(1, 8); tp.lineTo(5, 5); tp.lineTo(9, 8); 
                tp.stroke();
                break;
            case "web":
                // Dibuja un ícono global (El mundo de internet)
                tp.circle(5, 5, 4); 
                tp.stroke();
                tp.moveTo(1, 5); tp.lineTo(9, 5); tp.stroke();
                tp.moveTo(5, 1); tp.lineTo(5, 9); tp.stroke();
                break;
            case "dir":
                // Dibuja un pin de ubicación (Globo con pico hacia abajo)
                tp.circle(5, 7, 2.5f); 
                tp.stroke();
                tp.moveTo(3.2f, 5.2f); tp.lineTo(5, 1); tp.lineTo(6.8f, 5.2f); 
                tp.stroke();
                break;
        }
        return Image.getInstance(tp);
    }
}