module be.hogent.view {
    requires be.hogent.cafe.model;
    requires javafx.controls;
    requires javafx.fxml;
    exports be.hogent.cafe.view;
    opens be.hogent.cafe.view to javafx.graphics, javafx.fxml;
}