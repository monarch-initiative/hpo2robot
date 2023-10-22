module org.robinsonpn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jakarta.activation;
   // requires jakarta.mail;
    requires java.mail;

    opens org.monarchinitiative.view to javafx.fxml, javafx.web;
    exports org.monarchinitiative;
    opens org.monarchinitiative.controller to javafx.fxml, javafx.web;

}