package stockwise.com.model;

public record ActivityLog(
        int logId,
        int userId,
        String actionType,
        String actionDescription,
        String timestamp,
        int affectedRecordId
) {}