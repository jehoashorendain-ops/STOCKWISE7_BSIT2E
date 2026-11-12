package stockwise.com.ui;

import stockwise.com.dao.ActivityLogDAO;
import stockwise.com.dao.ProductDAO;
import stockwise.com.model.Product;
import stockwise.com.model.User;
import java.util.List;
import java.util.Scanner;

public class InventoryMenu {
    private final ProductDAO productDao;
    private final ActivityLogDAO logDao;
    private final Scanner scanner;
    private final User currentUser;

    public InventoryMenu(ProductDAO productDao, ActivityLogDAO logDao, Scanner scanner, User currentUser) {
        this.productDao = productDao;
        this.logDao = logDao;
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== INVENTORY PORTAL ==========");
            System.out.println("Welcome, " + currentUser.fullName() + " (ID: " + currentUser.userId() + ")!");
            System.out.println("[1] View Inventory Catalog");
            System.out.println("[2] Add New Product");
            System.out.println("[3] Update Stock / Restock Item");
            System.out.println("[4] Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> viewInventory();
                case "2" -> addNewProduct();
                case "3" -> updateStock();
                case "4" -> {
                    // Deliverable 8 Trigger 1: Logout Logging
                    logDao.logAction(currentUser.userId(), "USER_LOGOUT",
                            "User " + currentUser.username() + " (ID: " + currentUser.userId() + ") logged out.");
                    running = false;
                    System.out.println("Logging out from Inventory portal.");
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void viewInventory() {
        List<Product> products = productDao.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("\nNo inventory records found.");
            return;
        }

        System.out.println("\n+----+----------------------+---------------+------------+----------+---------------+");
        System.out.printf("| %-2s | %-20s | %-13s | %-10s | %-8s | %-13s |\n", "ID", "Product Name", "Category", "Price", "Stock", "Reorder Level");
        System.out.println("+----+----------------------+---------------+------------+----------+---------------+");
        for (Product p : products) {
            System.out.printf("| %-2d | %-20s | %-13s | P%-9.2f | %-8d | %-13d |\n",
                    p.productId(), p.productName(), p.category(), p.price(), p.quantity(), p.reorderLevel());
        }
        System.out.println("+----+----------------------+---------------+------------+----------+---------------+");
    }

    private void addNewProduct() {
        System.out.println("\n===== ADD NEW PRODUCT =====");
        System.out.print("Enter product name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Product name cannot be empty.");
            return;
        }

        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();

        double price = readDoubleInput("Enter unit price (P): ");
        int quantity = readIntInput("Enter initial stock quantity: ");
        int reorderLevel = readIntInput("Enter reorder threshold level: ");

        boolean added = productDao.addProduct(name, category, price, quantity, reorderLevel);
        if (added) {
            System.out.println("Product added successfully!");
            // Deliverable 8 Business Process Log
            logDao.logAction(currentUser.userId(), "PRODUCT_ADDED",
                    "Added new product: " + name + " (Qty: " + quantity + ", Price: P" + price + ")");
        } else {
            System.out.println("Failed to add product.");
        }
    }

    private void updateStock() {
        System.out.println("\n===== UPDATE STOCK / RESTOCK =====");
        viewInventory();

        int productId = readIntInput("Enter Product ID to update (or 0 to cancel): ");
        if (productId == 0) return;

        int newQuantity = readIntInput("Enter new stock quantity: ");
        if (newQuantity < 0) {
            System.out.println("Stock quantity cannot be negative.");
            return;
        }

        boolean updated = productDao.updateProductQuantity(productId, newQuantity);
        if (updated) {
            System.out.println("Stock updated successfully!");
            // Deliverable 8 Trigger 4: Lifecycle / State Change Auto-Log
            logDao.logAction(currentUser.userId(), "STOCK_UPDATED",
                    "Updated Product #" + productId + " stock level to " + newQuantity);
        } else {
            System.out.println("Failed to update stock. Verify Product ID.");
        }
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    private double readDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numerical price.");
            }
        }
    }
}