package org.example.academicbuddy;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

public class SubjectEntry {

    private final StringProperty subjectName = new SimpleStringProperty();
    private final IntegerProperty q1 = new SimpleIntegerProperty(0);
    private final IntegerProperty q2 = new SimpleIntegerProperty(0);
    private final IntegerProperty q3 = new SimpleIntegerProperty(0);
    private final IntegerProperty q4 = new SimpleIntegerProperty(0);
    private final IntegerProperty a1 = new SimpleIntegerProperty(0);
    private final IntegerProperty a2 = new SimpleIntegerProperty(0);
    private final IntegerProperty a3 = new SimpleIntegerProperty(0);
    private final IntegerProperty a4 = new SimpleIntegerProperty(0);
    private final IntegerProperty midterm = new SimpleIntegerProperty(0);
    private final IntegerProperty finalExam = new SimpleIntegerProperty(0);
    private final DoubleProperty totalMarks = new SimpleDoubleProperty(0);
    private final StringProperty grade = new SimpleStringProperty("F");
    private final DoubleProperty gpa = new SimpleDoubleProperty(0);
    private final IntegerProperty creditHours = new SimpleIntegerProperty(3);
    private final StringProperty semester = new SimpleStringProperty("Semester 1");

    private int studentId;

    public SubjectEntry(int studentId, String subjectName, int q1, int q2, int q3, int q4,
                        int a1, int a2, int a3, int a4, int midterm, int finalExam,
                        int creditHours, String semester) {
        this.studentId = studentId;
        this.subjectName.set(subjectName);
        this.q1.set(q1); this.q2.set(q2); this.q3.set(q3); this.q4.set(q4);
        this.a1.set(a1); this.a2.set(a2); this.a3.set(a3); this.a4.set(a4);
        this.midterm.set(midterm);
        this.finalExam.set(finalExam);
        this.creditHours.set(creditHours);
        this.semester.set(semester);
        calculateTotalAndGrade();
    }

    public void calculateTotalAndGrade() {
        double quizTotal = (q1.get() + q2.get() + q3.get() + q4.get()) / 40.0 * 15;
        double assignTotal = (a1.get() + a2.get() + a3.get() + a4.get()) / 40.0 * 10;
        double total = quizTotal + assignTotal + midterm.get() + finalExam.get();

        totalMarks.set(total);

        if (total >= 85) { grade.set("A"); gpa.set(4.0); }
        else if (total >= 80) { grade.set("A-"); gpa.set(3.67); }
        else if (total >= 75) { grade.set("B+"); gpa.set(3.33); }
        else if (total >= 70) { grade.set("B"); gpa.set(3.0); }
        else if (total >= 60) { grade.set("C"); gpa.set(2.67); }
        else if (total >= 50) { grade.set("D"); gpa.set(2.0); }
        else { grade.set("F"); gpa.set(0.0); }
    }

    // 🆕 Method to update this entry from another
    public void updateFrom(SubjectEntry other) {
        this.setQ1(other.getQ1());
        this.setQ2(other.getQ2());
        this.setQ3(other.getQ3());
        this.setQ4(other.getQ4());
        this.setA1(other.getA1());
        this.setA2(other.getA2());
        this.setA3(other.getA3());
        this.setA4(other.getA4());
        this.setMidterm(other.getMidterm());
        this.setFinalExam(other.getFinalExam());
        this.setCreditHours(other.getCreditHours());
        this.setSemester(other.getSemester());
        this.calculateTotalAndGrade();
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }

    public String getSubjectName() { return subjectName.get(); }
    public void setSubjectName(String name) { subjectName.set(name); }

    public int getQ1() { return q1.get(); }
    public void setQ1(int val) { q1.set(val); }

    public int getQ2() { return q2.get(); }
    public void setQ2(int val) { q2.set(val); }

    public int getQ3() { return q3.get(); }
    public void setQ3(int val) { q3.set(val); }

    public int getQ4() { return q4.get(); }
    public void setQ4(int val) { q4.set(val); }

    public int getA1() { return a1.get(); }
    public void setA1(int val) { a1.set(val); }

    public int getA2() { return a2.get(); }
    public void setA2(int val) { a2.set(val); }

    public int getA3() { return a3.get(); }
    public void setA3(int val) { a3.set(val); }

    public int getA4() { return a4.get(); }
    public void setA4(int val) { a4.set(val); }

    public int getMidterm() { return midterm.get(); }
    public void setMidterm(int val) { midterm.set(val); }

    public int getFinalExam() { return finalExam.get(); }
    public void setFinalExam(int val) { finalExam.set(val); }

    public double getTotalMarks() { return totalMarks.get(); }

    public String getGrade() { return grade.get(); }

    public double getGpa() { return gpa.get(); }

    public int getCreditHours() { return creditHours.get(); }
    public void setCreditHours(int val) { creditHours.set(val); }

    public String getSemester() { return semester.get(); }
    public void setSemester(String s) { semester.set(s); }

    public ObservableValue<Number> getNumberProperty(String property) {
        return switch (property) {
            case "q1" -> q1;
            case "q2" -> q2;
            case "q3" -> q3;
            case "q4" -> q4;
            case "a1" -> a1;
            case "a2" -> a2;
            case "a3" -> a3;
            case "a4" -> a4;
            case "midterm" -> midterm;
            case "finalExam" -> finalExam;
            case "totalMarks" -> totalMarks;
            case "gpa" -> gpa;
            case "creditHours" -> creditHours;
            default -> null;
        };
    }

    public ObservableValue<String> getStringProperty(String property) {
        return switch (property) {
            case "subjectName" -> subjectName;
            case "grade" -> grade;
            case "semester" -> semester;
            default -> null;
        };
    }
}
