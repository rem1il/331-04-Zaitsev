package module_02_zaytsev.src.ui;

import module_02_zaytsev.src.Db;
import module_01_zaytsev.src.ImporterExcel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransactionUI extends JPanel {
    private final Db db;
    private JTable table;
    private DefaultTableModel tableModel;

    private final Font SEGOE_UI = new Font("Segoe UI", Font.PLAIN, 14);
    private final Color PRIMARY_COLOR = Color.decode("#FFFFFF");
    private final Color SECONDARY_COLOR = Color.decode("#F4E8D3");
    private final Color ACCENT_COLOR = Color.decode("#67BA80");

    public TransactionUI(Db db) {
        this.db = db;
        setLayout(new BorderLayout());
        setBackground(PRIMARY_COLOR);


        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Продукция", "Наименование партнёра", "Количество", "Дата продажи"});
        table = new JTable(tableModel);
        table.setFont(SEGOE_UI);
        table.setRowHeight(25);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);
  
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(PRIMARY_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
        add(scrollPane, BorderLayout.CENTER);

        JButton uploadButton = new JButton("Загрузить историю продаж");
        uploadButton.setFont(SEGOE_UI);
        uploadButton.setBackground(Color.WHITE);
        uploadButton.setForeground(ACCENT_COLOR);
        uploadButton.setFocusPainted(false);
        uploadButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        uploadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        uploadButton.addActionListener(e -> uploadExcelFile());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(SECONDARY_COLOR);
        buttonPanel.add(uploadButton);
        add(buttonPanel, BorderLayout.NORTH);

        loadSalesHistoryFromDB();
    }

    private void uploadExcelFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите файл с историей продаж");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            try (Connection connection = db.connect()) {
                if (connection != null) {
                    tableModel.setRowCount(0);

                    boolean success = ImporterExcel.importSalesHistory(connection, filePath);

                    if (success) {
                        loadSalesHistoryFromDB(); 
                        JOptionPane.showMessageDialog(this, "История продаж загружена", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showStyledErrorDialog("Ошибка: Некорректные данные в файле! Проверьте количество товаров и дату.", "Ошибка загрузки");
                    }
                } else {
                    showStyledErrorDialog("Ошибка подключения к базе", "Ошибка");
                }
            } catch (Exception e) {
                showStyledErrorDialog("Ошибка обработки файла: " + e.getMessage(), "Ошибка");
            }
        }
    }

    private void loadSalesHistoryFromDB() {
        tableModel.setRowCount(0);

        try (Connection connection = db.connect()) {
            if (connection == null) {
                showStyledErrorDialog("Ошибка подключения к базе данных!", "Ошибка");
                return;
            }

            String sql = "SELECT product_name, partner_name, quantity, sale_date FROM sales_history ORDER BY sale_date DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getString("product_name"),
                        rs.getString("partner_name"),
                        rs.getInt("quantity"),
                        rs.getDate("sale_date").toString()
                };
                tableModel.addRow(row);
            }

            tableModel.fireTableDataChanged();
            table.repaint();

        } catch (Exception e) {
            showStyledErrorDialog("Ошибка загрузки истории продаж: " + e.getMessage(), "Ошибка");
        }
    }

    private void showStyledErrorDialog(String message, String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        JLabel textLabel = new JLabel("<html><div style='font-family:Segoe UI;font-size:13px;color:#B00020;'><b>" + message + "</b></div></html>");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(textLabel, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.PLAIN_MESSAGE);
    }
}

