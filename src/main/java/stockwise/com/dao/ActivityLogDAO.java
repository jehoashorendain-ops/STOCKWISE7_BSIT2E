package stockwise.com.dao;

import stockwise.com.model.ActivityLog;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {

    /**
     * Records an activity log using a 24-hour military timestamp format (e.g., 2026-06-11 14:00:00).
     */
    public boolean logAction(int userId, String actionType, String actionDescription, int affectedRecordId) {
        String militaryTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String sql = "INSERT INTO Activity_Logs (user_id, action_type, action_description, timestamp, affected_record_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, actionType.toUpperCase());
            pstmt.setString(3, actionDescription);
            pstmt.setString(4, militaryTimestamp);
            pstmt.setInt(5, affectedRecordId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error recording activity log: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all activity logs ordered from newest to oldest.
     */
    public List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM Activity_Logs ORDER BY timestamp DESC, log_id DESC";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                logs.add(new ActivityLog(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action_type"),
                        rs.getString("action_description"),
                        rs.getString("timestamp"),
                        rs.getInt("affected_record_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching activity logs: " + e.getMessage());
        }
        return logs;
    }
}