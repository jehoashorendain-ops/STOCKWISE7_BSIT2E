package stockwise.com.model;

public record PurchaseOrder(
        int poId,
        int supplierId,
        int userId,
        String orderDate,
        String status,
        double totalAmount
) {}