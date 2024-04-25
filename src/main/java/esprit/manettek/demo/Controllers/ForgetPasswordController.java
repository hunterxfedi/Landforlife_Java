package esprit.manettek.demo.Controllers;


import esprit.manettek.demo.Entities.ResetPassword;
import esprit.manettek.demo.Entities.User;
import esprit.manettek.demo.Service.JavaMailUtil;
import esprit.manettek.demo.Service.ResetPasswordService;
import esprit.manettek.demo.Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

public class ForgetPasswordController {

    @FXML
    private Button btnConfirmerCode;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnSendCode;

    @FXML
    private Label lbCodeError;

    @FXML
    private Label lbConfirmPasswordReset;

    @FXML
    private Label lbEmail;

    @FXML
    private Label lbPasswordReset;

    @FXML
    private TextField tfCode;

    @FXML
    private PasswordField tfConfirmPasswordReset;

    @FXML
    private TextField tfEmailForget;

    @FXML
    private PasswordField tfPasswordReset;

    @FXML
    private VBox vboxCode;

    @FXML
    private VBox vboxEmail;

    @FXML
    private VBox vboxMain;

    @FXML
    private VBox vboxReset;

    String email;

    Parent fxml;

    @FXML
    void fnConfirmerCode(ActionEvent event) {
        if (validateEmail()) {
            LocalDateTime now = LocalDateTime.now();

            // Subtract 3 hours from the current date and time
            LocalDateTime threeHoursAgo = now.minusHours(3);

            ResetPasswordService rsp = new ResetPasswordService();
            User user = new User();
            user.setEmail(email);
            ResetPassword rp = rsp.get(user);

            if (rp != null && rp.getCode() == Integer.parseInt(tfCode.getText())) {
                if (rp.getDateCreation().toLocalDateTime().isBefore(threeHoursAgo)) {
                    lbCodeError.setText("Code has expired. Please request a new one.");
                } else {
                    lbCodeError.setText("");
                    vboxMain.getChildren().set(0, vboxReset);
                    vboxMain.getChildren().removeAll(vboxEmail, vboxCode);
                    vboxReset.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }
            } else {
                lbCodeError.setText("Please enter a valid code");
            }
        }
    }


    @FXML
    void fnReset(ActionEvent event) {
        User user = new User();
        user.setEmail(email);
        ResetPasswordService rps = new ResetPasswordService();
        user.setPassword(tfPasswordReset.getText());
        rps.ResetPassword(user);
        //move to the sign in page
        try {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/esprit/manettek/demo/Main.fxml"));
            Parent mainRoot = mainLoader.load();

            // Set up the scene with the main root
            Scene scene = new Scene(mainRoot);
            scene.setFill(Color.TRANSPARENT);

            // Set up the new stage
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.initStyle(StageStyle.TRANSPARENT);
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    @FXML
    void fnSendCode(ActionEvent event) {
        if(validateEmail()){
            Random random = new Random();

            // Generate a random 4-digit number
            int randomNumber = random.nextInt(9000) + 1000;

            // Convert the number to a string
            String randomString = String.valueOf(randomNumber);
            try {
                ResetPasswordService rps = new ResetPasswordService();
                User user = new User();
                user.setEmail(tfEmailForget.getText());
                ResetPassword rp = new ResetPassword();
                rp.setUser(user);
                rp.setCode(randomNumber);
                rps.create(rp);
                JavaMailUtil.sendMail(tfEmailForget.getText(),"Reset Password","if you forget your password pls use this code to reset it: "+randomString);
                email = tfEmailForget.getText();
                vboxMain.getChildren().set(0,vboxCode);
                vboxMain.getChildren().removeAll(vboxEmail, vboxReset);

                vboxCode.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean validateEmail() {
        String email = tfEmailForget.getText();
        UserService us = new UserService();
        if (isValidEmail(email) && us.isEmailExist(email)) {
            lbEmail.setText("");
            return true;
        } else {
            lbEmail.setText("Please enter a valid email");
            return false;
        }
    }
    private boolean isValidEmail(String email) {
        // Implement your email validation logic here
        // You can use a regex pattern or any other validation method
        // For example, using a simple regex pattern:
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        System.out.println(email.matches(emailPattern));
        return email.matches(emailPattern);
    }

}
