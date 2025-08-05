package org.example.academicbuddy;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;

public class TaskDialog extends Dialog<TaskEntry> {
    public TaskDialog(int studentid) {
        setTitle("New Task");

        // === Input Fields ===
        TextField taskField = new TextField();
        TextField subjectField = new TextField();
        DatePicker deadlinePicker = new DatePicker(LocalDate.now());

        // === Layout ===
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(10));
        grid.addRow(0, new Label("Task:"), taskField);
        grid.addRow(1, new Label("Subject:"), subjectField);
        grid.addRow(2, new Label("Deadline:"), deadlinePicker);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // === Result Converter ===
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new TaskEntry(
                        studentid, // <-- or just 'studentId' if you're inside Controller
                        taskField.getText().trim(),
                        subjectField.getText().trim(),
                        deadlinePicker.getValue(),
                        false
                );
            }
            return null;
        });

    }
}
