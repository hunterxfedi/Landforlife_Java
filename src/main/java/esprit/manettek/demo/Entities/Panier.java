package esprit.manettek.demo.Entities;

public class Panier {
    private int id;
    private User id_user;

    public Panier() {
    }

    public Panier(int id, User id_user) {
        this.id = id;
        this.id_user = id_user;
    }

    public Panier(User id_user) {
        this.id_user = id_user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getId_user() {
        return id_user;
    }

    public void setId_user(User id_user) {
        this.id_user = id_user;
    }
}
