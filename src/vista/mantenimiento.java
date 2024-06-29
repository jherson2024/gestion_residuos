package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class mantenimiento {
    private static JFrame frame;
    private static JPanel mainPanel;
    private static String currentPanelType;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crear el marco principal
            frame = new JFrame("Mantenimiento");
            frame.setSize(600, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            mainPanel = new JPanel(new GridLayout(1, 3)); // Aumentar el GridLayout a 1 fila y 3 columnas
            mainPanel.setBackground(Color.WHITE);

            // Crear el menú
            JMenuBar menuBar = new JMenuBar();
            JMenu archivoMenu = new JMenu("Opciones");
            JMenuItem salirItem = new JMenuItem("Salir");
            salirItem.addActionListener(e -> System.exit(0));
            archivoMenu.add(salirItem);
            menuBar.add(archivoMenu);
            frame.setJMenuBar(menuBar);

            // Botón para mostrar el panel de tablas referenciales
            JButton btnTablasReferenciales = new JButton("Tablas de Referencia");
            btnTablasReferenciales.addActionListener(e -> mostrarPanel("tablas_de_referencia"));
            btnTablasReferenciales.setBackground(Color.WHITE);
            mainPanel.add(btnTablasReferenciales);

            // Botón para mostrar el panel de tablas fundamentales
            JButton btnTablasFundamentales = new JButton("Tablas Fundamentales");
            btnTablasFundamentales.addActionListener(e -> mostrarPanel("tablas_fundamentales"));
            btnTablasFundamentales.setBackground(Color.WHITE);
            mainPanel.add(btnTablasFundamentales);

            // Botón para mostrar el panel de tablas de asociación
            JButton btnTablasAsociacion = new JButton("Tablas de Asociación");
            btnTablasAsociacion.addActionListener(e -> mostrarPanel("tablas_de_asociacion"));
            btnTablasAsociacion.setBackground(Color.WHITE);
            mainPanel.add(btnTablasAsociacion);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }

    private static void mostrarPanel(String tipoPanel) {
    	currentPanelType = tipoPanel;
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);

        // Sub-panel para los botones
        JPanel botonesPanel = new JPanel(new GridLayout(4, 4));
        botonesPanel.setBackground(Color.LIGHT_GRAY);

        // Agregar botones al sub-panel según el tipo de panel
        if ("tablas_de_referencia".equals(tipoPanel)) {
            addButtonToPanel(botonesPanel, "actividad");
            addButtonToPanel(botonesPanel, "disposicionesFinales");
            addButtonToPanel(botonesPanel, "metodosDeTratamiento");
            addButtonToPanel(botonesPanel, "desventajasTratamiento");
            addButtonToPanel(botonesPanel, "impactoAmbiental");
            addButtonToPanel(botonesPanel, "licenciaDisposicion");
            addButtonToPanel(botonesPanel, "licenciaTraslado");
            addButtonToPanel(botonesPanel, "beneficiosTratamiento");
            addButtonToPanel(botonesPanel, "constituyente");
            addButtonToPanel(botonesPanel, "sectorIndustrial");
            addButtonToPanel(botonesPanel, "tamanoEmpresa");
            addButtonToPanel(botonesPanel, "tipoInstalacion");
            addButtonToPanel(botonesPanel, "unidadDeMedida");
            addButtonToPanel(botonesPanel, "entidad");
            addButtonToPanel(botonesPanel, "regulaciones");
        } else if ("tablas_fundamentales".equals(tipoPanel)) {
        	addButtonToPanel(botonesPanel, "empresaProductora");
            addButtonToPanel(botonesPanel, "empresaTransportista");
            addButtonToPanel(botonesPanel, "conductor");
            addButtonToPanel(botonesPanel, "destino");
            addButtonToPanel(botonesPanel, "instalacionResiduos");
            addButtonToPanel(botonesPanel, "permisoDestino");
            addButtonToPanel(botonesPanel, "registroDeResiduos");
            addButtonToPanel(botonesPanel, "residuo");
            addButtonToPanel(botonesPanel, "residuoConstituyente");
            addButtonToPanel(botonesPanel, "seguro");
            addButtonToPanel(botonesPanel, "traslado");
            addButtonToPanel(botonesPanel, "residuoTraslado");
            // Agrega más botones según sea necesario
        } else if ("tablas_de_asociacion".equals(tipoPanel)) {
            addButtonToPanel(botonesPanel, "certificacionesEmpresaTraslado");
            addButtonToPanel(botonesPanel, "certificadoConductor");
            addButtonToPanel(botonesPanel, "licenciaDestino");
            addButtonToPanel(botonesPanel, "licenciaEmpresaTransporte");
            addButtonToPanel(botonesPanel, "metodosResiduos");
            addButtonToPanel(botonesPanel, "metodoTratamientoDestino");
            addButtonToPanel(botonesPanel, "metodoTratamientoImpactoAmbiental");
            addButtonToPanel(botonesPanel, "metodoTratamientoRegulaciones");
            addButtonToPanel(botonesPanel, "residuoDisposicionesFinales");
            addButtonToPanel(botonesPanel, "residuoEmpresa");
        }

        // Agregar el sub-panel al centro del panel
        panel.add(botonesPanel, BorderLayout.CENTER);

        // Botón para regresar al panel principal
        JPanel regresarPanel = new JPanel();
        regresarPanel.setBackground(Color.LIGHT_GRAY);
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            frame.getContentPane().remove(panel); // Remover el panel actual
            frame.getContentPane().add(mainPanel); // Volver a mostrar el panel principal
            frame.revalidate(); // Revalidar el contenido del frame
            frame.repaint(); // Repintar el frame
        });
        regresarPanel.add(btnRegresar);

        // Agregar el panel de regreso al sur del panel
        panel.add(regresarPanel, BorderLayout.SOUTH);

        // Mostrar el panel
        frame.getContentPane().remove(mainPanel); // Remover el panel principal
        frame.getContentPane().add(panel); // Mostrar el nuevo panel
        frame.revalidate(); // Revalidar el contenido del frame
        frame.repaint(); // Repintar el frame
    }

    private static void addButtonToPanel(JPanel panel, String buttonText) {
        JButton button = new JButton(buttonText);
        button.setBackground(Color.WHITE);
        button.addActionListener(e -> ejecutarClaseEnMantenimiento(buttonText));
        panel.add(button);
    }

    private static void ejecutarClaseEnMantenimiento(String nombreClase) {
    	String paqueteBase = "";
        switch (currentPanelType) {
            case "tablas_de_referencia":
                paqueteBase = "tablas_de_referencia.";
                break;
            case "tablas_fundamentales":
                paqueteBase = "tablas_fundamentales.";
                break;
            case "tablas_de_asociacion":
                paqueteBase = "tablas_de_asociacion.";
                break;
        }

        try {
            Class<?> clase = Class.forName(paqueteBase + nombreClase);
            Method metodoMain = clase.getMethod("main", String[].class);
            metodoMain.invoke(null, (Object) new String[]{});
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
}
