package esprit.manettek.demo.Entities;

import javafx.scene.control.Button;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class User {
    private int id;
    private String nom;
    private String roles;
    private String password;
    private String email;
    private String prenom;
    private boolean is_verified;
    private String address;
    private Date date_naissance;


    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", roles='" + roles + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", prenom='" + prenom + '\'' +
                ", is_verified=" + is_verified +
                ", address='" + address + '\'' +
                ", date_naissance=" + date_naissance +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public boolean isIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(Date date_naissance) {
        this.date_naissance = date_naissance;
    }

    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }
}
