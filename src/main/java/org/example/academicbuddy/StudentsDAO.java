package org.example.academicbuddy;

import java.sql.*;

public class StudentsDAO {

    public static boolean registerStudent(String name, String password, int age, String dept) {
        String checkSql = "SELECT * FROM students WHERE name = ?";
        String insertSql = "INSERT INTO students (name, password, age, department) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return false; // User already exists
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, password);
                insertStmt.setInt(3, age);
                insertStmt.setString(4, dept);
                insertStmt.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean validateLogin(String name, String password) {
        String sql = "SELECT * FROM students WHERE name = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Login successful if a match is found

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getStudentIdByName(String name) {
        String sql = "SELECT id FROM students WHERE name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                System.err.println("❌ No student found with name: " + name);
            }
        } catch (SQLException e) {
            System.err.println("❌ DB error in getStudentIdByName(): " + e.getMessage());
        }
        return -1;  // Error case
    }
}
