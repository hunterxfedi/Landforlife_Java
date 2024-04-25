package esprit.manettek.demo.Entities;

import java.util.Objects;

public class Produit {
    private int id;
    private String nom;
    private int priu;
    private String image;
    private String statut ;
    private int quantity;

    public Produit(int id, String nom, int priu, String image, String statut, int quantity) {
        this.id = id;
        this.nom = nom;
        this.priu = priu;
        this.image = image;
        this.statut = statut;
        this.quantity = quantity;
    }

    public Produit(String nom, int priu, String image, String statut, int quantity) {
        this.nom = nom;
        this.priu = priu;
        this.image = image;
        this.statut = statut;
        this.quantity = quantity;
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

    public int getPriu() {
        return priu;
    }

    public void setPriu(int priu) {
        this.priu = priu;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produit produit)) return false;
        return getId() == produit.getId() && getPriu() == produit.getPriu() && getQuantity() == produit.getQuantity() && Objects.equals(getNom(), produit.getNom()) && Objects.equals(getImage(), produit.getImage()) && Objects.equals(getStatut(), produit.getStatut());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNom(), getPriu(), getImage(), getStatut(), getQuantity());
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", priu=" + priu +
                ", image='" + image + '\'' +
                ", statut='" + statut + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
