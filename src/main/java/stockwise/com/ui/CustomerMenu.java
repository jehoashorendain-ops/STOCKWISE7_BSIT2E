package stockwise.com.ui;

import stockwise.com.dao.ProductDAO;
import stockwise.com.model.Product;
import stockwise.com.model.User;
import java.util.List;
import java.util.Scanner;

public class CustomerMenu {
    private final ProductDAO productDao;
    private final Scanner scanner;
    private final User currentUser;

    public CustomerMenu(ProductDAO productDao, Scanner scanner, User currentUser) {
        this.productDao = productDao;
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== CUSTOMER PORTAL ==========");
            System.out.println("Welcome, " + currentUser.fullName() + "!");
            System.out.println("[1] Browse Available Products & Stock");
            System.out.println("[2] Search Products by Name/Category");
            System.out.println("[3] Logout / Return");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> viewProducts();
                case "2" -> searchProducts();
                case "3" -> {
                    running = false;
                    System.out.println("Logging out from Customer portal.");
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void viewProducts() {
        List<Product> products = productDao.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("\nNo products available in the store right now.");
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

    private void searchProducts() {
        System.out.print("\nEnter keyword to search (Name or Category): ");
        String keyword = scanner.nextLine().trim().toLowerCase();
        List<Product> products = productDao.getAllProducts();

        List<Product> filtered = products.stream()
                .filter(p -> p.productName().toLowerCase().contains(keyword) || p.category().toLowerCase().contains(keyword))
                .toList();

        if (filtered.isEmpty()) {
            System.out.println("No matching products found.");
            return;
        }

        System.out.println("\n+----+----------------------+---------------+------------+----------+");
        System.out.printf("| %-2s | %-20s | %-13s | %-10s | %-8s |\n", "ID", "Product Name", "Category", "Price", "Stock");
        System.out.println("+----+----------------------+---------------+------------+----------+");
        for (Product p : filtered) {
            System.out.printf("| %-2d | %-20s | %-13s | $%-9.2f | %-8d |\n",
                    p.productId(), p.productName(), p.category(), p.price(), p.quantity());
        }
        System.out.println("+----+----------------------+---------------+------------+----------+");
    }
}