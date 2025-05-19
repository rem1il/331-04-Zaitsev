package module_02_zaytsev.src.ui;

import module_02_zaytsev.src.Db;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PromoUI extends JPanel {
    private JComboBox<String> partnerDropdown, productDropdown;
    private JTextField totalSalesField;
    private JLabel discountLabel;
    private JButton calculateButton, updateButton;
    private final Font SEGOE_UI = new Font("Segoe UI", Font.PLAIN, 14);
    private final Color PRIMARY_COLOR = Color.decode("#FFFFFF");
    private final Color SECONDARY_COLOR = Color.decode("#F4E8D3");
    private final Color ACCENT_COLOR = Color.decode("#67BA80");

    public PromoUI(Db db) {
        setLayout(new BorderLayout());
        setBackground(PRIMARY_COLOR);

        JLabel titleLabel = new JLabel("Индивидуальные скидки", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PRIMARY_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 2, true),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        JLabel partnerLabel = createStyledLabel("Выберите партнёра:");
        gbc.gridx = 0;
        formPanel.add(partnerLabel, gbc);
        partnerDropdown = new JComboBox<>(loadPartnersFromDBWithEmpty());
        styleComboBoxModern(partnerDropdown);
        gbc.gridx = 1;
        formPanel.add(partnerDropdown, gbc);
        partnerDropdown.addActionListener(e -> updateProductDropdown());

        JLabel productLabel = createStyledLabel("Выберите продукцию:");
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(productLabel, gbc);
        productDropdown = new JComboBox<>();
        styleComboBoxModern(productDropdown);
        gbc.gridx = 1;
        formPanel.add(productDropdown, gbc);

        JLabel salesLabel = createStyledLabel("Общий объём продаж:");
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(salesLabel, gbc);
        totalSalesField = new JTextField();
        styleTextField(totalSalesField);
        gbc.gridx = 1;
        formPanel.add(totalSalesField, gbc);

        JLabel discountTextLabel = createStyledLabel("Индивидуальная скидка:");
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(discountTextLabel, gbc);
        discountLabel = createStyledLabel("0%");
        discountLabel.setForeground(ACCENT_COLOR);
        gbc.gridx = 1;
        formPanel.add(discountLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(PRIMARY_COLOR);
        calculateButton = createStyledButton("Рассчитать скидку");
        updateButton = createStyledButton("Обновить данные");
        calculateButton.addActionListener(e -> calculateDiscount());
        updateButton.addActionListener(e -> updateDatabase());
        buttonPanel.add(calculateButton);
        buttonPanel.add(updateButton);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(PRIMARY_COLOR);
        scrollPane.getViewport().setBackground(PRIMARY_COLOR);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ACCENT_COLOR;
                this.trackColor = SECONDARY_COLOR;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setVisible(false);
                return button;
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setVisible(false);
                return button;
            }
        });
        add(scrollPane, BorderLayout.CENTER);
    }

    private String[] loadPartnersFromDBWithEmpty() {
        ArrayList<String> partners = new ArrayList<>();
        partners.add(""); // Пустой элемент для выбора по умолчанию
        try (Connection conn = Db.connect()) {
            String sql = "SELECT DISTINCT partner_name FROM sales_history";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                partners.add(rs.getString("partner_name"));
            }
        } catch (Exception e) {
            showStyledErrorDialog("Ошибка загрузки партнёров: " + e.getMessage(), "Ошибка");
        }
        if (partners.size() == 1) {
            partners.add("Нет доступных партнёров");
        }
        return partners.toArray(new String[0]);
    }

    private void updateProductDropdown() {
        String selectedPartner = (String) partnerDropdown.getSelectedItem();
        if (selectedPartner == null || selectedPartner.equals("Нет доступных партнёров")) {
            return;
        }

        productDropdown.removeAllItems();

        try (Connection conn = Db.connect()) {
            String sql = "SELECT DISTINCT product_name FROM sales_history WHERE partner_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, selectedPartner);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                productDropdown.addItem(rs.getString("product_name"));
            }

            if (productDropdown.getItemCount() == 0) {
                productDropdown.addItem("Нет доступных товаров");
            }

        } catch (Exception e) {
            showStyledErrorDialog("Ошибка загрузки продукции: " + e.getMessage(), "Ошибка");
        }
    }

    private void calculateDiscount() {
        try {
            int totalSales = Integer.parseInt(totalSalesField.getText());
            float discount = calculateDiscountRate(totalSales);
            discountLabel.setText(discount + "%");
        } catch (NumberFormatException e) {
            showStyledErrorDialog("Ошибка! Введите корректное число.", "Ошибка");
        }
    }

    private float calculateDiscountRate(int totalSales) {
        if (totalSales < 10_000) return 0.0f;
        if (totalSales < 50_000) return 5.0f;
        if (totalSales < 300_000) return 10.0f;
        return 15.0f;
    }

    private void updateDatabase() {
        try (Connection conn = Db.connect()) {
            conn.setAutoCommit(false);
            String selectedPartner = (String) partnerDropdown.getSelectedItem();
            String selectedProduct = (String) productDropdown.getSelectedItem();
            
            String salesText = totalSalesField.getText().trim();
            if (salesText.isEmpty()) {
                showStyledErrorDialog("Пожалуйста, введите объем продаж", "Ошибка");
                return;
            }
            
            int totalSales;
            try {
                totalSales = Integer.parseInt(salesText);
            } catch (NumberFormatException e) {
                showStyledErrorDialog("Пожалуйста, введите корректное число", "Ошибка");
                return;
            }
            
            float discount = calculateDiscountRate(totalSales);

            String checkSql = "SELECT COUNT(*) FROM rate WHERE name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, selectedPartner);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                String updateSql = "UPDATE rate SET total_sales = ?, discount_rate = ? WHERE name = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, totalSales);
                updateStmt.setFloat(2, discount);
                updateStmt.setString(3, selectedPartner);
                updateStmt.executeUpdate();
                System.out.println("Данные обновлены!");
                conn.commit();
            } else {
                String insertSql = "INSERT INTO rate (name, total_sales, discount_rate) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, selectedPartner);
                insertStmt.setInt(2, totalSales);
                insertStmt.setFloat(3, discount);
                insertStmt.executeUpdate();
                System.out.println("Новый партнёр добавлен!");
                conn.commit();
            }
            JOptionPane.showMessageDialog(this, "Данные обновлены", "Успешно", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showStyledErrorDialog("Ошибка обновления данных: " + e.getMessage(), "Ошибка");
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SEGOE_UI);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(SEGOE_UI);
        button.setBackground(Color.WHITE);
        button.setForeground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void styleComboBoxModern(JComboBox<?> comboBox) {
        comboBox.setFont(SEGOE_UI);
        comboBox.setBackground(SECONDARY_COLOR);
        comboBox.setForeground(Color.BLACK);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 32)));
        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton arrowButton = new JButton();
                arrowButton.setPreferredSize(new Dimension(24, 24));
                arrowButton.setContentAreaFilled(false);
                arrowButton.setBorderPainted(false);
                arrowButton.setFocusPainted(false);
                arrowButton.setOpaque(false);
                arrowButton.setIcon(new ArrowIcon(ACCENT_COLOR));
                return arrowButton;
            }
        });
    }

    private static class ArrowIcon implements Icon {
        private final Color color;
        public ArrowIcon(Color color) { this.color = color; }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            int w = getIconWidth(), h = getIconHeight();
            int[] xs = {x + 4, x + w / 2, x + w - 4};
            int[] ys = {y + 8, y + h - 6, y + 8};
            g2.fillPolygon(xs, ys, 3);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 16; }
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(SEGOE_UI);
        textField.setBackground(PRIMARY_COLOR);
        textField.setForeground(Color.BLACK);
        textField.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
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
