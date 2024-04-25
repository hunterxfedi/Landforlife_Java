package esprit.manettek.demo.Controllers.Front.ProduitPanier;

import esprit.manettek.demo.Entities.Panier;
import esprit.manettek.demo.Entities.PanierProduit;
import esprit.manettek.demo.Entities.Produit;
import esprit.manettek.demo.Entities.User;
import esprit.manettek.demo.Service.*;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class userFrontGymProduitPanier {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox root;

    @FXML
    private AnchorPane side_ankerpane;

    @FXML
    private Label nbrPannier;

    @FXML
    private Button payNowButton;

    @FXML
    private Label TotalPriceField;

    @FXML
    private Pagination pagination;

    @FXML
    private Label userNameLabel;

    @FXML
    private Button backprod;

    private User user;
    Stage paymentStage = new Stage();

    @FXML
    void backProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/manettek/demo/produitpanierfront/userFrontGymProduit.fxml"));
            Parent parent = loader.load();

            // Accéder au contrôleur de la nouvelle fenêtre
            userFrontGymProduit controller = loader.getController();
            controller.setUserModel(user); // Envoyer l'utilisateur au contrôleur de FrontAbonnement
            System.out.println("envoi succes");
            Scene scene = userNameLabel.getScene();
            // Remplacer le contenu de la scène actuelle par le contenu de la nouvelle interface
            scene.setRoot(parent);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    void payNow(ActionEvent event) {
        ServicePanier servicePanier = new ServicePanier();
        ServiceProduit serviceProduit = new ServiceProduit();
        ServicePanierProduit servicePanierProduit = new ServicePanierProduit();
        try {
            Panier panier = servicePanier.selectByUserId(user.getId()).get(0);

            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();

            ArrayList<String> itemsList = new ArrayList<>();
            ArrayList<Integer> itemsQteList = new ArrayList<>();
            ArrayList<Float> pricesList = new ArrayList<>();

            for(PanierProduit panierProduit:servicePanierProduit.selectByPanierId(panier.getId()))
            {
                Produit produit = serviceProduit.selectById(panierProduit.getProduit_id().getId());
                itemsList.add(produit.getNom());
                itemsQteList.add(panierProduit.getQuantite());
                pricesList.add((float) (produit.getPriu()*panierProduit.getQuantite()));
            }

            float total = pricesList.stream().reduce(0f, Float::sum);

            webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    StringBuilder script = new StringBuilder();
                    for (int i = 0; i < pricesList.size(); i++) {
                        script.append("var row = document.createElement('tr');");
                        script.append("row.innerHTML = '<td>' + '").append(itemsList.get(i)).append(" (").append(itemsQteList.get(0)).append(")' + '</td>' + '<td>").append(String.format("%.2f", pricesList.get(i))).append("DT</td>';");
                        script.append("document.getElementById('receipt-table-body').appendChild(row);");
                    }
                    script.append("document.getElementById('total').innerText = '").append(String.format("%.2f", total)).append("DT';");
                    webEngine.executeScript(script.toString());
                    JSObject window = (JSObject) webView.getEngine().executeScript("window");
                    window.setMember("java", this);
                }
            });

            webEngine.load(Objects.requireNonNull(getClass().getResource("/payment_form.html")).toExternalForm());
            StackPane root = new StackPane();
            root.getChildren().add(webView);

            Scene scene = new Scene(root, 600, 600);
            paymentStage.setScene(scene);

            paymentStage.setTitle("Payment Details");
            paymentStage.show();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void payNowBtnClickedReceipt() {
        ServicePanierProduit servicePanierProduit = new ServicePanierProduit();
        ServiceProduit serviceProduit = new ServiceProduit();
        ServicePanier servicePanier = new ServicePanier();
        Payment p = new Payment();
        ArrayList<Float> pricesList = new ArrayList<>();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Payment Confirmed");
        alert.setHeaderText(null);
        alert.setContentText("Payment confirmed, wait for delivery in approximately 48 hours.");

        alert.showAndWait();
        try {
            Panier panier = servicePanier.selectByUserId(user.getId()).get(0);
            for(PanierProduit panierProduit:servicePanierProduit.selectByPanierId(panier.getId())) {
                Produit produit = serviceProduit.selectById(panierProduit.getProduit_id().getId());
                produit.setQuantity(produit.getQuantity()-panierProduit.getQuantite());
                serviceProduit.updateOne(produit);
                pricesList.add((float) (produit.getPriu()*panierProduit.getQuantite()));
            }
            float total = pricesList.stream().reduce(0f, Float::sum);

            servicePanier.deleteOne(panier);
            long priceLong = (long) (total*0.32) *100;
            p.processPayment(priceLong);
            showBasket();

            paymentStage.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void printBtnClickedReceipt() {
        // Implement your printReceipt action here
        System.out.println("print");
        WebView webView = (WebView) paymentStage.getScene().getRoot().getChildrenUnmodifiable().get(0);

        // Create a PrinterJob
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null) {
            // Print the content of the WebView node
            boolean success = job.printPage(webView);

            if (success) {
                job.endJob();
            }
        }
    }
    private int getNbrProductInPannier(int id_user) {
        int nbr = 0;

        ServicePanier sp = new ServicePanier();

        try {
            Panier panier = sp.selectByUserId(id_user).get(0);
            if(panier == null)
                return nbr;

            ServicePanierProduit Spp = new ServicePanierProduit();

            List<PanierProduit> panierProduits = Spp.selectByPanierId(panier.getId());

            for(PanierProduit panierProduit : panierProduits) {
                nbr += panierProduit.getQuantite();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IndexOutOfBoundsException e){
            return 0;
        }

        return nbr;
    }

    @FXML
    void initialize() {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'userFrontGymProduitPanier.fxml'.";
        assert side_ankerpane != null : "fx:id=\"side_ankerpane\" was not injected: check your FXML file 'userFrontGymProduitPanier.fxml'.";
        assert nbrPannier != null : "fx:id=\"nbrPannier\" was not injected: check your FXML file 'userFrontGymProduitPanier.fxml'.";
        assert payNowButton != null : "fx:id=\"payNowButton\" was not injected: check your FXML file 'userFrontGymProduitPanier.fxml'.";
        assert TotalPriceField != null : "fx:id=\"TotalPriceField\" was not injected: check your FXML file 'userFrontGymProduitPanier.fxml'.";
        assert pagination != null : "fx:id=\"pagination\" was not injected: check your FXML file 'userFrontGymProduitPanier.fxml'.";
        assert userNameLabel != null : "fx:id=\"userNameLabel\" was not injected: check your FXML file 'userFrontGymProduitPanier.fxml'.";
    }
    private void showBasket() {
        System.out.println("show basket running");
        int nbrProductInPannier = getNbrProductInPannier(user.getId());
        nbrPannier.setText(String.valueOf(nbrProductInPannier));

        try {
            if(nbrProductInPannier == 0)
            {
                TotalPriceField.setText("Total Price: N/a");
                payNowButton.setDisable(true);
                pagination.setPageCount(1);
                pagination.setPageFactory(null);
            }else{
                ServicePanierProduit Spp = new ServicePanierProduit();
                List<PanierProduit> panierProduits = Spp.selectAll();
                int pageSize = panierProduits.size() / 3;
                if (panierProduits.size() % 3 != 0) {
                    pageSize++;
                }
                pagination.setPageCount(pageSize);
                pagination.setPageFactory(this::createPage);

                float finalPrice = 0;

                ServiceProduit serviceProduit = new ServiceProduit();
                for(PanierProduit panierProduit : panierProduits){
                    finalPrice += serviceProduit.selectById(panierProduit.getProduit_id().getId()).getPriu() * panierProduit.getQuantite();
                    TotalPriceField.setText("Total Price: "+finalPrice+" DT");
                }

                payNowButton.setDisable(false);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    private AnchorPane createPage(int pageIndex) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(952, 167);

        try {
            ServicePanierProduit spp = new ServicePanierProduit();
            ServicePanier servicePanier = new ServicePanier();
            List<Panier>  panierList = servicePanier.selectByUserId(user.getId());
            if(panierList.isEmpty())
            {
                return anchorPane;
            }
            Panier panier = panierList.get(0);

            List<PanierProduit> panierProduits = spp.selectByPanierId(panier.getId());
            int itemsPerPage = 3;
            int startIndex = pageIndex * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, panierProduits.size());

            int offsetY = 0;
            for (int i = startIndex; i < endIndex; i++) {

                PanierProduit panierProduit = panierProduits.get(i);
                ServiceProduit serviceProduit = new ServiceProduit();

                Produit produit = serviceProduit.selectById(panierProduit.getProduit_id().getId());
                System.out.println(panierProduit);

                Pane productPane = new Pane();
                productPane.setLayoutY(offsetY);
                productPane.setPrefSize(952, 167);
                productPane.setStyle(
                        "-fx-background-color: #5bc0de;" +
                                "-fx-border-color: #000000;" +
                                "-fx-border-width: 2px 2px 2px 2px;" +
                                "-fx-border-radius: 16px;" +
                                "-fx-background-radius: 16px;" +
                                "-fx-border-style: solid;");
                anchorPane.getChildren().add(productPane);

                Label productNameLabel = new Label(produit.getNom());
                productNameLabel.setLayoutX(240);
                productNameLabel.setLayoutY(14);
                productNameLabel.setPrefWidth(400);
                productNameLabel.setTextFill(Color.WHITE);
                productNameLabel.setFont(Font.font("Century Gothic Bold", 17));
                productPane.getChildren().add(productNameLabel);



                Label prixLabel = new Label("Prix: "+produit.getPriu()+"DT");
                prixLabel.setLayoutX(680);
                prixLabel.setLayoutY(120);
                prixLabel.setPrefWidth(400);
                prixLabel.setStyle("-fx-text-fill: white;");
                prixLabel.setFont(Font.font("Century Gothic Bold", 17));
                prixLabel.setWrapText(true);
                productPane.getChildren().add(prixLabel);


                Button removeFromBasket = new Button("Remove From Basket");
                removeFromBasket.setLayoutX(800);
                removeFromBasket.setLayoutY(14);
                removeFromBasket.setPrefSize(139, 30);
                removeFromBasket.setStyle(
                        "-fx-background-color:  #dc3545;" +
                                "-fx-text-fill: white;"+
                                "-fx-border-width: 2px 2px 2px 2px;" +
                                "-fx-border-radius: 50px;" +
                                "-fx-background-radius: 50px;" +
                                "-fx-border-style: solid;" +
                                "-fx-border-color: black;");

                removeFromBasket.setOnAction(event->{
                    ServicePanierProduit servicePanierProduit = new ServicePanierProduit();
                    try {
                        servicePanierProduit.deleteOne(panierProduit);
                        showBasket();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                productPane.getChildren().add(removeFromBasket);

                Label categoryLabel = new Label("Statut: "+produit.getStatut());
                categoryLabel.setLayoutX(680);
                categoryLabel.setLayoutY(80);
                categoryLabel.setPrefWidth(400);
                categoryLabel.setStyle("-fx-text-fill: white;");
                categoryLabel.setFont(Font.font("Century Gothic Bold", 17));
                categoryLabel.setWrapText(true);
                productPane.getChildren().add(categoryLabel);

                Label qteLabel = new Label("Qte:");
                qteLabel.setLayoutX(800);
                qteLabel.setLayoutY(120);
                qteLabel.setPrefWidth(400);
                qteLabel.setStyle("-fx-text-fill: white;");
                qteLabel.setFont(Font.font("Century Gothic Bold", 17));
                qteLabel.setWrapText(true);
                productPane.getChildren().add(qteLabel);

                TextField qteField = new TextField(String.valueOf(panierProduit.getQuantite()));
                qteField.setLayoutX(840);
                qteField.setLayoutY(110);
                qteField.setPrefWidth(80);
                qteField.setStyle("-fx-text-fill: black;");
                qteField.setFont(Font.font("Century Gothic Bold", 17));
                qteField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) {
                        qteField.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                });
                qteField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        handleQteFieldChange(qteField.getText(), panierProduit.getPanier_id().getId(), produit.getId());
                        event.consume();
                    }
                });

                productPane.getChildren().add(qteField);

                Image image;
                if (produit.getImage().isEmpty()) {
                    image = new Image(getClass().getResource("/assets/icons/images/1.jpg").toExternalForm());
                } else {
                    image = new Image(getClass().getResource("/upload/" + produit.getImage()).toExternalForm());
                }
                ImageView imageView = new ImageView(image);

                imageView.setLayoutX(110);
                imageView.setLayoutY(30);
                imageView.setFitWidth(120);
                imageView.setFitHeight(110);
                productPane.getChildren().add(imageView);

                imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        ImageView fullScreenImageView = new ImageView(imageView.getImage());
                        fullScreenImageView.setFitWidth(600);
                        fullScreenImageView.setFitHeight(600);
                        StackPane fullScreenPane = new StackPane(fullScreenImageView);
                        Scene fullScreenScene = new Scene(fullScreenPane, 600, 600);
                        Stage fullScreenStage = new Stage();
                        fullScreenStage.setScene(fullScreenScene);
                        fullScreenStage.show();
                    }
                });
                offsetY += 180;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return anchorPane;
    }


    private void handleQteFieldChange(String newValue, int panierId, int produit_id) {
        try {
            ServicePanierProduit servicePanierProduit = new ServicePanierProduit();
            ServiceProduit serviceProduit = new ServiceProduit();
            ServicePanier servicePanier = new ServicePanier();
            PanierProduit panierProduit = new PanierProduit(servicePanier.selectById(panierId),serviceProduit.selectById(produit_id),Integer.parseInt(newValue));
            Produit produit = serviceProduit.selectById(produit_id);

            if(produit.getQuantity()<panierProduit.getQuantite())
            {
                panierProduit.setQuantite(produit.getQuantity());
            }

            if(panierProduit.getQuantite() <=0)
                servicePanierProduit.deleteOne(panierProduit);
            else
                servicePanierProduit.updateOne(panierProduit);

            showBasket();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUserModel(User user) {
        this.user = user;
        nbrPannier.setText(String.valueOf(getNbrProductInPannier(user.getId())));
        // Utilisez l'utilisateur chargé pour initialiser votre interface utilisateur
        if (user != null) {

            System.out.println("trouve");
            userNameLabel.setText(user.getNom());

        } else {
            System.out.println("Utilisateur non trouvé");
        }
        UserService serviceUser = new UserService();
        nbrPannier.setText(String.valueOf(getNbrProductInPannier(user.getId())));
        showBasket();
        System.out.println("test");
    }


}

