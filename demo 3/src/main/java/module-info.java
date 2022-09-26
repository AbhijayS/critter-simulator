module assignment5 {
    requires javafx.controls;
    requires javafx.fxml;

    opens assignment5 to javafx.fxml;
    exports assignment5;
}
