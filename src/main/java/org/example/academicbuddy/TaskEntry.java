package org.example.academicbuddy;

import javafx.beans.property.*;

import java.time.LocalDate;

public class TaskEntry {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty subject = new SimpleStringProperty(); // ← Added
    private final ObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>();
    private final BooleanProperty completed = new SimpleBooleanProperty(false);
    private int studentId;

    public TaskEntry(int studentId, String description, String subject, LocalDate dueDate, boolean completed) {
        this.studentId = studentId;
        this.description.set(description);
        this.subject.set(subject); // ← Added
        this.dueDate.set(dueDate);
        this.completed.set(completed);
    }

    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int val) { id.set(val); }
    public IntegerProperty idProperty() { return id; }

    public int getStudentId() { return studentId; }

    public String getDescription() { return description.get(); }
    public void setDescription(String val) { description.set(val); }
    public StringProperty descriptionProperty() { return description; }

    public String getSubject() { return subject.get(); } // ← Added
    public void setSubject(String val) { subject.set(val); } // ← Added
    public StringProperty subjectProperty() { return subject; } // ← Added

    public LocalDate getDueDate() { return dueDate.get(); }
    public void setDueDate(LocalDate val) { dueDate.set(val); }
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }

    public boolean isCompleted() { return completed.get(); }
    public void setCompleted(boolean val) { completed.set(val); }
    public BooleanProperty completedProperty() { return completed; }
    public StringProperty taskProperty() {
        return description;
    }

    public ObjectProperty<LocalDate> deadlineProperty() {
        return dueDate;
    }

    public BooleanProperty doneProperty() {
        return completed;
    }

}
