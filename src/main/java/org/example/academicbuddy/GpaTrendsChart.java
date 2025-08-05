package org.example.academicbuddy;

import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

import java.util.List;

public class GpaTrendsChart {

    public static void show(int studentId) {
        Stage stage = new Stage();
        stage.setTitle("GPA & CGPA Trend");

        // Fetch all subjects
        List<SubjectEntry> allSubjects = SubjectsDAO.getSubjectsForStudent(studentId);
        List<String> semesters = SubjectsDAO.getSemestersForStudent(studentId);

        // Line chart setup
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Semester");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("GPA");

        // Custom Y-axis scale: min = lowest GPA, max = 4, tick unit = 0.1
        double minGpa = allSubjects.stream()
                .mapToDouble(SubjectEntry::getGpa)
                .min().orElse(0.0);
        minGpa = Math.floor(minGpa * 10) / 10.0; // round down to nearest 0.1

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(minGpa);
        yAxis.setUpperBound(4.0);
        yAxis.setTickUnit(0.1);

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("GPA vs CGPA Over Time");

        XYChart.Series<String, Number> gpaSeries = new XYChart.Series<>();
        gpaSeries.setName("Semester GPA");

        XYChart.Series<String, Number> cgpaSeries = new XYChart.Series<>();
        cgpaSeries.setName("Cumulative CGPA");

        double totalPoints = 0;
        int totalCredits = 0;

        for (String semester : semesters) {
            List<SubjectEntry> semesterSubjects = SubjectsDAO.getSubjectsForStudentBySemester(studentId, semester);

            double semesterPoints = 0;
            int semesterCredits = 0;

            for (SubjectEntry entry : semesterSubjects) {
                semesterPoints += entry.getGpa() * entry.getCreditHours();
                semesterCredits += entry.getCreditHours();
            }

            double semesterGpa = semesterCredits == 0 ? 0 : semesterPoints / semesterCredits;

            totalPoints += semesterPoints;
            totalCredits += semesterCredits;
            double cgpa = totalCredits == 0 ? 0 : totalPoints / totalCredits;

            gpaSeries.getData().add(new XYChart.Data<>(semester, semesterGpa));
            cgpaSeries.getData().add(new XYChart.Data<>(semester, cgpa));
        }

        chart.getData().addAll(gpaSeries, cgpaSeries);

        Scene scene = new Scene(chart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

}
