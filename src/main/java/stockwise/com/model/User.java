package stockwise.com.model;

public record User(
        int userId,
        String username,
        String password,
        String fullName,
        String role,
        String accountStatus
) {}