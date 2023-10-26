module org.robinsonpn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jakarta.activation;
    requires org.monarchinitiative.phenol.core;
    requires org.monarchinitiative.phenol.io;
    requires com.fasterxml.jackson.databind;
    requires org.controlsfx.controls;
    requires org.slf4j;

    opens org.monarchinitiative.view to javafx.fxml, javafx.web;
    exports org.monarchinitiative;
    //exports com.sun.javafx.event to org.controlsfx.controls;
    opens org.monarchinitiative.controller to javafx.fxml, javafx.web;
}