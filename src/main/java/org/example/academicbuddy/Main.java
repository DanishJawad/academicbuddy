package org.example.academicbuddy;

import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {

    public static void main(String[] args) {
        try {
            Connection conn = DBUtil.getConnection();
            System.out.println("✅ Connected to MySQL!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }

        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        LoginScreen.show(primaryStage);
    }
}

