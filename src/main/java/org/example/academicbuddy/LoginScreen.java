package org.example.academicbuddy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;

public class LoginScreen {

    public static void show(Stage stage) {
        // === Logo ===
        ImageView logo = new ImageView();
        URL logoUrl = LoginScreen.class.getResource("/logo.png");
        if (logoUrl != null) {
            logo.setImage(new Image(logoUrl.toExternalForm()));
            logo.setFitHeight(80);
            logo.setPreserveRatio(true);
        }

        Label title = new Label("Academic Buddy");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // === Form ===
        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button goToSignupBtn = new Button("Sign up");
        goToSignupBtn.setStyle("-fx-background-color: #000140; -fx-text-fill: white;");

        Label status = new Label();
        status.setStyle("-fx-text-fill: red;");

        VBox form = new VBox(10,
                userLabel, usernameField,
                passLabel, passwordField,
                loginBtn, goToSignupBtn,
                status
        );
        form.setPadding(new Insets(20));
        form.setMaxWidth(300);

        VBox loginBox = new VBox(15, logo, title, form);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(30));
        loginBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.65);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 6);"
        );

        // === Background ===
        StackPane root = new StackPane(loginBox);
        root.setAlignment(Pos.CENTER);

        URL bgUrl = LoginScreen.class.getResource("/bg.jpg");
        if (bgUrl != null) {
            BackgroundImage bgImage = new BackgroundImage(
                    new Image(bgUrl.toExternalForm(), true),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, false, true)
            );
            root.setBackground(new Background(bgImage));
        } else {
            root.setStyle("-fx-background-color: linear-gradient(to bottom, #ece9e6, #ffffff);"); // fallback
        }

        // === Actions ===
        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText().trim();
            if (StudentsDAO.validateLogin(user, pass)) {
                new Controller().launchUI(stage, user);
            } else {
                status.setText("❌ Invalid credentials");
            }
        });

        goToSignupBtn.setOnAction(e -> SignupScreen.show(stage));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login - Academic Buddy");

        // === Fullscreen ===
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }
}
