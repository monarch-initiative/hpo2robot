module org.robinsonpn {
    requires javafx.controls;
    requires javafx.fxml;
    opens org.robinsonpn to javafx.fxml;
    exports org.robinsonpn;

}