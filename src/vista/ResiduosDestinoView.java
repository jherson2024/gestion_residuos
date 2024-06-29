package vista;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResiduosDestinoView extends JFrame {

    public ResiduosDestinoView() {
        setTitle("Vista de Residuos por Destino");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        List<DestinoData> destinosData = loadDestinosData();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        int panelCount = (int) Math.ceil(destinosData.size() / 6.0);
        for (int i = 0; i < panelCount; i++) {
            int start = i * 6;
            int end = Math.min(start + 6, destinosData.size());
            List<DestinoData> sublist = destinosData.subList(start, end);
            JPanel panel = new DestinosPanel(sublist);
            mainPanel.add(panel);
            panel.setBackground(Color.WHITE); // Opcional, para ver mejor el borde

            // Crear un borde negro
            Border blackBorder = BorderFactory.createLineBorder(Color.BLACK, 1); // El segundo argumento es el grosor del borde

            // Aplicar el borde al panel
            panel.setBorder(blackBorder);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    private List<DestinoData> loadDestinosData() {
        List<DestinoData> data = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/gestion_residuos?useSSL=false"; // Cambia la URL según tu configuración
        String username = "root"; // Cambia el nombre de usuario según tu configuración
        String password = "1234"; // Cambia la contraseña según tu configuración

        String query = "SELECT NombreDestino, DireccionDestino, Capacidad, CantidadResiduos, PorcentajeLleno FROM vista_residuos_destino";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                DestinoData destinoData = new DestinoData(
                        rs.getString("NombreDestino"),
                        rs.getString("DireccionDestino"),
                        rs.getDouble("Capacidad"),
                        rs.getDouble("CantidadResiduos"),
                        rs.getDouble("PorcentajeLleno")
                );
                data.add(destinoData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    class DestinosPanel extends JPanel {
        public DestinosPanel(List<DestinoData> data) {
            setLayout(new GridLayout(1, data.size(), 20, 20));
            for (DestinoData destino : data) {
                add(new DestinoPanel(destino));
            }
        }
    }

    class DestinoPanel extends JPanel {
        private DestinoData data;

        public DestinoPanel(DestinoData data) {
            this.data = data;
            setPreferredSize(new Dimension(200, 300));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int bottleWidth = 60;
            int bottleHeight = 200;
            int x = (getWidth() - bottleWidth) / 2;
            int y = 40;

            // Dibujar la botella
            g.drawRect(x, y, bottleWidth, bottleHeight);

            // Rellenar la botella en escala de grises
            int fillHeight = (int) (data.getCantidadResiduos() / data.getCapacidad() * bottleHeight);
            int fillY = y + bottleHeight - fillHeight;
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x + 1, fillY, bottleWidth - 1, fillHeight);

            // Dibujar el porcentaje y otros datos
            g.setColor(Color.BLACK);
            g.drawString(data.getNombreDestino(), 10, 20);
            g.drawString(String.format("Capacidad: %.2f", data.getCapacidad()), 10, 35);
            g.drawString(String.format("Residuos: %.2f", data.getCantidadResiduos()), 10, bottleHeight + y + 20);
            g.drawString(String.format("Lleno: %.2f%%", data.getPorcentajeLleno()), 10, bottleHeight + y + 35);
            g.drawString(data.getDireccionDestino(), 10, bottleHeight + y + 50);
        }
    }

    class DestinoData {
        private String nombreDestino;
        private String direccionDestino;
        private double capacidad;
        private double cantidadResiduos;
        private double porcentajeLleno;

        public DestinoData(String nombreDestino, String direccionDestino, double capacidad, double cantidadResiduos, double porcentajeLleno) {
            this.nombreDestino = nombreDestino;
            this.direccionDestino = direccionDestino;
            this.capacidad = capacidad;
            this.cantidadResiduos = cantidadResiduos;
            this.porcentajeLleno = porcentajeLleno;
        }

        public String getNombreDestino() {
            return nombreDestino;
        }

        public String getDireccionDestino() {
            return direccionDestino;
        }

        public double getCapacidad() {
            return capacidad;
        }

        public double getCantidadResiduos() {
            return cantidadResiduos;
        }

        public double getPorcentajeLleno() {
            return porcentajeLleno;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ResiduosDestinoView view = new ResiduosDestinoView();
            view.setVisible(true);
        });
    }
}