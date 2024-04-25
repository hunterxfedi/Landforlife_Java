package esprit.manettek.demo.Controllers.back.ProduitPanier;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import esprit.manettek.demo.Entities.Produit;
import esprit.manettek.demo.Service.ServiceProduit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class UserBackProductManagement {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox root;

    @FXML
    private TableView<Produit> tableView;

    @FXML
    private TableColumn<?, ?> NomCo;

    @FXML
    private TableColumn<?, ?> PrixCo;

    @FXML
    private TableColumn<?, ?> QtCo;

    @FXML
    private TableColumn<?, ?> StatutCo;

    @FXML
    private Button btn_workbench111112;

    @FXML
    private Button updateButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button printButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private Button bak;

    @FXML
    private Button btn_workbench13;
    private Produit selectedProduit;

    @FXML
    void backHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/manettek/demo/hello-view.fxml"));
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
    void addProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/manettek/demo/ProduitPanier/userBackProductManagmentForm.fxml"));
            Parent parent = loader.load(); // Charger l'interface dans le parent

            // Récupérer la scène actuelle depuis l'événement
            Scene scene = ((Node) event.getSource()).getScene();
            // Remplacer le contenu de la scène actuelle par le contenu de la nouvelle interface
            scene.setRoot(parent);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }}

    @FXML
    void print(ActionEvent event) {
        List<Produit> produitListe = tableView.getItems();

        // Appeler la méthode generatePDF pour créer le PDF
        generatePDF(produitListe);

    }



    @FXML
    void removeProduct(ActionEvent event) {
        if(selectedProduit != null)
        {
            ServiceProduit sp = new ServiceProduit();
            try {
                sp.deleteOne(selectedProduit);

                displayAllProductsInTableView();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @FXML
    void searchProduct(ActionEvent event) {
        String filterBy = filterComboBox.getValue();
        String search = searchField.getText();

        try {
            ServiceProduit serviceProduit = new ServiceProduit();
            List<Produit> produits;
            if(search.isEmpty()){
                produits = serviceProduit.selectAll();
            }else
                produits = serviceProduit.searchBy(filterBy,search);
            ObservableList<Produit> produitObservableList = FXCollections.observableArrayList(produits);
            tableView.setItems(produitObservableList);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des personnes : " + e.getMessage());
        }

    }

    @FXML
    void updateProduct(ActionEvent event) {
        if(selectedProduit != null)
        {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/manettek/demo/ProduitPanier/userBackProductManagmentForm.fxml"));
                Parent parent = loader.load();

                userBackProductManagementForm controller = loader.getController();

                controller.setProduct(selectedProduit);

                Scene scene = ((Node) event.getSource()).getScene();
                // Remplacer le contenu de la scène actuelle par le contenu de la nouvelle interface
                scene.setRoot(parent);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void displayAllProductsInTableView() {
        tableView.setOnMouseClicked(this::handleTableViewClick);

        NomCo.setCellValueFactory(new PropertyValueFactory<>("nom"));
        PrixCo.setCellValueFactory(new PropertyValueFactory<>("priu"));
        StatutCo.setCellValueFactory(new PropertyValueFactory<>("statut"));
        QtCo.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        try {
            ServiceProduit serviceproduit = new ServiceProduit(); // Pass the connection object
            List<Produit> products = serviceproduit.selectAll();
            ObservableList<Produit> produitObservableList = FXCollections.observableArrayList(products);
            tableView.setItems(produitObservableList);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des personnes : " + e.getMessage());
        }
    }

    private void handleTableViewClick(MouseEvent mouseEvent) {
        Produit selectedProduit = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduit != null) {
            this.selectedProduit = selectedProduit;

            setTableActionButtons(false);
        }
    }

    public void generatePDF(List<Produit> produitlist) {
        // Création d'une boîte de dialogue pour choisir l'emplacement de sauvegarde du PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));

        // Afficher la boîte de dialogue et attendre que l'utilisateur sélectionne un emplacement
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        // Vérifier si un fichier a été sélectionné
        if (selectedFile != null) {
            // Utiliser le chemin sélectionné par l'utilisateur pour enregistrer le PDF
            String dest = selectedFile.getAbsolutePath();
            try {
                // Initialiser le document PDF
                PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
                Document document = new Document(pdfDoc, PageSize.A4);

                // Ajouter un titre au document
                Paragraph title = new Paragraph("Liste des produits ");
                title.setFontSize(16).setBold();
                document.add(title);

                // Créer un tableau pour afficher les données
                Table table = new Table(5);
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("nom")));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("prix")));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Qauntity")));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Statut")));





                // Remplir le tableau avec les données de la liste
                for (Produit product :produitlist) {
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(product.getNom())));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(product.getPriu()))));

                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(product.getQuantity()))));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(product.getStatut())));




                }

                // Ajouter le tableau au document
                document.add(table);

                // Fermer le document
                document.close();

                // Affichage d'une alerte de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Le fichier PDF a été enregistré avec succès !");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucun emplacement sélectionné.");
        }
    }

    private void setTableActionButtons(boolean state) {
        updateButton.setDisable(state);
        removeButton.setDisable(state);

    }

    @FXML
    void initialize() {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert NomCo != null : "fx:id=\"NomCo\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert PrixCo != null : "fx:id=\"PrixCo\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert QtCo != null : "fx:id=\"QtCo\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert StatutCo != null : "fx:id=\"StatutCo\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert btn_workbench111112 != null : "fx:id=\"btn_workbench111112\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert updateButton != null : "fx:id=\"updateButton\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert removeButton != null : "fx:id=\"removeButton\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert printButton != null : "fx:id=\"printButton\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert searchField != null : "fx:id=\"searchField\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert filterComboBox != null : "fx:id=\"filterComboBox\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        assert btn_workbench13 != null : "fx:id=\"btn_workbench13\" was not injected: check your FXML file 'userBackProductManagement.fxml'.";
        displayAllProductsInTableView();
        setTableActionButtons(true);

    }

}
