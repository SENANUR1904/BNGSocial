package org.example.bngsocial.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class RootController {

    @FXML private BorderPane mainPane;
    private int currentUserId;
    private String currentUsername;


    public void setUserData(int userId, String username) {
        this.currentUserId = userId;
        this.currentUsername = username;
        showHome();
    }

    @FXML
    private void showHome() {
        loadView("/org/example/bngsocial/views/HomeView.fxml");
    }

    @FXML
    private void showFriends() {
        loadView("/org/example/bngsocial/views/FriendsView.fxml");
    }

    @FXML
    private void showNetwork() {
        // Genel Ağ Sayfasını Yükle
        loadView("/org/example/bngsocial/views/NetworkView.fxml");
    }

    @FXML
    private void showNewPost() {
        // Senin oluşturduğun paylaşım sayfası
        loadView("/org/example/bngsocial/views/Sharing.fxml");
         }

    @FXML
    private void showTopLists() {
        loadView("/org/example/bngsocial/views/TopListsView.fxml");
    }

    @FXML
    private void showSettings() {
        loadView("/org/example/bngsocial/views/SettingsView.fxml");
    }

    // Ortak ekran yükleme ve veri taşıma metodu
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            view.getStylesheets().add(getClass().getResource("/org/example/bngsocial/styles/style.css").toExternalForm());

            // Yüklenen sayfanın controller'ına eriş
            Object controller = loader.getController();

            // Hangi controller ise ona ID'yi gönder (Type Casting)
            if (controller instanceof HomeController) {
                ((HomeController) controller).setUserId(currentUserId);
            }
            else if (controller instanceof FriendsController) {
                ((FriendsController) controller).setUserId(currentUserId);
            }
            else if (controller instanceof NetworkController) {
                ((NetworkController) controller).setUserId(currentUserId);
            }
            else if (controller instanceof TopListsController) {
                ((TopListsController) controller).setUserId(currentUserId);
            }
            else if (controller instanceof SettingsController) {
                ((SettingsController) controller).setUserId(currentUserId);
            }
            else if (controller instanceof SharingController) {
                // Eğer eski mainScreen'i kullanıyorsan onun metod adı setUserData idi
                // Ama ismi almak için veritabanına tekrar sormak yerine basitçe "Kullanıcı" diyebiliriz
                // veya sadece ID gönderecek şekilde o metodu güncelleyebilirsin.
                ((SharingController) controller).setUserData(currentUserId, currentUsername);
            }

            mainPane.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Sayfa yüklenemedi: " + fxmlPath);
        }
    }
}