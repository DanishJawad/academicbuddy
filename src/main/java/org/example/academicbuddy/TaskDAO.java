package org.example.academicbuddy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public static void insertTask(TaskEntry task) {
        String sql = "INSERT INTO tasks (student_id, description, subject, due_date, completed) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, task.getStudentId());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getSubject());
            stmt.setDate(4, Date.valueOf(task.getDueDate()));
            stmt.setBoolean(5, task.isCompleted());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<TaskEntry> getTasksForStudent(int studentId) {
        List<TaskEntry> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE student_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TaskEntry task = new TaskEntry(
                        studentId,
                        rs.getString("description"),
                        rs.getString("subject"), // ← ADD this
                        rs.getDate("due_date").toLocalDate(),
                        rs.getBoolean("completed")
                );
                task.setId(rs.getInt("id")); // Optional if you're using id
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static void updateTask(TaskEntry task) {
        String sql = "UPDATE tasks SET description = ?, due_date = ?, completed = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getDescription());
            stmt.setDate(2, Date.valueOf(task.getDueDate()));
            stmt.setBoolean(3, task.isCompleted());
            stmt.setInt(4, task.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
