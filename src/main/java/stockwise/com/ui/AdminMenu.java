package stockwise.com.ui;

import stockwise.com.dao.ActivityLogDAO;
import stockwise.com.dao.UserDAO;
import stockwise.com.model.ActivityLog;
import stockwise.com.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDAO userDao;
    private final ActivityLogDAO logDao;
    private final Scanner scanner;
    private final User currentUser;

    public AdminMenu(UserDAO userDao, Scanner scanner, User currentUser) {
        this.userDao = userDao;
        this.logDao = new ActivityLogDAO();
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== ADMIN DASHBOARD ==========");
            System.out.println("Logged in as: " + currentUser.fullName() + " (ID: " + currentUser.userId() + ")");
            System.out.println("[1] View All Users");
            System.out.println("[2] Approve Pending Account");
            System.out.println("[3] Update User Role / Status");
            System.out.println("[4] View System Audit Logs");
            System.out.println("[5] Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> approveUserAccount();
                case "3" -> updateUserRoleAndStatus();
                case "4" -> viewAuditLogsMenu();
                case "5" -> {
                    logDao.logAction(currentUser.userId(), "USER_LOGOUT",
                            "User " + currentUser.username() + " (ID: " + currentUser.userId() + ") logged out.");
                    running = false;
                    System.out.println("Logging out from Admin dashboard.");
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void viewAllUsers() {
        List<User> users = userDao.getAllUsers();
        System.out.println("\n+----+-----------------+----------------------+-----------------+----------+");
        System.out.printf("| %-2s | %-15s | %-20s | %-15s | %-8s |\n", "ID", "Username", "Full Name", "Role", "Status");
        System.out.println("+----+-----------------+----------------------+-----------------+----------+");
        for (User u : users) {
            System.out.printf("| %-2d | %-15s | %-20s | %-15s | %-8s |\n",
                    u.userId(), u.username(), u.fullName(), u.role(), u.status());
        }
        System.out.println("+----+-----------------+----------------------+-----------------+----------+");
    }

    private void approveUserAccount() {
        viewAllUsers();
        int targetId = readSafeInt("Enter User ID to approve (or 0 to cancel): ");
        if (targetId == 0) return;

        User targetUser = userDao.getUserById(targetId);
        if (targetUser == null) {
            System.out.println("❌ Error: User ID " + targetId + " does not exist.");
            return;
        }

        if (targetUser.status().equalsIgnoreCase("APPROVED")) {
            System.out.println("⚠️ Action Denied: User ID " + targetId + " (" + targetUser.username() + ") is ALREADY APPROVED.");
            return;
        }

        if (userDao.updateUserStatus(targetId, "APPROVED")) {
            System.out.println("✅ User ID " + targetId + " approved successfully.");

            logDao.logAction(currentUser.userId(), "USER_APPROVED",
                    "Admin " + currentUser.userId() + " approved account for User ID " + targetId);
        } else {
            System.out.println("❌ Failed to approve user.");
        }
    }

    private void updateUserRoleAndStatus() {
        viewAllUsers();
        int targetId = readSafeInt("Enter User ID to update (or 0 to cancel): ");
        if (targetId == 0) return;

        // Verify if user exists before asking for role/status
        User targetUser = userDao.getUserById(targetId);
        if (targetUser == null) {
            System.out.println("❌ Error: User ID " + targetId + " does not exist.");
            return;
        }

        // Guided Menu for Role Selection (Prevents invalid text/numbers)
        String newRole = "";
        while (true) {
            System.out.println("\nSelect New Role:");
            System.out.println("[1] ADMIN");
            System.out.println("[2] MANAGER");
            System.out.println("[3] INVENTORY_STAFF");
            System.out.println("[4] CASHIER");
            System.out.println("[5] CUSTOMER");
            System.out.print("Choose role option (1-5): ");

            String roleChoice = scanner.nextLine().trim();
            switch (roleChoice) {
                case "1" -> newRole = "ADMIN";
                case "2" -> newRole = "MANAGER";
                case "3" -> newRole = "INVENTORY_STAFF";
                case "4" -> newRole = "CASHIER";
                case "5" -> newRole = "CUSTOMER";
                default -> {
                    System.out.println("❌ Invalid choice. Please enter a number between 1 and 5.");
                    continue;
                }
            }
            break;
        }

        // Guided Menu for Status Selection
        String newStatus = "";
        while (true) {
            System.out.println("\nSelect New Status:");
            System.out.println("[1] APPROVED");
            System.out.println("[2] PENDING");
            System.out.println("[3] SUSPENDED");
            System.out.print("Choose status option (1-3): ");

            String statusChoice = scanner.nextLine().trim();
            switch (statusChoice) {
                case "1" -> newStatus = "APPROVED";
                case "2" -> newStatus = "PENDING";
                case "3" -> newStatus = "SUSPENDED";
                default -> {
                    System.out.println("❌ Invalid choice. Please enter a number between 1 and 3.");
                    continue;
                }
            }
            break;
        }

        if (userDao.updateUserRoleAndStatus(targetId, newRole, newStatus)) {
            System.out.println("✅ User ID " + targetId + " updated successfully (Role: " + newRole + ", Status: " + newStatus + ").");

            logDao.logAction(currentUser.userId(), "USER_UPDATED",
                    "Admin " + currentUser.userId() + " changed User " + targetId + " to " + newRole + " (" + newStatus + ")");
        } else {
            System.out.println("❌ Failed to update user in database.");
        }
    }

    private void viewAuditLogsMenu() {
        boolean inLogMenu = true;
        while (inLogMenu) {
            System.out.println("\n=== SYSTEM AUDIT TRAIL ===");
            System.out.println("[1] View All System Logs");
            System.out.println("[2] Filter Logs by User ID");
            System.out.println("[3] Clear Historical Logs");
            System.out.println("[4] Back to Admin Dashboard");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> renderLogTable(logDao.getAllLogs());
                case "2" -> {
                    int filterUserId = readSafeInt("Enter User ID to filter logs: ");
                    renderLogTable(logDao.getLogsByUserId(filterUserId));
                }
                case "3" -> {
                    System.out.print("Are you sure you want to permanently clear all historical logs? (YES/NO): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("YES")) {
                        if (logDao.clearLogs()) {
                            System.out.println("Historical logs cleared.");
                            logDao.logAction(currentUser.userId(), "LOGS_CLEARED",
                                    "Admin " + currentUser.userId() + " cleared historical audit logs.");
                        }
                    } else {
                        System.out.println("Clear operation cancelled.");
                    }
                }
                case "4" -> inLogMenu = false;
                default -> System.out.println("Invalid choice. Please enter 1-4.");
            }
        }
    }

    private void renderLogTable(List<ActivityLog> logs) {
        if (logs.isEmpty()) {
            System.out.println("\nNo audit log records found.");
            return;
        }

        System.out.println("\n-------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-8s | %-18s | %-35s | %-20s\n",
                "ID", "USER ID", "ACTION TYPE", "DESCRIPTION", "TIMESTAMP");
        System.out.println("-------------------------------------------------------------------------------------------------");

        for (ActivityLog log : logs) {
            String desc = log.description().length() > 35
                    ? log.description().substring(0, 32) + "..."
                    : log.description();

            System.out.printf("%-5d | %-8d | %-18s | %-35s | %-20s\n",
                    log.id(),
                    log.userId(),
                    log.actionType(),
                    desc,
                    log.timestamp());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    private int readSafeInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a numeric ID.");
            }
        }
    }
}