package esprit.manettek.demo.Service;


import esprit.manettek.demo.Entities.Panier;
import esprit.manettek.demo.Utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePanier implements CRUD<Panier>{
    private Connection cnx ;

    public ServicePanier() {
        cnx = DBConnection.getInstance().getCnx();;
    }

    @Override
    public void insertOne(Panier panier) throws SQLException {
        String req = "INSERT INTO `panier`(`id_user`) VALUES (?)";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setInt(1, panier.getId_user().getId());
        st.executeUpdate();
        System.out.println("Panier Added !");
    }

    @Override
    public void updateOne(Panier panier) throws SQLException {
        String req = "UPDATE `panier` SET `id_user` = ? WHERE `id` = ?";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setInt(1, panier.getId_user().getId());
        st.setInt(2, panier.getId());
        st.executeUpdate();
        System.out.println("Panier Updated !");
    }

    @Override
    public void deleteOne(Panier panier) throws SQLException {
        String req = "DELETE FROM `panier` WHERE `id` = ?";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setInt(1, panier.getId());
        st.executeUpdate();
        System.out.println("Panier Deleted !");
    }

    @Override
    public List<Panier> selectAll() throws SQLException {
        List<Panier> paniers = new ArrayList<>();
        String req = "SELECT * FROM `panier`";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int id = rs.getInt("id");
            int id_user = rs.getInt("id_user");
            UserService serviceUser = new UserService();
            paniers.add(new Panier(id, serviceUser.selectById(id_user)));
        }
        return paniers;
    }

    public Panier selectById(int id) throws SQLException {
        String req = "SELECT * FROM `panier` WHERE `id` = ?";
        PreparedStatement st = cnx.prepareStatement(req);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            int id_user = rs.getInt("id_user");
            UserService serviceUser = new UserService();
            return new Panier(id, serviceUser.selectById(id_user));
        }
        return null;
    }

    public List<Panier> selectByUserId(int id_user) throws SQLException {
        List<Panier> paniers = new ArrayList<>();
        String req = "SELECT * FROM `panier` WHERE `id_user` = ?";
        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setInt(1, id_user);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    UserService serviceUser = new UserService();
                    paniers.add(new Panier(id, serviceUser.selectById(id_user)));
                }
            }
        }
        return paniers;
    }

}
