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

    private MainApp mainApp;
    private Stage logInStage;

    @FXML
    private TextField userName;

    @FXML
    private PasswordField pass;

       public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;

    }

    public void logIn()
    {
        if (logInCheck())
        {
            mainApp.showCafeOverview();

        }
    }

    public boolean logInCheck() {
        String errorMessage;

        try {mainApp.getModel().logIn(userName.getText(), pass.getText());
            return true;}
        catch (IllegalArgumentException e)
        {
            errorMessage = e.getMessage();
            Alert alert = new Alert (Alert.AlertType.ERROR);
         //   alert.initOwner (logInStage);
            alert.setTitle ("Invalid Fields");
            alert.setHeaderText ("Please correct invalid fields:");
            alert.setContentText (errorMessage);

            alert.showAndWait ();
        }
        return false;
     }



}
