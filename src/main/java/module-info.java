module org.example.bngsocial {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.bngsocial to javafx.fxml;
    exports org.example.bngsocial;
}