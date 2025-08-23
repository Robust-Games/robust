open module com.robustgames.robustclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires javafx.graphics;
    requires com.almasb.fxgl.scene;
    requires com.almasb.fxgl.core;

    exports com.robustgames.robustclient.business.entitiy.components;
    exports com.robustgames.robustclient.business.logic;
    exports com.robustgames.robustclient.business.factories;
    exports com.robustgames.robustclient.application;
    exports com.robustgames.robustclient.business.entitiy.components.animations;
    exports com.robustgames.robustclient.business.logic.tankService;
    exports com.robustgames.robustclient.business.logic.gameService;

}