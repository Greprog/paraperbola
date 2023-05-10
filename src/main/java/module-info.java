module com.example.coordinatesystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    //requires javafx.swing;


    opens com.example.coordinatesystem to javafx.fxml;
    exports com.example.coordinatesystem;
}