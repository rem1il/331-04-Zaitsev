package module_02_zaytsev.src;

import module_01_zaytsev.src.ImporterExcel;
import module_02_zaytsev.src.ui.ClientUI;
import java.sql.Connection;
import module_02_zaytsev.src.Db;

public class Main {
    public static void main(String[] args) {
        Connection connection = Db.connect();
        if (connection != null) {
            ImporterExcel.importExcel(connection);
            new ClientUI(new module_02_zaytsev.src.Db());
        }
    }
}

