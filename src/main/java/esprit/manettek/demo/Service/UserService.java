package esprit.manettek.demo.Service;

import esprit.manettek.demo.Entities.User;
import esprit.manettek.demo.Interfaces.IService;
import esprit.manettek.demo.Utils.ConnectionManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService implements IService<User> {

    private User authenticatedUser; // Store authenticated user here
    private static UserService instance;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    @Override
    public void create(User entity) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO animals (name, espece, age, sexe, description, image) VALUES (?, ?, ?, ?, ?, ?)")) {

            preparedStatement.setString(1, entity.getNom());
            preparedStatement.setString(2, entity.getRoles());
            preparedStatement.setString(3, entity.getPassword());
            preparedStatement.setString(4, entity.getEmail());
            preparedStatement.setString(5, entity.getPrenom());
            preparedStatement.setBoolean(6, entity.isIs_verified());
            preparedStatement.setString(7, entity.getAddress());
            preparedStatement.setDate(8, entity.getDate_naissance());

            preparedStatement.executeUpdate();
            System.out.println("User created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public void update(User entity) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE animals SET name = ?, espece = ?, age = ?, sexe = ?, description = ?, image = ? WHERE id = ?")) {

            preparedStatement.setString(1, entity.getNom());
            preparedStatement.setString(2, entity.getRoles());
            preparedStatement.setString(3, entity.getPassword());
            preparedStatement.setString(4, entity.getEmail());
            preparedStatement.setString(5, entity.getPrenom());
            preparedStatement.setBoolean(6, entity.isIs_verified());
            preparedStatement.setString(7, entity.getAddress());
            preparedStatement.setDate(8, entity.getDate_naissance());
            preparedStatement.setInt(9, entity.getId());

            preparedStatement.executeUpdate();
            System.out.println("User updated successfully");
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAll() {
        List<User> userList = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM user")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String roles = resultSet.getString("roles");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String prenom = resultSet.getString("prenom");
                boolean is_verified = resultSet.getBoolean("is_verified");
                String address = resultSet.getString("address");
                Date date_naissance = resultSet.getDate("date_naissance");

                User user = new User();
                user.setId(id);
                user.setNom(nom);
                user.setRoles(roles);
                user.setPassword(password);
                user.setEmail(email);
                user.setPrenom(prenom);
                user.setIs_verified(is_verified);
                user.setAddress(address);
                user.setDate_naissance(date_naissance);

                userList.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return userList;
    }

    @Override
    public User selectById(int idUser) {
        User user = null;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE id = ?")) {

            preparedStatement.setInt(1, idUser);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setRoles(resultSet.getString("roles"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setIs_verified(resultSet.getBoolean("is_verified"));
                    user.setAddress(resultSet.getString("address"));
                    user.setDate_naissance(resultSet.getDate("date_naissance"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error selecting user by ID: " + e.getMessage());
        }
        return user;
    }



    @Override
    public void delete(User entity) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM user WHERE id = ?")) {

            preparedStatement.setInt(1, entity.getId());

            preparedStatement.executeUpdate();
            System.out.println("User deleted successfully");
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    public boolean signUpUser(User user) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean success = false;
        String query = "INSERT INTO user (nom, roles, password, email, prenom, is_verified, address, date_naissance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = ConnectionManager.getConnection();
            preparedStatement = conn.prepareStatement("SELECT email FROM user WHERE email = ?");
            preparedStatement.setString(1, user.getEmail());
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, user.getNom());
                preparedStatement.setString(2, "[\"ROLE_USER\"]");
                preparedStatement.setString(3, hashedPassword);
                preparedStatement.setString(4, user.getEmail());
                preparedStatement.setString(5, user.getPrenom());
                preparedStatement.setBoolean(6, user.isIs_verified());
                preparedStatement.setString(7, user.getAddress());
                preparedStatement.setDate(8, user.getDate_naissance());

                preparedStatement.executeUpdate();
                success = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public User authenticate(String email, String password) {
        User user = null;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            conn = ConnectionManager.getConnection();
            String query = "SELECT * FROM user WHERE email = ?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, email);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String hashedPasswordFromDB = resultSet.getString("password");
                if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setRoles(resultSet.getString("roles"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setIs_verified(resultSet.getBoolean("is_verified"));
                    user.setAddress(resultSet.getString("address"));
                    user.setDate_naissance(resultSet.getDate("date_naissance"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        authenticatedUser = user;
        return user;
    }

    public boolean forgetPassUser(String email, String password) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean res = false;

        try {
            Connection conn = ConnectionManager.getConnection();
            preparedStatement = conn.prepareStatement("SELECT password, email FROM user WHERE email = ?");
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("User not found in the database!!");
                res = false;
            } else {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                String query = "UPDATE user SET password = ? WHERE email = ?";
                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, hashedPassword);
                preparedStatement.setString(2, email);
                preparedStatement.executeUpdate();
                res = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public boolean isEmailExist(String email) {
        boolean emailExists = false;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT email FROM user WHERE email = ?")) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                emailExists = resultSet.next(); // If there's a result, email exists
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return emailExists;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }
}
