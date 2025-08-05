package org.example.academicbuddy;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple CSV helper for subjects and tasks.
 * Assumes header row exists. Fields containing commas or quotes are quoted with double-quotes.
 */
public class CSVUtil {

    // Escape a value for CSV (quote if necessary)
    private static String escape(String val) {
        if (val == null) return "";
        String s = val.replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s + "\"";
        }
        return s;
    }

    // Parse a single CSV line into fields (handles quoted)
    public static List<String> parseLine(String line) throws IOException {
        List<String> result = new ArrayList<>();
        if (line == null || line.isEmpty()) return result;
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(ch);
                }
            } else {
                if (ch == '"') {
                    inQuotes = true;
                } else if (ch == ',') {
                    result.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(ch);
                }
            }
        }
        result.add(cur.toString());
        return result;
    }

    // Export subject entries to CSV
    public static void exportSubjects(List<SubjectEntry> entries, Path outFile) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(outFile)) {
            // header
            w.write("subjectName,q1,q2,q3,q4,a1,a2,a3,a4,midterm,finalExam,creditHours,semester");
            w.newLine();
            for (SubjectEntry s : entries) {
                String line = String.join(",",
                        escape(s.getSubjectName()),
                        String.valueOf(s.getQ1()),
                        String.valueOf(s.getQ2()),
                        String.valueOf(s.getQ3()),
                        String.valueOf(s.getQ4()),
                        String.valueOf(s.getA1()),
                        String.valueOf(s.getA2()),
                        String.valueOf(s.getA3()),
                        String.valueOf(s.getA4()),
                        String.valueOf(s.getMidterm()),
                        String.valueOf(s.getFinalExam()),
                        String.valueOf(s.getCreditHours()),
                        escape(s.getSemester())
                );
                w.write(line);
                w.newLine();
            }
        }
    }

    // Import subject entries from CSV: returns list, caller should insert via DAO
    public static List<SubjectEntry> importSubjects(int studentId, Path inFile) throws IOException {
        List<SubjectEntry> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(inFile)) {
            String header = r.readLine();
            if (header == null) return out;
            String line;
            while ((line = r.readLine()) != null) {
                List<String> cols = parseLine(line);
                if (cols.size() < 13) continue; // skip invalid
                try {
                    String subjectName = cols.get(0);
                    int q1 = Integer.parseInt(cols.get(1));
                    int q2 = Integer.parseInt(cols.get(2));
                    int q3 = Integer.parseInt(cols.get(3));
                    int q4 = Integer.parseInt(cols.get(4));
                    int a1 = Integer.parseInt(cols.get(5));
                    int a2 = Integer.parseInt(cols.get(6));
                    int a3 = Integer.parseInt(cols.get(7));
                    int a4 = Integer.parseInt(cols.get(8));
                    int midterm = Integer.parseInt(cols.get(9));
                    int finalExam = Integer.parseInt(cols.get(10));
                    int creditHours = Integer.parseInt(cols.get(11));
                    String semester = cols.get(12);
                    SubjectEntry entry = new SubjectEntry(studentId, subjectName, q1, q2, q3, q4,
                            a1, a2, a3, a4, midterm, finalExam, creditHours, semester);
                    out.add(entry);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return out;
    }

    // Export task entries to CSV
    public static void exportTasks(List<TaskEntry> tasks, Path outFile) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(outFile)) {
            w.write("description,subject,dueDate,completed");
            w.newLine();
            for (TaskEntry t : tasks) {
                String line = String.join(",",
                        escape(t.getDescription()),
                        escape(t.getSubject()),
                        t.getDueDate() != null ? t.getDueDate().toString() : "",
                        String.valueOf(t.isCompleted())
                );
                w.write(line);
                w.newLine();
            }
        }
    }

    // Import tasks from CSV
    public static List<TaskEntry> importTasks(int studentId, Path inFile) throws IOException {
        List<TaskEntry> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(inFile)) {
            String header = r.readLine();
            if (header == null) return out;
            String line;
            while ((line = r.readLine()) != null) {
                List<String> cols = parseLine(line);
                if (cols.size() < 4) continue;
                String description = cols.get(0);
                String subject = cols.get(1);
                LocalDate due = null;
                if (!cols.get(2).isBlank()) {
                    try {
                        due = LocalDate.parse(cols.get(2));
                    } catch (Exception ignored) {}
                }
                boolean completed = Boolean.parseBoolean(cols.get(3));
                TaskEntry task = new TaskEntry(studentId, description, subject, due, completed);
                out.add(task);
            }
        }
        return out;
    }
}
