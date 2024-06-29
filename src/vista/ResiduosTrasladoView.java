package vista;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.sql.*;

public class ResiduosTrasladoView extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public ResiduosTrasladoView() {
    	setTitle("Vista de Traslados de Residuos");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Configurar el TableRowSorter para la tabla
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadData();
        adjustColumnSizes(table);
    }

    private void loadData() {
        String url = "jdbc:mysql://localhost:3306/gestion_residuos?useSSL=false"; // Cambia la URL según tu configuración
        String username = "root"; // Cambia el nombre de usuario según tu configuración
        String password = "1234"; // Cambia la contraseña según tu configuración

//        String query = "SELECT * FROM vista_residuos_gestion";
        String query = "SELECT * FROM vista_residuos_traslado";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Añadir nombres de columnas al modelo de tabla
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            // Añadir filas al modelo de tabla
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos de la vista.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adjustColumnSizes(JTable table) {
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component c = table.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                // We've exceeded the maximum width, no need to check other rows
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ResiduosTrasladoView view = new ResiduosTrasladoView();
            view.setVisible(true);
        });
    }
}
