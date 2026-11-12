package stockwise.com.dao;

import stockwise.com.model.ActivityLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {

    /**
     * Inserts a new log entry using SQLite's built-in 24-hour CURRENT_TIMESTAMP (localtime).
     */
    public boolean logAction(int userId, String actionType, String description) {
        String sql = "INSERT INTO activity_logs (user_id, action_type, description, timestamp) " +
                "VALUES (?, ?, ?, datetime('now', 'localtime'))";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, actionType.toUpperCase());
            pstmt.setString(3, description);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error recording activity log: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns all recorded actions ordered by newest timestamp first.
     */
    public List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs ORDER BY timestamp DESC, id DESC";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                logs.add(new ActivityLog(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("action_type"),
                        rs.getString("description"),
                        rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching activity logs: " + e.getMessage());
        }
        return logs;
    }

    /**
     * Filters and returns logs for a specific user ID.
     */
    public List<ActivityLog> getLogsByUserId(int userId) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE user_id = ? ORDER BY timestamp DESC, id DESC";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new ActivityLog(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("action_type"),
                            rs.getString("description"),
                            rs.getString("timestamp")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering activity logs: " + e.getMessage());
        }
        return logs;
    }

    /**
     * Safely clears historical logs from the database.
     */
    public boolean clearLogs() {
        String sql = "DELETE FROM activity_logs";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error clearing activity logs: " + e.getMessage());
            return false;
        }
    }
}