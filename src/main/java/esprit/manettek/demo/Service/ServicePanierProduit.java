package esprit.manettek.demo.Service;

import esprit.manettek.demo.Entities.PanierProduit;
import esprit.manettek.demo.Utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePanierProduit implements CRUD<PanierProduit>{
    private Connection cnx ;

    public ServicePanierProduit() {
        cnx = DBConnection.getInstance().getCnx();;
    }

    @Override
    public void insertOne(PanierProduit panier_produit) throws SQLException {
        String req = "INSERT INTO `panier_produit`(`panier_id`, `produit_id`, `quantite`) VALUES (?, ?, ?)";

        // Using PreparedStatement to safely handle parameters
        PreparedStatement pst = cnx.prepareStatement(req);
        pst.setInt(1, panier_produit.getPanier_id().getId()); // Assuming panier_id is a String, adjust accordingly
        pst.setInt(2, panier_produit.getProduit_id().getId()); // Assuming produit_id is an integer, adjust accordingly
        pst.setInt(3, panier_produit.getQuantite()); // Assuming quantite is an integer, adjust accordingly

        pst.executeUpdate();
        System.out.println("Panier Produit Added !");

        // Close PreparedStatement
        pst.close();
    }

    @Override
    public void updateOne(PanierProduit panier_produit) throws SQLException {
        String req = "UPDATE `panier_produit` SET `quantite` = ? WHERE `panier_id` = ? AND `produit_id` = ?";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setInt(1, panier_produit.getQuantite());
        st.setInt(2, panier_produit.getPanier_id().getId());
        st.setInt(3, panier_produit.getProduit_id().getId());
        st.executeUpdate();
        System.out.println("Panier Produit Updated !");
    }

    @Override
    public void deleteOne(PanierProduit panier_produit) throws SQLException {
        String req = "DELETE FROM `panier_produit` WHERE `panier_id` = ? AND `produit_id` = ? ";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setInt(1, panier_produit.getPanier_id().getId());
        st.setInt(2, panier_produit.getProduit_id().getId());
        st.executeUpdate();
        System.out.println("Panier Produit Deleted !");
    }

    @Override
    public List<PanierProduit> selectAll() throws SQLException {
        List<PanierProduit> panierProduits = new ArrayList<>();
        String req = "SELECT * FROM `panier_produit`";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int panier_id = rs.getInt("panier_id");
            int produit_id = rs.getInt("produit_id");
            int quantite = rs.getInt("quantite");
            ServiceProduit serviceProduit = new ServiceProduit();
            ServicePanier servicePanier = new ServicePanier();
            panierProduits.add(new PanierProduit(servicePanier.selectById(panier_id), serviceProduit.selectById(produit_id),quantite));
        }
        return panierProduits;
    }
    public List<PanierProduit> selectByPanierProduitId(int panier_id,int produit_id) throws SQLException {
        List<PanierProduit> panierProduits = new ArrayList<>();
        String req = "SELECT * FROM `panier_produit` WHERE `panier_id` = ? AND `produit_id` = ?";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setInt(1, panier_id);
        st.setInt(2, produit_id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            int panier_id_data = rs.getInt("panier_id");
            int produit_id_data = rs.getInt("produit_id");
            int quantite_data = rs.getInt("quantite");
            ServiceProduit serviceProduit = new ServiceProduit();
            ServicePanier servicePanier = new ServicePanier();
            panierProduits.add(new PanierProduit(servicePanier.selectById(panier_id_data), serviceProduit.selectById(produit_id_data),quantite_data));
        }
        return panierProduits;
    }

    public List<PanierProduit> selectByPanierId(int panier_id) throws SQLException {
        List<PanierProduit> panierProduits = new ArrayList<>();
        String req = "SELECT * FROM `panier_produit` WHERE `panier_id` = ? ";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setInt(1, panier_id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            int panier_id_data = rs.getInt("panier_id");
            int produit_id_data = rs.getInt("produit_id");
            int quantite_data = rs.getInt("quantite");
            ServiceProduit serviceProduit = new ServiceProduit();
            ServicePanier servicePanier = new ServicePanier();
            panierProduits.add(new PanierProduit(servicePanier.selectById(panier_id_data), serviceProduit.selectById(produit_id_data),quantite_data));        }
        return panierProduits;
    }

}
