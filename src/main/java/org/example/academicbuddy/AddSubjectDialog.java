package org.example.academicbuddy;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class AddSubjectDialog {

    public static Optional<SubjectEntry> show(int studentId) {
        return show(studentId, null);
    }

    public static Optional<SubjectEntry> show(int studentId, SubjectEntry existing) {
        Dialog<SubjectEntry> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Subject" : "Update Subject");

        ButtonType submitBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(15));

        TextField subjectField = new TextField();
        TextField[] quizFields = new TextField[4];
        TextField[] assignFields = new TextField[4];
        TextField midField = new TextField();
        TextField finalField = new TextField();
        TextField creditField = new TextField("3");
        ComboBox<String> semesterBox = new ComboBox<>();
        semesterBox.getItems().addAll("Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5");
        semesterBox.setValue("Semester 1");

        for (int i = 0; i < 4; i++) {
            quizFields[i] = new TextField();
            assignFields[i] = new TextField();
        }

        if (existing != null) {
            subjectField.setText(existing.getSubjectName());
            quizFields[0].setText(String.valueOf(existing.getQ1()));
            quizFields[1].setText(String.valueOf(existing.getQ2()));
            quizFields[2].setText(String.valueOf(existing.getQ3()));
            quizFields[3].setText(String.valueOf(existing.getQ4()));
            assignFields[0].setText(String.valueOf(existing.getA1()));
            assignFields[1].setText(String.valueOf(existing.getA2()));
            assignFields[2].setText(String.valueOf(existing.getA3()));
            assignFields[3].setText(String.valueOf(existing.getA4()));
            midField.setText(String.valueOf(existing.getMidterm()));
            finalField.setText(String.valueOf(existing.getFinalExam()));
            creditField.setText(String.valueOf(existing.getCreditHours()));
            semesterBox.setValue(existing.getSemester());
        }

        grid.add(new Label("Subject:"), 0, 0);
        grid.add(subjectField, 1, 0);

        for (int i = 0; i < 4; i++) {
            grid.add(new Label("Quiz " + (i + 1) + ":"), 0, i + 1);
            grid.add(quizFields[i], 1, i + 1);
        }

        for (int i = 0; i < 4; i++) {
            grid.add(new Label("Assignment " + (i + 1) + ":"), 2, i + 1);
            grid.add(assignFields[i], 3, i + 1);
        }

        grid.add(new Label("Midterm:"), 0, 6);
        grid.add(midField, 1, 6);
        grid.add(new Label("Final:"), 2, 6);
        grid.add(finalField, 3, 6);
        grid.add(new Label("Credit Hours:"), 0, 7);
        grid.add(creditField, 1, 7);
        grid.add(new Label("Semester:"), 2, 7);
        grid.add(semesterBox, 3, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitBtnType) {
                try {
                    String subject = subjectField.getText().trim();
                    int q1 = Integer.parseInt(quizFields[0].getText());
                    int q2 = Integer.parseInt(quizFields[1].getText());
                    int q3 = Integer.parseInt(quizFields[2].getText());
                    int q4 = Integer.parseInt(quizFields[3].getText());
                    int a1 = Integer.parseInt(assignFields[0].getText());
                    int a2 = Integer.parseInt(assignFields[1].getText());
                    int a3 = Integer.parseInt(assignFields[2].getText());
                    int a4 = Integer.parseInt(assignFields[3].getText());
                    int mid = Integer.parseInt(midField.getText());
                    int fin = Integer.parseInt(finalField.getText());
                    int credits = Integer.parseInt(creditField.getText());
                    String semester = semesterBox.getValue();

                    return new SubjectEntry(studentId, subject, q1, q2, q3, q4, a1, a2, a3, a4, mid, fin, credits, semester);
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Please enter valid numbers.").showAndWait();
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
