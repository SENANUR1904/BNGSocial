package org.example.bngsocial;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image; // Bu satırı ekledim, logo için şart.
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. FXML Yükleme
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bngsocial/views/signScreen.fxml"));
            Parent root = loader.load();

            // 2. Scene ve Stil Ayarları
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/bngsocial/styles/style.css").toExternalForm());

            // 3. LOGO EKLEME (İkon Olarak)
            // logo.png dosyan resources/org/example/bngsocial/ içinde olduğu için yol bu şekildedir.
            try {
                Image appIcon = new Image(getClass().getResourceAsStream("/org/example/bngsocial/logo.png"));
                primaryStage.getIcons().add(appIcon);
            } catch (Exception e) {
                System.out.println("⚠️ Uyarı: Logo dosyası bulunamadı, ikon yüklenemedi.");
            }

            // 4. Pencere Ayarları
            primaryStage.setScene(scene);
            primaryStage.setTitle("BNGSocial - Giriş");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorScreen(primaryStage);
        }
    }

    private void showErrorScreen(Stage stage) {
        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "❌ Uygulama başlatılamadı!\n\n" +
                        "Lütfen konsoldaki hataları (Stack Trace) kontrol edin."
        );
        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-padding: 20px; -fx-text-alignment: center;");

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(errorLabel);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        stage.setScene(new Scene(vbox, 500, 300));
        stage.setTitle("Hata!");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}