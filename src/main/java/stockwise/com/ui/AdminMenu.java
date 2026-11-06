package stockwise.com.ui;

import stockwise.com.dao.UserDAO;
import stockwise.com.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDAO userDao;
    private final Scanner scanner;
    private final User currentAdmin;

    public AdminMenu(UserDAO userDao, Scanner scanner, User currentAdmin) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentAdmin = currentAdmin;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== ADMIN MANAGEMENT MENU ==========");
            System.out.println("[1] View All System Users");
            System.out.println("[2] View Pending Registrations");
            System.out.println("[3] Approve User Account");
            System.out.println("[4] Update User Role and Status");
            System.out.println("[5] Logout / Back to Main Menu");
            System.out.print("Select an option: ");

            int choice = readIntInput("");

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> viewPendingUsers();
                case 3 -> approveUser();
                case 4 -> updateUserRoleAndStatus();
                case 5 -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void printUserTable(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("\nNo users found.");
            return;
        }

        System.out.println("\n+------+-----------------+----------------------+-----------------+------------+");
        System.out.printf("| %-4s | %-15s | %-20s | %-15s | %-10s |\n", "ID", "Username", "Full Name", "Role", "Status");
        System.out.println("+------+-----------------+----------------------+-----------------+------------+");

        for (User u : users) {
            System.out.printf("| %-4d | %-15s | %-20s | %-15s | %-10s |\n",
                    u.userId(), u.username(), u.fullName(), u.role(), u.accountStatus());
        }
        System.out.println("+------+-----------------+----------------------+-----------------+------------+");
    }

    private void viewAllUsers() {
        System.out.println("\n===== ALL SYSTEM USERS =====");
        List<User> users = userDao.getAllUsers();
        printUserTable(users);
    }

    private void viewPendingUsers() {
        System.out.println("\n===== PENDING REGISTRATIONS =====");
        List<User> pending = userDao.getPendingUsers();
        printUserTable(pending);
    }

    private void approveUser() {
        System.out.println("\n===== APPROVE USER ACCOUNT =====");
        List<User> pending = userDao.getPendingUsers();

        if (pending.isEmpty()) {
            System.out.println("There are no pending users to approve.");
            return;
        }

        printUserTable(pending);

        int userId = readIntInput("\nEnter User ID to approve (or 0 to cancel): ");
        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        boolean success = userDao.approveUser(userId);
        if (success) {
            System.out.println("User ID " + userId + " has been APPROVED successfully!");
        } else {
            System.out.println("Failed to approve user.");
        }
    }

    private void updateUserRoleAndStatus() {
        System.out.println("\n===== UPDATE USER ROLE AND STATUS =====");
        List<User> users = userDao.getAllUsers();
        printUserTable(users);

        int targetUserId = readIntInput("\nEnter target User ID to update (or 0 to cancel): ");
        if (targetUserId == 0) {
            System.out.println("Update operation cancelled.");
            return;
        }

        // Self-Edit Restriction Guardrail
        if (targetUserId == currentAdmin.userId()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        String newRole = readValidRole();
        String newStatus = readValidStatus();

        boolean success = userDao.updateUserRoleAndStatus(targetUserId, newRole, newStatus);
        if (success) {
            System.out.println("Success: User ID " + targetUserId + " updated to Role: " + newRole + ", Status: " + newStatus);
        } else {
            System.out.println("Failed to update user.");
        }
    }

    // Input Validation Wrappers
    private int readIntInput(String prompt) {
        while (true) {
            if (!prompt.isEmpty()) {
                System.out.print(prompt);
            }
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input (letters/symbols detected). Please enter a valid number.");
                System.out.print("Try again: ");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (ADMIN, MANAGER, INVENTORY_STAFF, CASHIER, CUSTOMER): ");
            String role = scanner.nextLine().trim().toUpperCase();
            if (role.equals("ADMIN") || role.equals("MANAGER") || role.equals("INVENTORY_STAFF") ||
                    role.equals("CASHIER") || role.equals("CUSTOMER")) {
                return role;
            }
            System.out.println("Invalid role. Please enter a recognized system role.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED, PENDING): ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("Invalid status. Please enter APPROVED or PENDING.");
        }
    }
}