package stockwise.com.model;

public record Category(
        int categoryId,
        String categoryName,
        String description
) {}