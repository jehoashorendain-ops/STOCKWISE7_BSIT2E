package stockwise.com.ui;

import stockwise.com.dao.UserDAO;
import java.util.Scanner;

public class ConsoleUI {
    private final UserDAO userDao;
    private final Scanner scanner;

    public ConsoleUI(UserDAO userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("ConsoleUI initialized. Delegating to Authentication flow...");
        Authentication auth = new Authentication(userDao, scanner);
        auth.start();
    }
}