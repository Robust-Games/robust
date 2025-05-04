module com.robustgames.robustclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;


    opens com.robustgames.robustclient.application to javafx.fxml;
    exports com.robustgames.robustclient.application;
    opens com.robustgames.robustclient.business to javafx.fxml;
    exports com.robustgames.robustclient.business;
}