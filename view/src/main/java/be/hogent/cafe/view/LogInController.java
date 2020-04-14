package be.hogent.cafe.view;

import be.hogent.cafe.model.Cafe;
import be.hogent.cafe.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LogInController {

    private MainApp logIn;
    private Cafe model;
    private Stage logInStage;

    @FXML
    private TextField userName;

    @FXML
    private PasswordField pass;

    public void setModel (Cafe model) {
        this.model = model;
    }


    public void setCafeView (MainApp logIn) {
        this.logIn = logIn;

    }

    public void logIn()
    {
        if (logInCheck() == true)
        {
            logIn.showCafeOverview();

        }
    }

    public boolean logInCheck() {
        String errorMessage = "";

        try {model.logIn(userName.getText(), pass.getText());
            return true;}
        catch (IllegalArgumentException e)
        {
            errorMessage = e.getMessage();
            Alert alert = new Alert (Alert.AlertType.ERROR);
            alert.initOwner (logInStage);
            alert.setTitle ("Invalid Fields");
            alert.setHeaderText ("Please correct invalid fields");
            alert.setContentText (errorMessage);

            alert.showAndWait ();
        }
        return false;
     }



}
