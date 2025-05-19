package module_01_zaytsev.src;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Random;

public class ImporterExcel {
    public static void importExcel(Connection connection) {
        String filePath = "module_01_zaytsev/import_data/partners_import.xlsx";

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            String sql = "INSERT INTO partners (partner_type, name, director, email, phone, legal_address, inn, rating, registration_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            Random random = new Random();
            LocalDate today = LocalDate.now();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String partnerType = getCellValue(row.getCell(0));
                String name = getCellValue(row.getCell(1));
                String director = getCellValue(row.getCell(2));
                String email = getCellValue(row.getCell(3));
                String phone = getCellValue(row.getCell(4));
                String legalAddress = getCellValue(row.getCell(5));
                String inn = getCellValue(row.getCell(6));
                String ratingStr = getCellValue(row.getCell(7));

                if (partnerType.isEmpty()) partnerType = "Не указан";
                if (ratingStr.isEmpty()) ratingStr = "0";

                int rating = Integer.parseInt(ratingStr);

                LocalDate registrationDate;
                if (row.getCell(8) == null || row.getCell(8).getCellType() == CellType.BLANK) {
                    int year = random.nextInt(today.getYear() - 2015 + 1) + 2015;
                    int month = random.nextInt(12) + 1;
                    int day = random.nextInt(28) + 1;
                    registrationDate = LocalDate.of(year, month, day);
                } else {
                    registrationDate = today;
                }

                preparedStatement.setString(1, partnerType);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, director);
                preparedStatement.setString(4, email);
                preparedStatement.setString(5, phone);
                preparedStatement.setString(6, legalAddress);
                preparedStatement.setString(7, inn);
                preparedStatement.setInt(8, rating);
                preparedStatement.setDate(9, Date.valueOf(registrationDate));
                preparedStatement.executeUpdate();
            }

            System.out.println("Данные загружены правильно");

        } catch (Exception e) {
            System.out.println("Ошибка загрузки" + e.getMessage());
        }
    }

    public static boolean importSalesHistory(Connection connection, String filePath) {
        boolean success = true;
        boolean duplicateWarningShown = false;

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            String sql = "INSERT INTO sales_history (product_name, partner_name, quantity, sale_date) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String productName = getCellValue(row.getCell(0));
                String partnerName = getCellValue(row.getCell(1));
                String quantityStr = getCellValue(row.getCell(2));
                String saleDateStr = getCellValue(row.getCell(3));

                if (productName.isEmpty() || partnerName.isEmpty() || quantityStr.isEmpty() || saleDateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ошибка: В файле есть пустые значения!", "Ошибка загрузки", JOptionPane.ERROR_MESSAGE);
                    success = false;
                    break;
                }

                int quantity = Integer.parseInt(quantityStr);
                LocalDate saleDate = LocalDate.parse(saleDateStr);

                if (isDuplicate(connection, productName, partnerName, saleDate, quantity)) {
                    if (!duplicateWarningShown) {
                        duplicateWarningShown = true;
                    }
                    updateRecord(connection, productName, partnerName, quantity, saleDate);
                    continue;
                }

                preparedStatement.setString(1, productName);
                preparedStatement.setString(2, partnerName);
                preparedStatement.setInt(3, quantity);
                preparedStatement.setDate(4, Date.valueOf(saleDate));
                preparedStatement.executeUpdate();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки Excel: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            success = false;
        }

        return success;
    }

    private static boolean isDuplicate(Connection connection, String productName, String partnerName, LocalDate saleDate, int quantity) {
        try {
            String checkSql = "SELECT quantity FROM sales_history WHERE product_name = ? AND partner_name = ? AND sale_date = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, productName);
            checkStmt.setString(2, partnerName);
            checkStmt.setDate(3, Date.valueOf(saleDate));
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int existingQuantity = rs.getInt("quantity");
                return existingQuantity == quantity;
            }
        } catch (Exception e) {
            System.out.println("Ошибка проверки дубликатов: " + e.getMessage());
        }
        return false;
    }


    private static void updateRecord(Connection connection, String productName, String partnerName, int newQuantity, LocalDate saleDate) {
        try {

            String checkSql = "SELECT product_name, partner_name, quantity, sale_date FROM sales_history WHERE product_name = ? AND partner_name = ? AND sale_date = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, productName);
            checkStmt.setString(2, partnerName);
            checkStmt.setDate(3, Date.valueOf(saleDate));
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String existingProduct = rs.getString("product_name");
                String existingPartner = rs.getString("partner_name");
                int existingQuantity = rs.getInt("quantity");
                LocalDate existingDate = rs.getDate("sale_date").toLocalDate();

                if (!existingProduct.equals(productName) || !existingPartner.equals(partnerName) || existingQuantity != newQuantity || !existingDate.equals(saleDate)) {

                    String updateSql = "UPDATE sales_history SET product_name = ?, partner_name = ?, quantity = ?, sale_date = ? WHERE product_name = ? AND partner_name = ? AND sale_date = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                    updateStmt.setString(1, productName);
                    updateStmt.setString(2, partnerName);
                    updateStmt.setInt(3, newQuantity);
                    updateStmt.setDate(4, Date.valueOf(saleDate));
                    updateStmt.setString(5, existingProduct);
                    updateStmt.setString(6, existingPartner);
                    updateStmt.setDate(7, Date.valueOf(existingDate));
                    updateStmt.executeUpdate();

                    System.out.println("Обновлено: " + productName + " (" + partnerName + ") → " + newQuantity + " на " + saleDate);
                } else {
                    System.out.println("Без изменений: " + productName + " (" + partnerName + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка обновления записи: " + e.getMessage());
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
