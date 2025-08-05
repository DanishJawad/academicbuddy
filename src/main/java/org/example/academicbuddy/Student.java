package org.example.academicbuddy;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Student {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ListProperty<SubjectEntry> subjects =
            new SimpleListProperty<>(FXCollections.observableArrayList());

    public Student(int id, String name, List<SubjectEntry> subjects) {
        this.id.set(id);
        this.name.set(name);
        this.subjects.addAll(subjects);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObservableList<SubjectEntry> getSubjects() {
        return subjects.get();
    }

    public ListProperty<SubjectEntry> subjectsProperty() {
        return subjects;
    }

    public double getGPA() {
        if (subjects.isEmpty()) return 0;
        double total = 0;
        for (SubjectEntry s : subjects) {
            total += s.getTotalMarks(); // ✅ FIXED HERE
        }
        return total / subjects.size();
    }

    public String getGrade() {
        double gpa = getGPA();
        if (gpa >= 85) return "A";
        else if (gpa >= 80) return "A-";
        else if (gpa >= 75) return "B+";
        else if (gpa >= 70) return "B-";
        else if (gpa >= 60) return "C";
        else if (gpa >= 50) return "D";
        else return "F";
    }
}
