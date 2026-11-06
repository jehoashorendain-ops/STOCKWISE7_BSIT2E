package stockwise.com;

import stockwise.com.dao.DatabaseInitializer;
import stockwise.com.dao.UserDAO;
import stockwise.com.ui.Authentication;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Database & Tables first!
        DatabaseInitializer.initializeDatabase();

        // 2. Shared resources
        Scanner scanner = new Scanner(System.in);
        UserDAO userDao = new UserDAO();

        // 3. Start Authentication
        Authentication auth = new Authentication(userDao, scanner);
        auth.start();

        scanner.close();
    }
}