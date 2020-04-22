package be.hogent.cafe.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class LogInController {

    private MainApp mainApp;

    @FXML
    private TextField userName;

    @FXML
    private PasswordField pass;

    @FXML
    private ImageView eye;
    private String password;


    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;

        eye.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> { //pass reveal feature
               password = pass.getText();
               pass.clear();
               pass.setPromptText(password);
           });
           eye.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
               pass.setText(password);
               pass.setPromptText("Password");
           });
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
