package stockwise.com.model;

public record Product(
        int productId,
        String productName,
        String category,
        double price,
        int quantity,
        int reorderLevel
) {}