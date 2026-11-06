package stockwise.com.ui;

import stockwise.com.dao.ProductDAO;
import stockwise.com.model.Product;
import java.util.List;
import java.util.Scanner;

public class InventoryMenu {
    private final ProductDAO productDao;
    private final Scanner scanner;

    public InventoryMenu(ProductDAO productDao, Scanner scanner) {
        this.productDao = productDao;
        this.scanner = scanner;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n========== INVENTORY MANAGEMENT ==========");
            System.out.println("[1] View All Products");
            System.out.println("[2] Add New Product");
            System.out.println("[3] Logout / Return");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> viewProducts();
                case "2" -> addNewProduct();
                case "3" -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void viewProducts() {
        List<Product> products = productDao.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("\nNo products found in inventory.");
            return;
        }

        System.out.println("\n+----+----------------------+---------------+------------+----------+--------------+");
        System.out.printf("| %-2s | %-20s | %-13s | %-10s | %-8s | %-12s |\n", "ID", "Product Name", "Category", "Price", "Qty", "Reorder Lvl");
        System.out.println("+----+----------------------+---------------+------------+----------+--------------+");
        for (Product p : products) {
            System.out.printf("| %-2d | %-20s | %-13s | $%-9.2f | %-8d | %-12d |\n",
                    p.productId(), p.productName(), p.category(), p.price(), p.quantity(), p.reorderLevel());
        }
        System.out.println("+----+----------------------+---------------+------------+----------+--------------+");
    }

    private void addNewProduct() {
        System.out.println("\n===== ADD NEW PRODUCT =====");
        System.out.print("Enter product name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();
        System.out.print("Enter price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Enter initial quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter reorder level: ");
        int reorder = Integer.parseInt(scanner.nextLine().trim());

        boolean success = productDao.addProduct(name, category, price, qty, reorder);
        if (success) {
            System.out.println("Product added successfully!");
        } else {
            System.out.println("Failed to add product.");
        }
    }
}