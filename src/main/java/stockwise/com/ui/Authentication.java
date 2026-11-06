package stockwise.com.ui;

import stockwise.com.dao.ActivityLogDAO;
import stockwise.com.dao.ProductDAO;
import stockwise.com.dao.UserDAO;
import stockwise.com.model.User;
import java.util.Scanner;

public class Authentication {
    private final UserDAO userDao;
    private final Scanner scanner;

    public Authentication(UserDAO userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== STOCKWISE AUTHENTICATION ===");
            System.out.println("[1] Login");
            System.out.println("[2] Register New Account");
            System.out.println("[3] Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> handleRegistration();
                case "3" -> {
                    running = false;
                    System.out.println("Exiting StockWise. Goodbye!");
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDao.authenticate(username, password);

        if (user != null) {
            System.out.println("\nLogin successful! Welcome, " + user.fullName() + " (" + user.role() + ")");

            // Automatically record login action with 24-hour military timestamp
            ActivityLogDAO logDao = new ActivityLogDAO();
            logDao.logAction(user.userId(), "USER_LOGIN", "User logged into the system", user.userId());

            routeToRoleMenu(user);
        } else {
            System.out.println("Invalid username/password or account is still PENDING approval.");
        }
    }

    private void handleRegistration() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty. Registration cancelled.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (password.isEmpty()) {
            System.out.println("Password cannot be empty. Registration cancelled.");
            return;
        }

        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine().trim();
        if (fullName.isEmpty()) {
            System.out.println("Full name cannot be empty. Registration cancelled.");
            return;
        }

        String role = "";
        while (true) {
            System.out.println("\nSelect Role:");
            System.out.println("[1] ADMIN");
            System.out.println("[2] MANAGER");
            System.out.println("[3] INVENTORY_STAFF");
            System.out.println("[4] CASHIER");
            System.out.println("[5] CUSTOMER");
            System.out.print("Choose role option (1-5): ");

            String roleChoice = scanner.nextLine().trim();

            switch (roleChoice) {
                case "1" -> role = "ADMIN";
                case "2" -> role = "MANAGER";
                case "3" -> role = "INVENTORY_STAFF";
                case "4" -> role = "CASHIER";
                case "5" -> role = "CUSTOMER";
                default -> {
                    System.out.println("❌ Invalid choice (" + roleChoice + "). Please enter a number between 1 and 5.");
                    continue;
                }
            }
            break;
        }

        boolean success = userDao.registerUser(username, password, fullName, role, "PENDING");
        if (success) {
            System.out.println("Registration successful! Your account is PENDING admin approval.");
        } else {
            System.out.println("Registration failed. Username may already exist.");
        }
    }

    private void routeToRoleMenu(User user) {
        System.out.println("Redirecting to " + user.role() + " dashboard...");
        switch (user.role().toUpperCase()) {
            case "ADMIN" -> {
                AdminMenu adminMenu = new AdminMenu(userDao, scanner, user);
                adminMenu.show();
            }
            case "MANAGER", "INVENTORY_STAFF" -> {
                ProductDAO productDao = new ProductDAO();
                InventoryMenu invMenu = new InventoryMenu(productDao, scanner);
                invMenu.show();
            }
            case "CUSTOMER" -> {
                ProductDAO productDao = new ProductDAO();
                CustomerMenu custMenu = new CustomerMenu(productDao, scanner, user);
                custMenu.show();
            }
            case "CASHIER" -> {
                ProductDAO productDao = new ProductDAO();
                ActivityLogDAO logDao = new ActivityLogDAO();
                CashierMenu cashierMenu = new CashierMenu(productDao, logDao, scanner, user);
                cashierMenu.show();
            }
            default -> System.out.println("Role dashboard under development.");
        }
    }
}