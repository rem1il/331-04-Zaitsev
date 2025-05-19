package module_02_zaytsev.src.ui;

import module_02_zaytsev.src.Db;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientUI extends JFrame {
    private final Db dbConnection;
    private JPanel partnerContainer;
    private JTabbedPane mainTabs;
    private final Font PRIMARY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Color BASE_COLOR = Color.decode("#FFFFFF");
    private final Color SECONDARY_COLOR = Color.decode("#F4E8D3");
    private final Color ACCENT_COLOR = Color.decode("#67BA80");

    public ClientUI(Db db) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}
        setTitle("Учет 'Мастер Пол'");
        setIconImage(Toolkit.getDefaultToolkit().getImage("module_02_zaytsev/images/icon.ico"));
        this.dbConnection = db;
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BASE_COLOR);

        JPanel header = buildHeader();
        add(header, BorderLayout.NORTH);

        mainTabs = new JTabbedPane();
        mainTabs.setFont(PRIMARY_FONT);
        mainTabs.setBackground(BASE_COLOR);
        add(mainTabs, BorderLayout.CENTER);

        partnerContainer = new JPanel();
        partnerContainer.setLayout(new GridLayout(0, 1, 10, 10));
        partnerContainer.setBackground(BASE_COLOR);
        JScrollPane partnerScroll = new JScrollPane(partnerContainer);
        partnerScroll.getViewport().setBackground(BASE_COLOR);
        partnerScroll.setBorder(BorderFactory.createEmptyBorder());
        partnerScroll.getVerticalScrollBar().setUnitIncrement(16);
        partnerScroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
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
        mainTabs.add("Партнёры", partnerScroll);
        JScrollPane transactionScroll = new JScrollPane(new TransactionUI(db));
        transactionScroll.getViewport().setBackground(BASE_COLOR);
        transactionScroll.setBorder(BorderFactory.createEmptyBorder());
        transactionScroll.getVerticalScrollBar().setUnitIncrement(16);
        transactionScroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
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
        mainTabs.addTab("История продаж", transactionScroll);
        mainTabs.addTab("Индивидуальные скидки", new PromoUI(db));

        loadPartnerData();
        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BASE_COLOR);

        JLabel logo = new JLabel(new ImageIcon(new ImageIcon("module_02_zaytsev/images/logo.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel logoWrapper = new JPanel();
        logoWrapper.setBackground(BASE_COLOR);
        logoWrapper.add(logo);
        header.add(logoWrapper);

        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(SECONDARY_COLOR);
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addBtn = createStyledButton("Добавить");
        JButton editBtn = createStyledButton("Редактировать");
        JButton deleteBtn = createStyledButton("Удалить");

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        header.add(actionPanel);

        addBtn.addActionListener(e -> {
            try {
                addPartner();
            } catch (Exception ex) {
                showStyledErrorDialog("Ошибка! Введите корректное число.", "Ошибка");
            }
        });
        editBtn.addActionListener(e -> {
            try {
                editPartner();
            } catch (Exception ex) {
                showStyledErrorDialog("Пожалуйста, введите объем продаж!", "Ошибка");
            }
        });
        deleteBtn.addActionListener(e -> {
            try {
                deletePartner();
            } catch (Exception ex) {
                showStyledErrorDialog("Ошибка! Проверьте данные.", "Ошибка");
            }
        });

        return header;
    }

    private JButton createStyledButton(String label) {
        JButton button = new JButton(label);
        button.setFont(PRIMARY_FONT);
        button.setBackground(Color.WHITE);
        button.setForeground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Нажмите, чтобы выполнить действие: " + label);

        return button;
    }

    private void loadPartnerData() {
        Connection conn = dbConnection.connect();
        if (conn == null) return;
        try {
            String sql = "SELECT p.partner_id, p.partner_type, p.name, p.director, p.phone, p.rating, r.discount_rate AS discount " +
                         "FROM partners p " +
                         "LEFT JOIN rate r ON p.name = r.name";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            partnerContainer.removeAll();
            while (rs.next()) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
                card.setBackground(SECONDARY_COLOR);
                card.setPreferredSize(new Dimension(850, 100));

                JLabel typeLabel = new JLabel(rs.getString("partner_type") + " | " + rs.getString("name"));
                typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                typeLabel.setForeground(Color.BLACK);

                JLabel directorLabel = new JLabel("<html><b>Директор:</b> " + rs.getString("director") + "</html>");
                directorLabel.setFont(PRIMARY_FONT);
                JLabel phoneLabel = new JLabel("<html><b>Телефон:</b> " + rs.getString("phone") + "</html>");
                phoneLabel.setFont(PRIMARY_FONT);
                JLabel ratingLabel = new JLabel("<html><b>Рейтинг:</b> " + rs.getInt("rating") + "</html>");
                ratingLabel.setFont(PRIMARY_FONT);
                ratingLabel.setForeground(ACCENT_COLOR);
                JLabel discountLabel = new JLabel("<html><b>Скидка:</b> " + rs.getInt("discount") + "%</html>");
                discountLabel.setFont(PRIMARY_FONT);
                discountLabel.setForeground(ACCENT_COLOR);

                JPanel textPanel = new JPanel(new GridLayout(4, 1));
                textPanel.setBackground(SECONDARY_COLOR);
                textPanel.add(typeLabel);
                textPanel.add(directorLabel);
                textPanel.add(phoneLabel);
                textPanel.add(discountLabel);

                card.add(textPanel, BorderLayout.WEST);
                card.add(ratingLabel, BorderLayout.EAST);

                partnerContainer.add(card);
            }
            partnerContainer.revalidate();
            partnerContainer.repaint();
            conn.close();
        } catch (Exception e) {
            System.out.println("Ошибка загрузки данных: " + e.getMessage());
        }
    }

    private void addPartner() {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBackground(BASE_COLOR);
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] partnerTypes = {"ООО", "ЗАО", "ИП", "ПАО", "Госкорпорация"};
        JComboBox<String> typeBox = new JComboBox<>(partnerTypes);
        typeBox.setFont(PRIMARY_FONT);
        typeBox.setBackground(Color.WHITE);

        JTextField nameField = new JTextField();
        JTextField directorField = new JTextField();
        JTextField phoneField = new JTextField();
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

        Component[] fields = {nameField, directorField, phoneField, ratingSpinner};
        for (Component field : fields) {
            field.setFont(PRIMARY_FONT);
            if (field instanceof JTextField) {
                ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        }

        form.add(new JLabel("Тип партнёра:"));
        form.add(typeBox);
        form.add(new JLabel("Название:"));
        form.add(nameField);
        form.add(new JLabel("Директор:"));
        form.add(directorField);
        form.add(new JLabel("Телефон:"));
        form.add(phoneField);
        form.add(new JLabel("Рейтинг:"));
        form.add(ratingSpinner);

        UIManager.put("Button.background", Color.WHITE);
        UIManager.put("Button.foreground", ACCENT_COLOR);
        UIManager.put("Button.font", PRIMARY_FONT);
        UIManager.put("Button.focus", new Color(0,0,0,0));
        UIManager.put("Button.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        int result = JOptionPane.showConfirmDialog(this, form, 
            "Добавление нового партнёра", JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);
        SwingUtilities.invokeLater(() -> {
            for (Window window : Window.getWindows()) {
                if (window instanceof JDialog) {
                    JDialog dialog = (JDialog) window;
                    if (dialog.isVisible()) {
                        styleDialogButtons(dialog.getContentPane());
                    }
                }
            }
        });

        if (result != JOptionPane.OK_OPTION) return;

        String partnerType = (String) typeBox.getSelectedItem();
        String name = nameField.getText().trim();
        String director = directorField.getText().trim();
        String phone = phoneField.getText().trim();
        int rating = (Integer) ratingSpinner.getValue();

        if (name.isEmpty() || director.isEmpty() || phone.isEmpty()) {
            showStyledErrorDialog("Все поля должны быть заполнены", "Ошибка");
            return;
        }

        Connection connection = dbConnection.connect();
        if (connection != null) {
            try {
                String sql = "INSERT INTO partners (partner_type, name, director, phone, rating) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, partnerType);
                stmt.setString(2, name);
                stmt.setString(3, director);
                stmt.setString(4, phone);
                stmt.setInt(5, rating);
                stmt.executeUpdate();
                loadPartnerData();
                connection.close();
            } catch (Exception e) {
                System.out.println("Ошибка добавления партнёра: " + e.getMessage());
            }
        }
    }

    private String selectPartner(String message) {
        Connection connection = dbConnection.connect();
        if (connection == null) return null;

        try {
            String sql = "SELECT name FROM partners";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            DefaultListModel<String> model = new DefaultListModel<>();
            while (rs.next()) {
                model.addElement(rs.getString("name"));
            }
            connection.close();

            JList<String> list = new JList<>(model);
            list.setFont(PRIMARY_FONT);
            list.setSelectionBackground(ACCENT_COLOR);
            list.setSelectionForeground(Color.WHITE);
            list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));
            scrollPane.setPreferredSize(new Dimension(300, 200));

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBackground(BASE_COLOR);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel headerLabel = new JLabel(message);
            headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            panel.add(headerLabel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            int option = JOptionPane.showConfirmDialog(this, panel, "Выбор партнёра", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            return (option == JOptionPane.OK_OPTION) ? list.getSelectedValue() : null;

        } catch (Exception e) {
            System.out.println("Ошибка выбора партнёра: " + e.getMessage());
            return null;
        }
    }

    private void editPartner() {
        String selectedPartner = selectPartner("Выберите партнёра для редактирования:");
        if (selectedPartner == null) return;

        String[] partnerTypes = {"ООО", "ЗАО", "ИП", "ПАО", "Госкорпорация"};
        JComboBox<String> typeBox = new JComboBox<>(partnerTypes);
        JOptionPane.showMessageDialog(this, typeBox, "Выберите новый тип партнёра", JOptionPane.QUESTION_MESSAGE);
        String partnerType = (String) typeBox.getSelectedItem();

        String name = JOptionPane.showInputDialog(this, "Введите новое название:");
        String director = JOptionPane.showInputDialog(this, "Введите имя директора:");
        String phone = JOptionPane.showInputDialog(this, "Введите новый телефон:");
        String ratingStr = JOptionPane.showInputDialog(this, "Введите новый рейтинг (от 1 до 10):");
        if (ratingStr == null || ratingStr.trim().isEmpty()) return;

        int rating = Integer.parseInt(ratingStr);

        Connection connection = dbConnection.connect();
        if (connection != null) {
            try {
                String sql = "UPDATE partners SET partner_type = ?, name = ?, director = ?, phone = ?, rating = ? WHERE name = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, partnerType);
                stmt.setString(2, name);
                stmt.setString(3, director);
                stmt.setString(4, phone);
                stmt.setInt(5, rating);
                stmt.setString(6, selectedPartner);
                stmt.executeUpdate();
                loadPartnerData();
                connection.close();
            } catch (Exception e) {
                System.out.println("Ошибка редактирования партнёра: " + e.getMessage());
            }
        }
    }

    private void deletePartner() {
        String selectedPartner = selectPartner("Выберите партнёра для удаления:");
        if (selectedPartner == null) return;

        Connection connection = dbConnection.connect();
        if (connection != null) {
            try {
                String sql = "DELETE FROM partners WHERE name = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, selectedPartner);
                stmt.executeUpdate();
                loadPartnerData();
                connection.close();
            } catch (Exception e) {
                System.out.println("Ошибка удаления партнёра: " + e.getMessage());
            }
        }
    }

    private void showStyledErrorDialog(String message, String title) {
        JDialog dialog = new JDialog(this, title, true);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BASE_COLOR);
        JLabel textLabel = new JLabel("<html><div style='font-family:Segoe UI;font-size:13px;color:#B00020;'><b>" + message + "</b></div></html>", SwingConstants.CENTER);
        textLabel.setFont(PRIMARY_FONT);
        panel.add(textLabel, BorderLayout.CENTER);
        JButton okButton = new JButton("OK");
        okButton.setFont(PRIMARY_FONT);
        okButton.setBackground(ACCENT_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(BASE_COLOR);
        btnPanel.add(okButton);
        panel.add(btnPanel, BorderLayout.SOUTH);
        dialog.setUndecorated(false);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void styleDialogButtons(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setFont(PRIMARY_FONT);
                btn.setBackground(Color.WHITE);
                btn.setForeground(ACCENT_COLOR);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR),
                    BorderFactory.createEmptyBorder(8, 18, 8, 18)
                ));
            } else if (comp instanceof Container) {
                styleDialogButtons((Container) comp);
            }
        }
    }
}
