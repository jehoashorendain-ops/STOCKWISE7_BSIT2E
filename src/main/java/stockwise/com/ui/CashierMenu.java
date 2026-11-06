package stockwise.com.ui;

import stockwise.com.dao.ActivityLogDAO;
import stockwise.com.dao.ProductDAO;
import stockwise.com.model.Product;
import stockwise.com.model.User;
import java.util.List;
import java.util.Scanner;

public class CashierMenu {
    private final ProductDAO productDao;
    private final ActivityLogDAO logDao;
    private final Scanner scanner;
    private final User currentUser;

    public CashierMenu(ProductDAO productDao, ActivityLogDAO logDao, Scanner scanner, User currentUser) {
        this.productDao = productDao;
        this.logDao = logDao;
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== CASHIER PORTAL ==========");
            System.out.println("Welcome, " + currentUser.fullName() + "!");
            System.out.println("[1] View Product Catalog & Prices");
            System.out.println("[2] Process Sale / Stock-Out Transaction");
            System.out.println("[3] Logout");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> viewProducts();
                case "2" -> processSale();
                case "3" -> {
                    running = false;
                    System.out.println("Logging out from Cashier portal.");
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void viewProducts() {
        List<Product> products = productDao.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("\nNo products available in the store.");
            return;
        }

        System.out.println("\n+----+----------------------+---------------+------------+----------+");
        System.out.printf("| %-2s | %-20s | %-13s | %-10s | %-8s |\n", "ID", "Product Name", "Category", "Price", "Stock");
        System.out.println("+----+----------------------+---------------+------------+----------+");
        for (Product p : products) {
            System.out.printf("| %-2d | %-20s | %-13s | $%-9.2f | %-8d |\n",
                    p.productId(), p.productName(), p.category(), p.price(), p.quantity());
        }
        System.out.println("+----+----------------------+---------------+------------+----------+");
    }

    private void processSale() {
        System.out.println("\n===== PROCESS SALE / STOCK-OUT =====");
        viewProducts();

        int productId = readIntInput("Enter Product ID to sell (or 0 to cancel): ");
        if (productId == 0) return;

        int qtyToSell = readIntInput("Enter quantity to sell: ");
        if (qtyToSell <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return;
        }

        List<Product> products = productDao.getAllProducts();
        Product targetProduct = products.stream()
                .filter(p -> p.productId() == productId)
                .findFirst()
                .orElse(null);

        if (targetProduct == null) {
            System.out.println("Product ID not found.");
            return;
        }

        if (targetProduct.quantity() < qtyToSell) {
            System.out.println("Insufficient stock! Available quantity: " + targetProduct.quantity());
            return;
        }

        int newQuantity = targetProduct.quantity() - qtyToSell;
        boolean updated = productDao.updateProductQuantity(productId, newQuantity);

        if (updated) {
            double totalAmount = targetProduct.price() * qtyToSell;
            System.out.printf("Sale successful! Total Amount: $%.2f\n", totalAmount);

            // Fixed: Passes all 4 required arguments (userId, actionType, description, affectedRecordId)
            logDao.logAction(
                    currentUser.userId(),
                    "SALE",
                    "Sold " + qtyToSell + " unit(s) of " + targetProduct.productName(),
                    targetProduct.productId()
            );
        } else {
            System.out.println("Failed to process sale transaction.");
        }
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}