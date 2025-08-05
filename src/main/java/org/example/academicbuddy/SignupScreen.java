package org.example.academicbuddy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SignupScreen {

    public static void show(Stage stage) {
        // === Logo & Title ===
        ImageView logo = new ImageView(new Image(SignupScreen.class.getResourceAsStream("/logo.png")));
        logo.setFitHeight(80);
        logo.setPreserveRatio(true);

        Label title = new Label("Academic Buddy");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // === Fields ===
        Label nameLabel = new Label("Username:");
        TextField nameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Label ageLabel = new Label("Age:");
        TextField ageField = new TextField();

        Label departmentLabel = new Label("Department:");
        TextField deptField = new TextField();

        Button signupBtn = new Button("Create Account");
        signupBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button backBtn = new Button("Back to Login");
        backBtn.setStyle("-fx-background-color: #000140; -fx-text-fill: white;");

        Label status = new Label();
        status.setStyle("-fx-text-fill: red;");

        VBox form = new VBox(10,
                nameLabel, nameField,
                passwordLabel, passwordField,
                ageLabel, ageField,
                departmentLabel, deptField,
                signupBtn, backBtn,
                status
        );
        form.setMaxWidth(300);
        form.setPadding(new Insets(20));

        VBox signupBox = new VBox(15, logo, title, form);
        signupBox.setAlignment(Pos.CENTER);
        signupBox.setPadding(new Insets(30));
        signupBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.65);" + // More transparent
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 6);"
        );

        // === Background ===
        BackgroundImage bg = new BackgroundImage(
                new Image(SignupScreen.class.getResource("/bg.jpg").toExternalForm(), true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );

        StackPane root = new StackPane(signupBox);
        root.setBackground(new Background(bg));
        root.setAlignment(Pos.CENTER);

        // === Actions ===
        signupBtn.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String password = passwordField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String dept = deptField.getText().trim();

                if (!name.isEmpty() && !password.isEmpty()) {
                    boolean created = StudentsDAO.registerStudent(name, password, age, dept);
                    if (created) {
                        new Alert(Alert.AlertType.INFORMATION, "Account created!").showAndWait();
                        LoginScreen.show(stage);
                    } else {
                        new Alert(Alert.AlertType.ERROR, "User already exists!").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Please fill all fields").show();
                }
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Age must be a number").show();
            }
        });

        backBtn.setOnAction(e -> LoginScreen.show(stage));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Signup - Academic Buddy");

        // === Fullscreen ===
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

        stage.show();
    }
}
