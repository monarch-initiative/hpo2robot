module org.monarchinitiative.hpo2robot {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.monarchinitiative.phenol.core;
    requires org.monarchinitiative.phenol.io;
    requires org.controlsfx.controls;
    requires org.slf4j;

    opens org.monarchinitiative.hpo2robot.view to javafx.fxml, javafx.web;
    exports org.monarchinitiative.hpo2robot;
    //exports com.sun.javafx.event to org.controlsfx.controls;
    opens org.monarchinitiative.hpo2robot.controller to javafx.fxml, javafx.web;
    opens org.monarchinitiative.hpo2robot.model to javafx.base;
}