module org.example.bngsocial {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.bngsocial to javafx.fxml;
    exports org.example.bngsocial;
    exports org.example.bngsocial.Controllers;
    opens org.example.bngsocial.Controllers to javafx.fxml;
    exports org.example.bngsocial.Screens;
    opens org.example.bngsocial.Screens to javafx.fxml;
}