package esprit.manettek.demo.Controllers;

import esprit.manettek.demo.Entities.User;
import esprit.manettek.demo.Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class signInController implements Initializable {

    @FXML
    private TextField tfEmail;
    @FXML
    private Button btnForgetPass;
    @FXML
    private VBox vbox;
    @FXML
    private TextField tfPassword;
    Parent fxml;
    public static User user_connected;
    private MainController mainController; // Reference to MainController

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    private UserService userService;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userService = UserService.getInstance();
    }

    @FXML
    void fnSignIn(ActionEvent event) {
        // Your code for sign-in
        String email = tfEmail.getText();
        String password = tfPassword.getText();

        // Check if email and password are not empty
        if (validateFields(email, password)) {
            // Authenticate user
            User authenticatedUser = userService.authenticate(email, password);
            if (authenticatedUser != null) {
                // Set authenticated user in UserService
                System.out.println("test"+authenticatedUser.toString());
                userService.setAuthenticatedUser(authenticatedUser);
                System.out.println(userService.getAuthenticatedUser().toString()+"ttttttt");
                // Your existing code to navigate to the next scene...
            }
            if (authenticatedUser != null) {
                try {
                    // Load appropriate dashboard based on user role
                    if (authenticatedUser.hasRole("ROLE_ADMIN")) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/monstergym/demo/DashboardAdmin.fxml"));
                        Parent root = loader.load();

                        // Pass the user to the DashboardAdminController
                       /* DashboardAdminController dashboardAdminController = loader.getController();
                        dashboardAdminController.setUser(authenticatedUser);*/

                        // Create a new stage for the new scene
                        Stage newStage = new Stage();
                        newStage.setScene(new Scene(root));
                        newStage.show();

                        // Close the current stage
                        Stage currentStage = (Stage) tfEmail.getScene().getWindow();
                        currentStage.close();
                    } else if (authenticatedUser.hasRole("ROLE_USER")) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/manettek/demo/hello-view.fxml"));
                        Parent root = loader.load();

                        // Pass the user to the DashboardClientController

                        HelloController helloController = loader.getController();
                        System.out.println(authenticatedUser.toString());
                        helloController.setUser(authenticatedUser);


                        // Create a new stage for the new scene
                        Stage newStage = new Stage();
                        newStage.setScene(new Scene(root));
                        newStage.show();

                        // Close the current stage
                        Stage currentStage = (Stage) tfEmail.getScene().getWindow();
                        currentStage.close();
                    } else {
                        // User has no role assigned, show error message
                        showErrorAlert("User role not assigned.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Authentication failed, show error message
                showErrorAlert("Invalid email or password.");
            }
        } else {
            // Fields are empty, show error message
            showErrorAlert("Please enter email and password.");
        }
    }


    // Method to validate email and password fields
    private boolean validateFields(String email, String password) {
        return !email.isEmpty() && !password.isEmpty();
    }

    // Method to show error alert
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle("Authentication Failed");
        alert.setHeaderText(null);
        alert.show();
    }

    @FXML
    void btnForgetPassAction(ActionEvent event) {
        try {
            // Load ForgetPassword.fxml
            fxml = FXMLLoader.load(getClass().getResource("/esprit/manettek/demo/ForgetPassword.fxml"));
            vbox.getChildren().setAll(fxml);
            // Traverse through the loaded FXML hierarchy to find the VBox elements
            VBox vboxMain = null;
            VBox vboxEmail = null;
            VBox vboxCode = null;
            VBox vboxReset = null;

            if (fxml instanceof VBox) {
                VBox rootVBox = vbox;
                for (Node node : rootVBox.getChildren()) {
                    if (node instanceof VBox) {
                        VBox vbox = (VBox) node;
                        switch (vbox.getId()) {
                            case "vboxMain":
                                vboxMain = vbox;
                                break;
                            default:
                                // Handle other nodes if needed
                                break;
                        }
                    }
                }
            }
            for(Node node : vboxMain.getChildren()){
                if(node instanceof  VBox) {
                    VBox vbox = (VBox) node;
                    switch (vbox.getId()){
                        case "vboxEmail":
                            vboxEmail = vbox;
                            break;
                        case "vboxCode":
                            vboxCode = vbox;
                            break;
                        case "vboxReset":
                            vboxReset = vbox;
                            break;
                        default:
                            break;
                    }
                }
            }
            // Remove vboxCode and vboxReset from vboxMain
            if (vboxMain != null) {
                vboxMain.getChildren().removeAll(vboxCode, vboxReset);
            }

            // Resize vboxEmail to occupy the entire space previously occupied by vboxMain
            if (vboxEmail != null) {
                vboxEmail.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }

            // Set the loaded FXML as the only child of vbox

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
