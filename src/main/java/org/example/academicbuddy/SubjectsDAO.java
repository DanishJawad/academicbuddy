package org.example.academicbuddy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectsDAO {

    public static void insertSubjectForStudent(SubjectEntry entry) {
        String sql = "INSERT INTO subjects (student_id, subject_name, q1, q2, q3, q4, a1, a2, a3, a4, midterm, final, credit_hours, semester) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entry.getStudentId());
            stmt.setString(2, entry.getSubjectName());
            stmt.setInt(3, entry.getQ1());
            stmt.setInt(4, entry.getQ2());
            stmt.setInt(5, entry.getQ3());
            stmt.setInt(6, entry.getQ4());
            stmt.setInt(7, entry.getA1());
            stmt.setInt(8, entry.getA2());
            stmt.setInt(9, entry.getA3());
            stmt.setInt(10, entry.getA4());
            stmt.setInt(11, entry.getMidterm());
            stmt.setInt(12, entry.getFinalExam());
            stmt.setInt(13, entry.getCreditHours());
            stmt.setString(14, entry.getSemester());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateSubjectForStudent(SubjectEntry entry) {
        String sql = "UPDATE subjects SET subject_name=?, q1=?, q2=?, q3=?, q4=?, a1=?, a2=?, a3=?, a4=?, midterm=?, final=?, credit_hours=?, semester=? " +
                "WHERE student_id=? AND subject_name=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entry.getSubjectName());
            stmt.setInt(2, entry.getQ1());
            stmt.setInt(3, entry.getQ2());
            stmt.setInt(4, entry.getQ3());
            stmt.setInt(5, entry.getQ4());
            stmt.setInt(6, entry.getA1());
            stmt.setInt(7, entry.getA2());
            stmt.setInt(8, entry.getA3());
            stmt.setInt(9, entry.getA4());
            stmt.setInt(10, entry.getMidterm());
            stmt.setInt(11, entry.getFinalExam());
            stmt.setInt(12, entry.getCreditHours());
            stmt.setString(13, entry.getSemester());
            stmt.setInt(14, entry.getStudentId());
            stmt.setString(15, entry.getSubjectName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSubjectForStudent(SubjectEntry entry) {
        String sql = "DELETE FROM subjects WHERE student_id = ? AND subject_name = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entry.getStudentId());
            stmt.setString(2, entry.getSubjectName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<SubjectEntry> getSubjectsForStudent(int studentId) {
        List<SubjectEntry> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE student_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subjects.add(mapRowToSubjectEntry(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjects;
    }

    public static List<String> getSemestersForStudent(int studentId) {
        List<String> semesters = new ArrayList<>();
        String sql = "SELECT DISTINCT semester FROM subjects WHERE student_id = ? ORDER BY semester";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                semesters.add(rs.getString("semester"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return semesters;
    }

    public static List<SubjectEntry> getSubjectsForStudentBySemester(int studentId, String semester) {
        List<SubjectEntry> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE student_id = ? AND semester = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setString(2, semester);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subjects.add(mapRowToSubjectEntry(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjects;
    }

    private static SubjectEntry mapRowToSubjectEntry(ResultSet rs) throws SQLException {
        SubjectEntry entry = new SubjectEntry(
                rs.getInt("student_id"),
                rs.getString("subject_name"),
                rs.getInt("q1"),
                rs.getInt("q2"),
                rs.getInt("q3"),
                rs.getInt("q4"),
                rs.getInt("a1"),
                rs.getInt("a2"),
                rs.getInt("a3"),
                rs.getInt("a4"),
                rs.getInt("midterm"),
                rs.getInt("final"),
                rs.getInt("credit_hours"),
                rs.getString("semester")
        );
        entry.calculateTotalAndGrade();
        return entry;
    }
}
