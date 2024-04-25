module esprit.monstergym.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;
    requires mail;
    requires AnimateFX;
    requires de.jensd.fx.glyphs.fontawesome;
    requires layout;
    requires kernel;
    requires jdk.jsobject;
    requires javafx.web;
    requires stripe.java;
    opens esprit.manettek.demo.Controllers to javafx.fxml;
    opens esprit.manettek.demo.Controllers.back.ProduitPanier to javafx.fxml;
    opens  esprit.manettek.demo.Controllers.Front.ProduitPanier to  javafx.fxml;
    exports esprit.manettek.demo.Main;
    exports esprit.manettek.demo.Controllers;
    exports esprit.manettek.demo.Entities;
    exports esprit.manettek.demo.Service;
    exports esprit.manettek.demo.Controllers.back.ProduitPanier;
    exports esprit.manettek.demo.Controllers.Front.ProduitPanier;

}
