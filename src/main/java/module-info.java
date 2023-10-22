module org.robinsonpn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jakarta.activation;
   // requires jakarta.mail;
    requires java.mail;

    opens org.robinsonpn.view to javafx.fxml, javafx.web;
    exports org.robinsonpn;
    opens org.robinsonpn.controller to javafx.fxml, javafx.web;

}