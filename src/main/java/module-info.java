module org.robinsonpn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    opens org.robinsonpn.view to javafx.fxml, javafx.web;
    opens org.robinsonpn.controller to javafx.fxml;
    exports org.robinsonpn;

}