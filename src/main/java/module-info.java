module org.robinsonpn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    opens org.robinsonpn.view to javafx.fxml, javafx.web;
    exports org.robinsonpn;

}