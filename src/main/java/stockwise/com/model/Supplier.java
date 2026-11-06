package stockwise.com.model;

public record Supplier(
        int supplierId,
        String supplierName,
        String contactPerson,
        String phone,
        String email,
        String status
) {}