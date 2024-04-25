package esprit.manettek.demo.Service;



import esprit.manettek.demo.Entities.Produit;
import esprit.manettek.demo.Utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ServiceProduit implements CRUD<Produit> {
    private Connection cnx;

    public ServiceProduit() {
        cnx = DBConnection.getInstance().getCnx();
    }

    @Override
    public void insertOne(Produit produit) throws SQLException {
        String req = "INSERT INTO `produit`(`id`,`nom`, `priu`, `statut`, `quantity`,`image`) VALUES (  ?,?, ?, ?, ?,?)";
        PreparedStatement pst = cnx.prepareStatement(req);

        pst.setInt(1, produit.getId());
        pst.setString(2, produit.getNom()); // Assuming nom is a String, adjust accordingly
        pst.setInt(3, produit.getPriu());
        pst.setString(4, produit.getStatut());// Assuming prix is a double, adjust accordingly
        pst.setInt(5, produit.getQuantity()); // Assuming qte is an integer, adjust accordingly
        pst.setString(6, produit.getImage()); // Assuming description is a String, adjust accordingly

        pst.executeUpdate();
        System.out.println("Produit Added !");

        // Close PreparedStatement
        pst.close();



    }

    @Override
    public void updateOne(Produit produit) throws SQLException {
        String req = "UPDATE `produit` SET `nom` = ? ,`priu` = ? ,`quantity`= ? ,`statut`= ?,`image`= ? WHERE `id` = ?";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setString(1, produit.getNom()); // Assuming nom is a String, adjust accordingly
        st.setInt(2, produit.getPriu());
        st.setInt(3, produit.getQuantity());
        st.setString(4, produit.getStatut());// Assuming prix is a double, adjust accordingly
        st.setString(5, produit.getImage());
        st.setInt(6, produit.getId());
        st.executeUpdate();
        System.out.println("Produit Updated !");


    }

    @Override
    public void deleteOne(Produit produit) throws SQLException {
        String req = "DELETE FROM `produit` WHERE `id` = ?";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setInt(1, produit.getId());
        st.executeUpdate();
        System.out.println("Produit Deleted !");


    }

    @Override
    public List<Produit> selectAll() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT * FROM `produit`";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            int priu = rs.getInt("priu");
            String statut = rs.getString("statut");
            int quantity = rs.getInt("quantity");
            String image = rs.getString("image");
            produits.add(new Produit(id,nom,priu,image, statut,quantity));
        }
        return produits;
    }
    public Produit selectById(int id) throws SQLException {
        String req = "SELECT * FROM `produit` WHERE `id` = ?";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {

            String nom = rs.getString("nom");
            int priu = rs.getInt("priu");
            String statut = rs.getString("statut");
            int quantity = rs.getInt("quantity");
            String image = rs.getString("image");
            return new Produit(id,nom,priu,image, statut,quantity);
        }

        return null;
    }
    public List<Produit> searchBy(String filterBy, String search) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT * FROM `produit` WHERE `"+filterBy+"` LIKE '%"+search+"%'";
        System.out.println(req);
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {

            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            int priu = rs.getInt("priu");
            String statut = rs.getString("statut");
            int quantity = rs.getInt("quantity");
            String image = rs.getString("image");

            produits.add(new Produit(id,nom,priu,statut, statut,quantity));
        }
        return produits;
    }


}

