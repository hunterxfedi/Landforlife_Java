package esprit.manettek.demo.Service;

import esprit.manettek.demo.Entities.ResetPassword;
import esprit.manettek.demo.Entities.User;
import esprit.manettek.demo.Interfaces.IResetPasswordService;
import esprit.manettek.demo.Utils.ConnectionManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class ResetPasswordService implements IResetPasswordService {


    @Override
    public void create(ResetPassword entity) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO resetPassword (email, code, date) VALUES (?, ?, CURRENT_TIMESTAMP)")) {

            preparedStatement.setString(1, entity.getUser().getEmail());
            preparedStatement.setInt(2, entity.getCode());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating reset password: " + e.getMessage());
        }
    }
    @Override
    public ResetPassword get(User user) {
        ResetPassword resetPassword = null;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM resetPassword WHERE email = ? ORDER BY date DESC LIMIT 1")) {

            preparedStatement.setString(1, user.getEmail());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                resetPassword = new ResetPassword();
                resetPassword.setUser(user);
                resetPassword.setCode(resultSet.getInt("code"));
                resetPassword.setDateCreation(resultSet.getTimestamp("date"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reset password: " + e.getMessage());
        }
        System.out.println(resetPassword.toString());
        return resetPassword;
    }

    @Override
    public void ResetPassword(User user) {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        // Implement the logic to insert a new user into the database
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE  user SET   password = ? WHERE email = ?")) {

            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setString(2, user.getEmail());

            preparedStatement.executeUpdate();
            System.out.println("password updated successfully");
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
    }
}
