open module com.robustgames.robustclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.almasb.fxgl.all;

    exports com.robustgames.robustclient.business.entitiy.components;
    exports com.robustgames.robustclient.business.factories;
    exports com.robustgames.robustclient.application;
    exports com.robustgames.robustclient.business.entitiy.components.animations;

}