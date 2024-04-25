package esprit.manettek.demo.Entities;

public class PanierProduit {
    private Panier panier_id;
    private Produit produit_id;
    private int quantite;

    public PanierProduit() {
    }

    public PanierProduit(Panier panier_id, Produit produit_id, int quantite) {
        this.panier_id = panier_id;
        this.produit_id = produit_id;
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "PanierProduit{" +
                "panier_id=" + panier_id +
                ", produit_id=" + produit_id +
                ", quantite=" + quantite +
                '}';
    }

    public Panier getPanier_id() {
        return panier_id;
    }

    public void setPanier_id(Panier panier_id) {
        this.panier_id = panier_id;
    }

    public Produit getProduit_id() {
        return produit_id;
    }

    public void setProduit_id(Produit produit_id) {
        this.produit_id = produit_id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}

