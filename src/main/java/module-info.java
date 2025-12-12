module org.example.bngsocial {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example.bngsocial to javafx.fxml;
    opens org.example.bngsocial.Controllers to javafx.fxml;
    opens org.example.bngsocial.Models to javafx.fxml;

    requires com.microsoft.sqlserver.jdbc;

    exports org.example.bngsocial;
    exports org.example.bngsocial.Controllers;
    exports org.example.bngsocial.Models;
}