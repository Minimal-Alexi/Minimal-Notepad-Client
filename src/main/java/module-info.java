module org.metropolia.minimalnotepadclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.metropolia.minimalnotepadclient to javafx.fxml;
    exports org.metropolia.minimalnotepadclient;
    exports org.metropolia.minimalnotepadclient.controller;
    opens org.metropolia.minimalnotepadclient.controller to javafx.fxml;
}