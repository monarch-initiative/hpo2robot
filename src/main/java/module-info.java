module org.monarchinitiative.hpo2robot {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.monarchinitiative.phenol.core;
    requires org.monarchinitiative.phenol.io;
    requires org.controlsfx.controls;
    requires org.slf4j;
    requires json.simple;
    requires java.net.http;

    opens org.monarchinitiative.hpo2robot.view to javafx.fxml, javafx.web;
    opens org.monarchinitiative.hpo2robot.controller to javafx.fxml, javafx.web;
    opens org.monarchinitiative.hpo2robot.model to javafx.base;

    exports org.monarchinitiative.hpo2robot;
    opens org.monarchinitiative.hpo2robot.controller.runner to javafx.base;
}