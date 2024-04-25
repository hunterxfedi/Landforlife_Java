package esprit.manettek.demo.Controllers.Front.ProduitPanier;

import esprit.manettek.demo.Entities.Panier;
import esprit.manettek.demo.Entities.PanierProduit;
import esprit.manettek.demo.Entities.Produit;
import esprit.manettek.demo.Entities.User;
import esprit.manettek.demo.Service.ServicePanier;
import esprit.manettek.demo.Service.ServicePanierProduit;
import esprit.manettek.demo.Service.ServiceProduit;
import esprit.manettek.demo.Service.UserService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;



public class userFrontGymProduit {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private HBox root;

    @FXML
    private Pagination pagination;

    @FXML
    private Label nbrPannier;

    @FXML
    private Label errorPanierProduit;

    @FXML
    private Label userNameLabel;

    @FXML
    private AnchorPane side_ankerpane;



    private List<Produit> produits;


    final int PRODUCTS_PER_PAGE = 6;
    private User user;


    public void openBasket(javafx.scene.input.MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/manettek/demo/produitpanierfront/UserFrontGymProduitPanier.fxml"));
            Parent parent = loader.load();
            userFrontGymProduitPanier controller = loader.getController();
            controller.setUserModel(user); // Utiliser la méthode setUserModel avec l'objet user

            // Récupérer la scène actuelle
            Scene scene = side_ankerpane.getScene();
            // Remplacer le contenu de la scène actuelle par le contenu de la nouvelle interface
            scene.setRoot(parent);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    @FXML
    void initialize() {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'userFrontGymProduit.fxml'.";
        assert side_ankerpane != null : "fx:id=\"side_ankerpane\" was not injected: check your FXML file 'userFrontGymProduit.fxml'.";
        assert pagination != null : "fx:id=\"pagination\" was not injected: check your FXML file 'userFrontGymProduit.fxml'.";
        assert nbrPannier != null : "fx:id=\"nbrPannier\" was not injected: check your FXML file 'userFrontGymProduit.fxml'.";
        assert errorPanierProduit != null : "fx:id=\"errorPanierProduit\" was not injected: check your FXML file 'userFrontGymProduit.fxml'.";
        assert userNameLabel != null : "fx:id=\"userNameLabel\" was not injected: check your FXML file 'userFrontGymProduit.fxml'.";


        ServiceProduit sp = new ServiceProduit();

        try {
            produits = sp.selectAll();
            int pageSize = produits.size() / 6;
            if (produits.size() % 6 != 0) {
                pageSize++;
            }
            pagination.setPageCount(pageSize);
            pagination.setPageFactory(this::createPage);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

    private AnchorPane createPage ( int pageIndex){
        if (this.user == null) {
            System.out.println("User is not initialized");
        }
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPrefWidth(900);
            anchorPane.setPrefHeight(600);

            int startIndex = pageIndex * PRODUCTS_PER_PAGE;
            int endIndex = Math.min(startIndex + PRODUCTS_PER_PAGE, produits.size());

            int xOffset = 0;
            int yOffset = 30;
            int xIncrement = 330;
            int yIncrement = 260;

            for (int i = startIndex; i < endIndex; i++) {
                Produit produit = produits.get(i);

                AnchorPane productPane = new AnchorPane();
                productPane.setLayoutX(xOffset);
                productPane.setLayoutY(yOffset);
                productPane.setPrefSize(307, 234);
                productPane.setStyle(
                        "-fx-background-color: #5bc0de;" +
                                "-fx-border-color: #000000;" +
                                "-fx-border-width: 2px 2px 2px 2px;" +
                                "-fx-border-radius: 16px;" +
                                "-fx-background-radius: 16px;" +
                                "-fx-border-style: solid;");

                Label nameLabel = new Label(produit.getNom());
                nameLabel.setLayoutX(18);
                nameLabel.setLayoutY(12);
                nameLabel.setPrefWidth(119);
                nameLabel.setStyle("-fx-text-fill: white;");
                nameLabel.setWrapText(true);
                productPane.getChildren().add(nameLabel);


                Label priceLabel = new Label("Prix: " + produit.getPriu() + "DT");
                priceLabel.setLayoutX(19);
                priceLabel.setLayoutY(207);
                priceLabel.setStyle("-fx-text-fill: white;");
                productPane.getChildren().add(priceLabel);

                Label qteLabel;
                if (produit.getQuantity() > 0) {
                    qteLabel = new Label("Status: In Stock (" + produit.getQuantity() + ")");
                    qteLabel.setStyle("-fx-text-fill: green;");
                } else {
                    qteLabel = new Label("Status: Out Of Stock (" + produit.getQuantity() + ")");
                    qteLabel.setStyle("-fx-text-fill: red;");
                }
                qteLabel.setLayoutX(100);
                qteLabel.setLayoutY(207);
                productPane.getChildren().add(qteLabel);


                Image image;
                if (produit.getImage().isEmpty()) {
                    image = new Image(getClass().getResource("/assets/icons/images/1.jpg").toExternalForm());
                } else {
                    image = new Image(getClass().getResource("/upload/" + produit.getImage()).toExternalForm());
                }
                ImageView imageView = new ImageView(image);

                imageView.setLayoutX(159);
                imageView.setLayoutY(48);
                imageView.setFitWidth(139);
                imageView.setFitHeight(141);
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


                Button addButton = new Button("Add To Basket");
                addButton.setLayoutX(159);
                addButton.setLayoutY(10);
                addButton.setPrefSize(139, 30);
                addButton.setStyle(
                        "-fx-background-color: #f3fafa;" +
                                "-fx-border-width: 2px 2px 2px 2px;" +
                                "-fx-border-radius: 50px;" +
                                "-fx-background-radius: 50px;" +
                                "-fx-border-style: solid;" +
                                "-fx-border-color: black;");
                productPane.getChildren().add(addButton);

                if (produit.getQuantity() <= 0)
                    addButton.setDisable(true);

                addButton.setOnAction(event -> {
                    try {
                        errorPanierProduit.setText("");
                        ServicePanier sp = new ServicePanier();
                        List<Panier> paniers = sp.selectByUserId(user.getId());
                        if (paniers.isEmpty()) {
                            UserService serviceUser = new UserService();
                            Panier p = new Panier(serviceUser.selectById(user.getId()));
                            sp.insertOne(p);
                            paniers = sp.selectByUserId(user.getId());
                        }
                        Panier panier = paniers.get(0);
                        ServicePanierProduit Spp = new ServicePanierProduit();

                        List<PanierProduit> currentPanierProduit = Spp.selectByPanierProduitId(panier.getId(), produit.getId());
                        if (currentPanierProduit.isEmpty()) {
                            System.out.println("current Pannier Produit is empty");
                            PanierProduit panierProduit = new PanierProduit(panier, produit, 1);
                            Spp.insertOne(panierProduit);
                        } else {

                            System.out.println("current Pannier Produit is not empty");
                            int currentQte = currentPanierProduit.get(0).getQuantite();
                            if (currentQte + 1 >= produit.getQuantity()) {
                                System.out.println("You reach the max qte for that product");
                                errorPanierProduit.setText("Error: You cannot add this item to your cart. The quantity you're trying to add (" + produit.getQuantity() + ") is less than or equal to the current quantity (" + (currentQte + 1) + ").");
                                return;
                            }
                            PanierProduit panierProduit = new PanierProduit(panier, produit, currentQte + 1);
                            Spp.updateOne(panierProduit);
                        }

                        nbrPannier.setText(String.valueOf(getNbrProductInPannier(user.getId())));
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                });

                xOffset += xIncrement;
                if ((i + 1) % 3 == 0) {
                    xOffset = 0;
                    yOffset += yIncrement;
                }

                anchorPane.getChildren().add(productPane);
            }



            return anchorPane;
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


    }









    }

