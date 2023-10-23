module org.robinsonpn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jakarta.activation;
   // requires jakarta.mail;
    requires java.mail;
    requires org.controlsfx.controls;

    opens org.monarchinitiative.view to javafx.fxml, javafx.web;
    exports org.monarchinitiative;
    //exports com.sun.javafx.event to org.controlsfx.controls;
    opens org.monarchinitiative.controller to javafx.fxml, javafx.web;

}