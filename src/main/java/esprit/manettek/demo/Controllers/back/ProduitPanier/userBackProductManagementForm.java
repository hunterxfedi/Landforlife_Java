package esprit.manettek.demo.Controllers.back.ProduitPanier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ResourceBundle;


import esprit.manettek.demo.Entities.Produit;
import esprit.manettek.demo.Service.ServiceProduit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;


import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class userBackProductManagementForm {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox root;

    @FXML
    private Button addT;

    @FXML
    private TextField ProductNomField;

    @FXML
    private TextField ProductPrixField;

    @FXML
    private TextField ProductQuantiteField;

    @FXML
    private Button btn_workbench1111121;

    @FXML
    private Label errorNom;

    @FXML
    private Label errorQuantite;

    @FXML
    private Label errorPrix;

    @FXML
    private Label errorCategorie;

    @FXML
    private Text uploadImagePath;

    @FXML
    private Text errorUploadingImage;

    @FXML
    private ImageView imageUploaded;

    @FXML
    private TextField ProductStatutField;

    @FXML
    private Label InscriMessageLabel;
    @FXML
    private Button home;
    private Produit SelectedProduit;

    @FXML
    void back(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/manettek/demo/ProduitPanier/userBackProductManagement.fxml"));
            Parent parent = loader.load(); // Charger l'interface dans le parent

            // Récupérer la scène actuelle

            Scene scene = ((Node) event.getSource()).getScene();
            // Remplacer le contenu de la scène actuelle par le contenu de la nouvelle interface
            scene.setRoot(parent);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void cancelProduct(ActionEvent event) {

    }

    @FXML
    void submitProduct(ActionEvent event) {
        clearErrors();

        // Validate input
        boolean isValid = true;

        if (ProductNomField.getText().isEmpty()) {
            errorNom.setText("Product name is required");
            isValid = false;
        }

        try {
            float ProductPrix = Float.parseFloat(ProductPrixField.getText());
            if (ProductPrix <= 0) {
                errorPrix.setText("Price must be greater than 0");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            errorPrix.setText("Invalid price format");
            isValid = false;
        }

        try {
            int ProductQte = Integer.parseInt(ProductQuantiteField.getText());
            if (ProductQte <= 0) {
                errorQuantite.setText("Quantity must be greater than 0");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            errorQuantite.setText("Invalid quantity format");
            isValid = false;
        }
        if (ProductStatutField.getText().isEmpty()) {
            errorCategorie.setText("Product name is required");
            isValid = false;
        }



        if (isValid) {
            try {
                ServiceProduit sp = new ServiceProduit();

                String nom = ProductNomField.getText();
                int priu = Integer.parseInt(ProductPrixField.getText());
                int quantity = Integer.parseInt(ProductQuantiteField.getText());
                String statut = ProductStatutField.getText();

                String image = uploadImagePath.getText();


                if(SelectedProduit != null)
                {
                    Produit product = new Produit(SelectedProduit.getId(),nom,priu,image, statut,quantity);
                    sp.updateOne(product);


                }else{
                    Produit product = new Produit(nom, priu, image, statut,quantity);
                    sp.insertOne(product);


                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }


    }

    private void clearErrors() {
        errorNom.setText("");
        errorPrix.setText("");
        errorQuantite.setText("");
        errorCategorie.setText("");

    }

    @FXML
    void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            uploadImagePath.setText(file.getName());
        }
        String fileName = uploadImagePath.getText();
        if (fileName != null && !fileName.isEmpty()) {
            try {
                URL resourceUrl = getClass().getClassLoader().getResource("/assets");
                if (resourceUrl == null) {
                    File uploadsDirectory = new File("src/main/resources/upload");
                    if (!uploadsDirectory.exists()) {
                        uploadsDirectory.mkdirs();
                    }
                    resourceUrl = uploadsDirectory.toURI().toURL();
                }

                Files.copy(new File(file.getPath()).toPath(), Paths.get(resourceUrl.toURI()).resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

                Image image = new Image(new FileInputStream(file));
                imageUploaded.setImage(image);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }


    }

    @FXML
    void initialize() {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert addT != null : "fx:id=\"addT\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert ProductNomField != null : "fx:id=\"ProductNomField\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert ProductPrixField != null : "fx:id=\"ProductPrixField\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert ProductQuantiteField != null : "fx:id=\"ProductQuantiteField\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert btn_workbench1111121 != null : "fx:id=\"btn_workbench1111121\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert errorNom != null : "fx:id=\"errorNom\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert errorQuantite != null : "fx:id=\"errorQuantite\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert errorPrix != null : "fx:id=\"errorPrix\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert errorCategorie != null : "fx:id=\"errorCategorie\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert uploadImagePath != null : "fx:id=\"uploadImagePath\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert errorUploadingImage != null : "fx:id=\"errorUploadingImage\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert imageUploaded != null : "fx:id=\"imageUploaded\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert ProductStatutField != null : "fx:id=\"ProductStatutField\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";
        assert InscriMessageLabel != null : "fx:id=\"InscriMessageLabel\" was not injected: check your FXML file 'userBackProduitManagementForm.fxml'.";

    }
    public void setProduct(Produit selectedProduit) {
        SelectedProduit = selectedProduit;

        ProductNomField.setText(selectedProduit.getNom());
        ProductPrixField.setText(String.valueOf(selectedProduit.getPriu()));
        ProductQuantiteField.setText(String.valueOf(selectedProduit.getQuantity()));
        ProductStatutField.setText(selectedProduit.getStatut());

        uploadImagePath.setText(selectedProduit.getImage());
    }
}
