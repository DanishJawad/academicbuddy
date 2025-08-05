package org.example.academicbuddy;

import javafx.stage.FileChooser;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class Controller {

    private TableView<SubjectEntry> subjectTable;
    private ObservableList<SubjectEntry> subjectEntries;
    private ObservableList<TaskEntry> taskList = FXCollections.observableArrayList();
    private ComboBox<String> semesterCombo;
    private Label gpaLabel;
    private Label cgpaLabel;
    private int studentId;

    public void launchUI(Stage stage, String username) {
        this.studentId = StudentsDAO.getStudentIdByName(username);

        // ✨ Welcome Section
        Label welcome = new Label("🎓 Welcome, " + username + "!");
        welcome.getStyleClass().add("title");
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // 🔐 Logout Button
        Button logoutBtn = styledButton("Logout", "#D32F2F");
        logoutBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    LoginScreen.show(stage);
                }
            });
        });

        cgpaLabel = new Label("CGPA: 0.00");
        gpaLabel = new Label("GPA: 0.00");
        cgpaLabel.setStyle("-fx-font-size: 14px;");
        gpaLabel.setStyle("-fx-font-size: 14px;");

        semesterCombo = new ComboBox<>();
        semesterCombo.setPromptText("📘 Select Semester");
        semesterCombo.setPrefWidth(200);

        List<String> semesters = SubjectsDAO.getSemestersForStudent(studentId);
        semesterCombo.getItems().addAll(semesters);
        semesterCombo.setOnAction(e -> loadSemesterData());

        subjectEntries = FXCollections.observableArrayList();
        subjectTable = new TableView<>(subjectEntries);
        subjectTable.setId("subject-table");
        subjectTable.setPlaceholder(new Label("No subjects found for selected semester."));
        subjectTable.setPrefHeight(350);

        subjectTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(SubjectEntry item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    String baseStyle = switch (item.getGrade()) {
                        case "A", "A-" -> "-fx-background-color: #e6ffe6;";
                        case "B+", "B" -> "-fx-background-color: #ffffe0;";
                        case "C", "D" -> "-fx-background-color: #fff0e0;";
                        case "F" -> "-fx-background-color: #ffe6e6;";
                        default -> "";
                    };

                    if (isSelected()) {
                        // Let JavaFX apply its default selection style
                        setStyle(null);
                    } else {
                        setStyle(baseStyle + "-fx-text-fill: black;");
                    }
                }
            }
        });


        subjectTable.getColumns().addAll(
                createColumn("Subject", "subjectName"),
                createColumn("Q1", "q1"), createColumn("Q2", "q2"),
                createColumn("Q3", "q3"), createColumn("Q4", "q4"),
                createColumn("A1", "a1"), createColumn("A2", "a2"),
                createColumn("A3", "a3"), createColumn("A4", "a4"),
                createColumn("Midterm", "midterm"),
                createColumn("Final", "finalExam"),
                createColumn("Total", "totalMarks"),
                createColumn("Grade", "grade"),
                createColumn("Credit Hours", "creditHours"),
                createColumn("GPA", "gpa")
        );

        // 🎛 Control Buttons
        Button addBtn = styledButton("➕ Add", "#4CAF50");
        Button updateBtn = styledButton("✏️ Update", "#2196F3");
        Button delBtn = styledButton("🗑 Delete", "#F44336");

        addBtn.setOnAction(e -> {
            AddSubjectDialog.show(studentId).ifPresent(entry -> {
                SubjectsDAO.insertSubjectForStudent(entry);
                if (entry.getSemester().equals(semesterCombo.getValue())) {
                    subjectEntries.add(entry);
                }
                refreshSemesterDropdown();
                updateCGPAandGPA();
            });
        });

        updateBtn.setOnAction(e -> {
            SubjectEntry selected = subjectTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                AddSubjectDialog.show(studentId, selected).ifPresent(updated -> {
                    selected.updateFrom(updated);
                    SubjectsDAO.updateSubjectForStudent(selected);
                    loadSemesterData();
                    refreshSemesterDropdown();
                    updateCGPAandGPA();
                });
            }
        });

        delBtn.setOnAction(e -> {
            SubjectEntry selected = subjectTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                SubjectsDAO.deleteSubjectForStudent(selected);
                subjectEntries.remove(selected);
                refreshSemesterDropdown();
                updateCGPAandGPA();
            }
        });

        Button exportSubjectsBtn = styledButton("Export Subjects", "#6A1B9A");
        Button importSubjectsBtn = styledButton("Import Subjects", "#8E24AA");

        exportSubjectsBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Subjects CSV");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            chooser.setInitialFileName("subjects.csv");
            java.io.File file = chooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    List<SubjectEntry> all = SubjectsDAO.getSubjectsForStudent(studentId);
                    CSVUtil.exportSubjects(all, file.toPath());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Subjects exported to " + file.getName());
                    alert.show();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Export failed: " + ex.getMessage()).show();
                }
            }
        });

        importSubjectsBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Import Subjects CSV");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            java.io.File file = chooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    List<SubjectEntry> imported = CSVUtil.importSubjects(studentId, file.toPath());
                    for (SubjectEntry s : imported) {
                        SubjectsDAO.insertSubjectForStudent(s);
                    }
                    loadSemesterData();
                    refreshSemesterDropdown();
                    updateCGPAandGPA();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Imported " + imported.size() + " subject(s).");
                    alert.show();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Import failed: " + ex.getMessage()).show();
                }
            }
        });

        // 🌙 Theme Toggle
        ToggleButton themeToggle = new ToggleButton("🌙 Dark Mode");
        themeToggle.setOnAction(e -> {
            Scene currentScene = themeToggle.getScene();
            if (themeToggle.isSelected()) {
                currentScene.getStylesheets().clear();
                currentScene.getStylesheets().add(getClass().getResource("/dark.css").toExternalForm());
                themeToggle.setText("☀️ Light Mode");
            } else {
                currentScene.getStylesheets().clear();
                currentScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                themeToggle.setText("🌙 Dark Mode");
            }
        });
        themeToggle.setStyle("-fx-background-color: #444; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topRow = new HBox(20, welcome, semesterCombo, spacer, logoutBtn);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setPadding(new Insets(10));

        HBox buttonBar = new HBox(10, addBtn, updateBtn, delBtn, exportSubjectsBtn, importSubjectsBtn);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10));

        HBox statusBar = new HBox(20, gpaLabel, cgpaLabel, themeToggle);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(10));

        VBox subjectLayout = new VBox(15, topRow, subjectTable, buttonBar, statusBar);
        subjectLayout.setPadding(new Insets(15));

        Tab subjectsTab = new Tab("📚 Subjects", subjectLayout);
        subjectsTab.setClosable(false);

        Tab gpaTrendsTab = new Tab("📈 GPA Trends");
        gpaTrendsTab.setClosable(false);

        Tab todoTab = createToDoTab();

        TabPane tabPane = new TabPane(subjectsTab, gpaTrendsTab, todoTab);
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == gpaTrendsTab) {
                GpaTrendsChart.show(studentId);
                tabPane.getSelectionModel().select(subjectsTab);
            }
        });

        Scene scene = new Scene(tabPane, 1200, 650);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("🎓 Academic Buddy");
        stage.setScene(scene);
        stage.show();
    }

    private Button styledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
        return btn;
    }

    private TableColumn<SubjectEntry, ?> createColumn(String title, String property) {
        double prefWidth = switch (property) {
            case "subjectName" -> 150;
            case "grade" -> 80;
            case "q1", "q2", "q3", "q4", "a1", "a2", "a3", "a4" -> 45;
            case "creditHours", "gpa" -> 70;
            default -> 75;
        };

        if (property.equals("grade") || property.equals("subjectName")) {
            TableColumn<SubjectEntry, String> col = new TableColumn<>(title);
            col.setCellValueFactory(cell -> cell.getValue().getStringProperty(property));
            col.setPrefWidth(prefWidth);
            return col;
        } else {
            TableColumn<SubjectEntry, Number> col = new TableColumn<>(title);
            col.setCellValueFactory(cell -> (ObservableValue<Number>) cell.getValue().getNumberProperty(property));
            col.setPrefWidth(prefWidth);
            return col;
        }
    }

    private void loadSemesterData() {
        String selected = semesterCombo.getValue();
        if (selected != null) {
            subjectEntries.setAll(SubjectsDAO.getSubjectsForStudentBySemester(studentId, selected));
            updateCGPAandGPA();
        }
    }

    private void updateCGPAandGPA() {
        double semesterPoints = 0, totalPoints = 0;
        int semesterCredits = 0, totalCredits = 0;

        for (SubjectEntry entry : subjectEntries) {
            semesterPoints += entry.getGpa() * entry.getCreditHours();
            semesterCredits += entry.getCreditHours();
        }

        List<SubjectEntry> allEntries = SubjectsDAO.getSubjectsForStudent(studentId);
        for (SubjectEntry entry : allEntries) {
            totalPoints += entry.getGpa() * entry.getCreditHours();
            totalCredits += entry.getCreditHours();
        }

        double semesterGPA = semesterCredits == 0 ? 0 : semesterPoints / semesterCredits;
        double cgpa = totalCredits == 0 ? 0 : totalPoints / totalCredits;

        gpaLabel.setText(String.format("GPA: %.2f", semesterGPA));
        cgpaLabel.setText(String.format("CGPA: %.2f", cgpa));
    }

    private void refreshSemesterDropdown() {
        List<String> semesters = SubjectsDAO.getSemestersForStudent(studentId);
        semesterCombo.getItems().setAll(semesters);
    }

    private Tab createToDoTab() {
        taskList = FXCollections.observableArrayList(TaskDAO.getTasksForStudent(studentId));
        TableView<TaskEntry> taskTable = new TableView<>(taskList);
        taskTable.setId("todo-table");

        TableColumn<TaskEntry, String> taskCol = new TableColumn<>("📝 Task");
        taskCol.setCellValueFactory(data -> data.getValue().taskProperty());

        TableColumn<TaskEntry, String> subjectCol = new TableColumn<>("📘 Subject");
        subjectCol.setCellValueFactory(data -> data.getValue().subjectProperty());

        TableColumn<TaskEntry, LocalDate> deadlineCol = new TableColumn<>("📅 Deadline");
        deadlineCol.setCellValueFactory(data -> data.getValue().deadlineProperty());

        TableColumn<TaskEntry, Boolean> doneCol = new TableColumn<>("✅ Done");
        doneCol.setCellValueFactory(data -> data.getValue().doneProperty());
        doneCol.setCellFactory(CheckBoxTableCell.forTableColumn(doneCol));
        doneCol.setEditable(true);

        doneCol.setCellFactory(col -> {
            CheckBoxTableCell<TaskEntry, Boolean> cell = new CheckBoxTableCell<>();
            cell.setSelectedStateCallback(index -> {
                TaskEntry task = taskTable.getItems().get(index);
                BooleanProperty prop = task.doneProperty();
                prop.addListener((obs, oldVal, newVal) -> {
                    task.setCompleted(newVal);
                    TaskDAO.updateTask(task);
                });
                return prop;
            });
            return cell;
        });

        taskTable.getColumns().addAll(taskCol, subjectCol, deadlineCol, doneCol);
        taskTable.setEditable(true);
        taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button addTaskBtn = styledButton("➕ Add Task", "#4CAF50");
        addTaskBtn.setOnAction(e -> {
            Dialog<TaskEntry> dialog = new TaskDialog(studentId);
            dialog.showAndWait().ifPresent(task -> {
                taskList.add(task);
                TaskDAO.insertTask(task);
            });
        });

        Button deleteTaskBtn = styledButton("🗑 Delete", "#F44336");
        deleteTaskBtn.setOnAction(e -> {
            TaskEntry selected = taskTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                taskList.remove(selected);
                TaskDAO.deleteTask(selected.getId());
            }
        });

        Button exportTasksBtn = styledButton("Export Tasks", "#0288D1");
        Button importTasksBtn = styledButton("Import Tasks", "#039BE5");

        exportTasksBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Tasks CSV");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            chooser.setInitialFileName("tasks.csv");
            java.io.File file = chooser.showSaveDialog(taskTable.getScene().getWindow());
            if (file != null) {
                try {
                    CSVUtil.exportTasks(taskList, file.toPath());
                    new Alert(Alert.AlertType.INFORMATION, "Tasks exported.").show();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Export failed: " + ex.getMessage()).show();
                }
            }
        });

        importTasksBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Import Tasks CSV");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            java.io.File file = chooser.showOpenDialog(taskTable.getScene().getWindow());
            if (file != null) {
                try {
                    List<TaskEntry> imported = CSVUtil.importTasks(studentId, file.toPath());
                    for (TaskEntry t : imported) {
                        TaskDAO.insertTask(t);
                        taskList.add(t);
                    }
                    new Alert(Alert.AlertType.INFORMATION, "Imported " + imported.size() + " task(s).").show();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Import failed: " + ex.getMessage()).show();
                }
            }
        });

        HBox buttons = new HBox(10, addTaskBtn, deleteTaskBtn, exportTasksBtn, importTasksBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        VBox layout = new VBox(15, taskTable, buttons);
        layout.setPadding(new Insets(15));

        Tab todoTab = new Tab("📋 To-Do List", layout);
        todoTab.setClosable(false);
        return todoTab;
    }
}
